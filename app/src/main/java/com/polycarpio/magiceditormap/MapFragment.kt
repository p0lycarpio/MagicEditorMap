package com.polycarpio.magiceditormap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.maps.Style
import androidx.fragment.app.Fragment
import com.polycarpio.magiceditormap.databinding.MapFragmentBinding
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.polycarpio.magiceditormap.models.MarkerPoint

class MapFragment : Fragment() {

    private var _binding: MapFragmentBinding? = null
    private var points: MutableList<MarkerPoint> = arrayListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MapFragmentBinding.inflate(inflater, container, false)

        val name = (activity as MainActivity)?.currentMap
        Log.i("MSG", "Carte: " + name)


        GlobalScope.launch {
            points = ApiClient.apiService.getGameById(name).body()!!
        }
        var sumLat = 0.0
        var sumLong = 0.0

        for ( point in points) {
            sumLat += point.latitude
            sumLong += point.longitude
        }

        val moyLat = sumLat / points.size
        val moyLong = sumLong / points.size


        //TODO définir la position de la caméra sur les marqueurs

/*
        val cameraPosition = CameraOptions.Builder()
            .zoom(8.0)
            .center(Point.fromLngLat(moyLong, moyLat))
            .build()
*/

        val map = binding.mapView?.getMapboxMap()

        map.loadStyleUri(Style.MAPBOX_STREETS,
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {

                    for ( element in points) {
                        addAnnotationToMap(element.longitude , element.latitude)
                    }
                }

                private fun addAnnotationToMap(long:Double, lat:Double) {
                    // Create an instance of the Annotation API and get the PointAnnotationManager.
                    bitmapFromDrawableRes(
                        (activity as MainActivity),
                        R.drawable.red_marker
                    )?.let {
                        val annotationApi = binding.mapView?.annotations
                        val pointAnnotationManager =
                            annotationApi.createPointAnnotationManager()
// Set options for the resulting symbol layer.
                        val pointAnnotationOptions: PointAnnotationOptions =
                            PointAnnotationOptions()
// Define a geographic coordinate.
                                .withPoint(Point.fromLngLat(long,lat ))
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
                        pointAnnotationManager?.create(pointAnnotationOptions)
                    }
                }


                private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
                    convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

                private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
                    if (sourceDrawable == null) {
                        return null
                    }
                    return if (sourceDrawable is BitmapDrawable) {
                        sourceDrawable.bitmap
                    } else {
// copying drawable object to not manipulate on the same reference
                        val constantState = sourceDrawable.constantState ?: return null
                        val drawable = constantState.newDrawable().mutate()
                        val bitmap: Bitmap = Bitmap.createBitmap(
                            drawable.intrinsicWidth, drawable.intrinsicHeight,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                        bitmap
                    }
                }
            }
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}