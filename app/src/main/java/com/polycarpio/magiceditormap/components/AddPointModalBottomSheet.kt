package com.polycarpio.magiceditormap.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.polycarpio.magiceditormap.MainActivity
import com.polycarpio.magiceditormap.R
import com.polycarpio.magiceditormap.databinding.ModalBottomSheetAddpointBinding
import com.polycarpio.magiceditormap.models.MarkerPoint
import com.polycarpio.magiceditormap.models.TypePoint
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AddPointModalBottomSheet(latitude: Double, longitude: Double) : BottomSheetDialogFragment() {
    private var _binding: ModalBottomSheetAddpointBinding? = null
    private val binding get() = _binding!!

    private var lat = latitude
    private var long = longitude

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetAddpointBinding.inflate(inflater, container, false)

        // Passer les données à la Modale
        val items = enumValues<TypePoint>()
        val adapter = ArrayAdapter(requireContext(), R.layout.point_type_list_item, items)
        binding.pointTypeField.setAdapter(adapter)
        binding.fieldLat.text = "Latitude : $lat"
        binding.fieldLong.text = "Longitude : $long"

        binding.bottomsheetButton.setOnClickListener {
            // Récupérer les valeurs saisies
            val pointName = binding.pointNameField.text.toString()
            var pointType: TypePoint = TypePoint.NULL

            if (binding.pointTypeField.text.isNotEmpty()) {
                pointType = TypePoint.valueOf(binding.pointTypeField.text.toString())
            }

            val name = (activity as MainActivity).currentMap

            val newPoint = MarkerPoint(pointName, lat, long, pointType)
            (activity as MainActivity).points.add(newPoint)

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response = ApiClient.apiService.postGameById(name, (activity as MainActivity).points)
                    Log.i("MSG", response.toString())

                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Point ajouté", Toast.LENGTH_LONG).show()
                        this@AddPointModalBottomSheet.dismiss()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "Erreur lors de l'ajout du point", Toast.LENGTH_LONG).show()
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