package com.polycarpio.magiceditormap

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
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

        binding.listview.setOnItemClickListener { parent, view, position, id ->
            var item = binding.listview.adapter.getItem(position)
            (activity as MainActivity)?.currentMap = item;
            navController.navigate(R.id.mapFragment)
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
