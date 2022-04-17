package com.polycarpio.magiceditormap.components

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.polycarpio.magiceditormap.MainActivity
import com.polycarpio.magiceditormap.R
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.app.Activity




class RemoveMapDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.message_delete_map) + " " + (activity as MainActivity).currentMap + " ?")
            .setNegativeButton("Annuler") { _, _ ->
                this.dismiss()
                Toast.makeText(activity, "Annulé", Toast.LENGTH_LONG).show()
            }
            .setPositiveButton("Oui") { _, _ ->
                val fActivity = activity

                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val del = (activity as MainActivity).currentMap.toString()
                        val response = ApiClient.apiService.deleteGameById(del)
                        Log.i("MSG", response.toString())
                        if (response.isSuccessful) {
                            Toast.makeText(fActivity, "Carte supprimée", Toast.LENGTH_LONG).show()
                            (activity as MainActivity).mapList.remove(del)
                            this@RemoveMapDialog.dismiss()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(fActivity, "Erreur de suppression", Toast.LENGTH_LONG).show()
                        Log.i("MSG", e.message.toString())
                    }
                }
            }
            .show()

    companion object {
        const val TAG = "RemoveMapDialog"
    }
}

