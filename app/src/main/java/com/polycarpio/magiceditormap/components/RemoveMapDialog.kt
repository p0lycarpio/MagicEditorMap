package com.polycarpio.magiceditormap.components

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.polycarpio.magiceditormap.MainActivity
import com.polycarpio.magiceditormap.R
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                        val response = ApiClient.apiService.deleteGameById((activity as MainActivity).currentMap)
                        Log.i("MSG", response.toString())
                        if (response.isSuccessful) {
                            Toast.makeText(fActivity, "Carte supprimée", Toast.LENGTH_LONG).show()
                            this@RemoveMapDialog.dismiss()
                            // TODO reload list
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