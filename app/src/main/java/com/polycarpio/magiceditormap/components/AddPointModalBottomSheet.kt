package com.polycarpio.magiceditormap.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.polycarpio.magiceditormap.R
import com.polycarpio.magiceditormap.databinding.ModalBottomSheetAddpointBinding
import com.polycarpio.magiceditormap.models.TypePoint

class AddPointModalBottomSheet : BottomSheetDialogFragment() {
    private var _binding: ModalBottomSheetAddpointBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ModalBottomSheetAddpointBinding.inflate(inflater, container, false)

        val items = enumValues<TypePoint>()
        val adapter = ArrayAdapter(requireContext(), R.layout.point_type_list_item, items)
        binding.autoCompleteTextView.setAdapter(adapter)

        //TODO poster le nouveau point sur l'API au clic

        return binding.root
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}