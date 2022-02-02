package com.polycarpio.magiceditormap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.polycarpio.magiceditormap.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val cartes = listOf(("Niort"), ("La Rochelle"))

        binding.listview.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cartes)

        // récupérer sur le fragment 2 requireActivity()
       binding.listview.onItemClickListener{parent, view, position, id ->
           val selectedItemText = parent.getItemAtPosition(position)
           Toast.makeText(this@FirstFragment, "Selected : $selectedItemText", Toast.LENGTH_LONG).show()
       }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // binding.buttonFirst.setOnClickListener {
        //   findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
       }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}