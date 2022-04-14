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
import com.polycarpio.magiceditormap.components.AddMapModalBottomSheet
import com.polycarpio.magiceditormap.components.AddPointModalBottomSheet
import com.polycarpio.magiceditormap.components.RemoveMapDialog
import com.polycarpio.magiceditormap.databinding.FragmentFirstBinding
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MapListFragment : Fragment() {

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
        var list = (activity as MainActivity).mapList


        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (!list.isEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        binding.listview.adapter =
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                list
                            )
                    }
                } else {

                    val cartes = ApiClient.apiService.getGameList()
                    if (cartes.isSuccessful && cartes.body() != null) {
                        list = cartes.body()!!
                        Handler(Looper.getMainLooper()).post {
                            binding.listview.adapter =
                                ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    list
                                )
                        }
                    } else {
                        binding.errortitle.text = "Erreur de l'API"
                        binding.errormsg.text =
                            "Il n'y a pas de données ou l'API n'est pas en ligne"
                    }
                }
            } catch (e: Exception) {
                binding.errortitle.text = "Erreur de connexion"
                binding.errormsg.text = "Vérifiez votre connexion internet"

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
            val modalBottomSheet = AddMapModalBottomSheet()
            modalBottomSheet.show(
                (activity as MainActivity).supportFragmentManager,
                AddPointModalBottomSheet.TAG
            )
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
