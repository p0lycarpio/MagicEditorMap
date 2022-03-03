package com.polycarpio.magiceditormap.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
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

        // TODO poster le nouveau point sur l'API lors de l'ajout

        binding.bottomsheetButton.setOnClickListener {
            // Récupérer les valeurs saisies
            val pointName = binding.pointNameField.text.toString()
            val pointType: TypePoint = TypePoint.valueOf(binding.pointTypeField.text.toString())

            val name = (activity as MainActivity).currentMap

            val newPoint = MarkerPoint(pointName, lat, long, pointType)
            (activity as MainActivity).points.add(newPoint)

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response = ApiClient.apiService.postGameById(name, (activity as MainActivity).points)
                    Log.i("MSG", response.toString())

                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Posté", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "Err- ", Toast.LENGTH_LONG).show()
                    Log.i("MSG", e.message.toString())
                    Log.i("MSG",  (activity as MainActivity).points.toString())
                }
            }


        }

        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}