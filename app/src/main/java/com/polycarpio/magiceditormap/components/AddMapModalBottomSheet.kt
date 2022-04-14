package com.polycarpio.magiceditormap.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.polycarpio.magiceditormap.MainActivity
import com.polycarpio.magiceditormap.R
import com.polycarpio.magiceditormap.databinding.ModalBottomSheetAddmapBinding
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AddMapModalBottomSheet() : BottomSheetDialogFragment() {
    private var _binding: ModalBottomSheetAddmapBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetAddmapBinding.inflate(inflater, container, false)
        val navController = findNavController()

        binding.bottomsheetButton.setOnClickListener {
            // Récupérer les valeurs saisies
            val mapName = binding.mapNameField.text.toString()

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response = ApiClient.apiService.postGameById(mapName, arrayListOf())

                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Carte ajoutée", Toast.LENGTH_LONG).show()
                        (activity as MainActivity).mapList.add(mapName)
                        (activity as MainActivity).currentMap = mapName
                        (activity as MainActivity).newMap = true
                        navController.navigate(R.id.mapFragment)
                        this@AddMapModalBottomSheet.dismiss()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        activity,
                        "Erreur lors de l'ajout de la carte",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("MSG", e.message.toString())
                }
            }
        }
        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}