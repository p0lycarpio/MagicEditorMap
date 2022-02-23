package com.polycarpio.magiceditormap.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.polycarpio.magiceditormap.R
import com.polycarpio.magiceditormap.databinding.ModalBottomSheetAddpointBinding
import com.polycarpio.magiceditormap.models.TypePoint

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
        binding.autoCompleteTextView.setAdapter(adapter)
        binding.fieldLat.text = "Latitude : $lat"
        binding.fieldLong.text = "Longitude : $long"

        // TODO poster le nouveau point sur l'API lors de l'ajout
        // Récupérer les valeurs saisies

        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}