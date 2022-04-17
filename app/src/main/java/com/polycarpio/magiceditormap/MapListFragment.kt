package com.polycarpio.magiceditormap

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.polycarpio.magiceditormap.components.AddMapModalBottomSheet
import com.polycarpio.magiceditormap.components.AddPointModalBottomSheet
import com.polycarpio.magiceditormap.components.RemoveMapDialog
import com.polycarpio.magiceditormap.databinding.FragmentMaplistBinding
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.MenuInflater

import android.view.Menu





/**
 * First fragment on app opening
 */
class MapListFragment : Fragment() {

    private var _binding: FragmentMaplistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMaplistBinding.inflate(inflater, container, false)

        val navController = findNavController()

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (!(activity as MainActivity).mapList.isEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        binding.listview.adapter =
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                (activity as MainActivity).mapList
                            )
                    }
                } else {

                    val cartes = ApiClient.apiService.getGameList()
                    if (cartes.isSuccessful && cartes.body() != null) {
                        (activity as MainActivity).mapList.addAll(cartes.body()!!)
                        Handler(Looper.getMainLooper()).post {
                            binding.listview.adapter =
                                ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    (activity as MainActivity).mapList
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
            // TODO reload list
            // RemoveMapDialog().onDismiss()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        const val TAG = "MapListFragment"
    }
}

