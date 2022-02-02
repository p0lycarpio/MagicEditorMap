package com.polycarpio.magiceditormap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import androidx.fragment.app.Fragment
import com.polycarpio.magiceditormap.databinding.FragmentFirstBinding
import com.polycarpio.magiceditormap.databinding.MapFragmentBinding

class MapFragment : Fragment() {

    private var _binding: MapFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MapFragmentBinding.inflate(inflater, container, false)

        binding.mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}