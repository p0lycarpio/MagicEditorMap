package com.polycarpio.magiceditormap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.polycarpio.magiceditormap.databinding.MapFragmentBinding
import com.polycarpio.magiceditormap.models.MarkerPoint
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private var _binding: MapFragmentBinding? = null
    private var points: MutableList<MarkerPoint> = arrayListOf()

    private val binding get() = _binding!!

    private fun addAnnotationToMap(long: Double, lat: Double) {
        // Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            (activity as MainActivity),
            R.drawable.red_marker
        )?.let {
            val annotationApi = binding.mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions =
                PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(long, lat))
                    .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    // Conversion du marker.png
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MapFragmentBinding.inflate(inflater, container, false)

        val mapView = binding.mapView.getMapboxMap()
        // Récupère le nom de la carte sélectionnée
        val name = (activity as MainActivity).currentMap


        // Appel à l'API pour les points
        GlobalScope.launch(Dispatchers.Main) {
            points = ApiClient.apiService.getGameById(name).body()!!


            // Définit la position de la caméra sur le premier point
            val cameraPosition = CameraOptions.Builder()
                .zoom(13.0)
                .center(Point.fromLngLat(points[0].longitude, points[0].latitude))
                .build()

            mapView.loadStyleUri(
                Style.MAPBOX_STREETS
            ) {
                mapView.setCamera(cameraPosition)

                for (element in points) {
                    addAnnotationToMap(element.longitude, element.latitude)
                }
            }
        }
        return binding.root
    }
}