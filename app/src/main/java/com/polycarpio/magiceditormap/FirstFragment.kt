package com.polycarpio.magiceditormap

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.polycarpio.magiceditormap.components.RemoveMapDialog
import com.polycarpio.magiceditormap.databinding.FragmentFirstBinding
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val navController = findNavController()


        GlobalScope.launch {
            val cartes = ApiClient.apiService.getGameList().body()

            Handler(Looper.getMainLooper()).post {
                binding.listview.adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cartes!!)
            }
        }
        binding.listview.setOnItemClickListener { _, _, position, _ ->
            val item = binding.listview.adapter.getItem(position)
            (activity as MainActivity)?.currentMap = item as String
            navController.navigate(R.id.mapFragment)
        }

        binding.listview.setOnItemLongClickListener { _, _, position, _ ->
            val item = binding.listview.adapter.getItem(position)
            (activity as MainActivity)?.currentMap = item as String
            RemoveMapDialog().show(childFragmentManager, RemoveMapDialog.TAG)
            return@setOnItemLongClickListener true
        }

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

// override fun onDestroyView() {
//   super.onDestroyView()
//   _binding = null
//}
