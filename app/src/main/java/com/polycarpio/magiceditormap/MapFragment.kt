package com.polycarpio.magiceditormap

import android.Manifest
import android.Manifest.*
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.polycarpio.magiceditormap.components.AddPointModalBottomSheet
import com.polycarpio.magiceditormap.databinding.MapFragmentBinding
import com.polycarpio.magiceditormap.models.MarkerPoint
import com.polycarpio.magiceditormap.service.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MapFragment : Fragment(), OnMapClickListener {

    private var _binding: MapFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView

    // Localisation values
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    // Points/marker functions
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(permission.ACCESS_FINE_LOCATION, false) -> {
                    Toast.makeText(activity, "Localisation...", Toast.LENGTH_LONG).show()
                }
                permissions.getOrDefault(permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(activity, "Localisation...", Toast.LENGTH_LONG).show()
                }
            }
        }

        if ((shouldShowRequestPermissionRationale(permission.ACCESS_COARSE_LOCATION) || shouldShowRequestPermissionRationale(
                permission.ACCESS_FINE_LOCATION
            ))
        ) {
            activity?.let {
                AlertDialog.Builder(it)
                    .setTitle("Localisation inactive")
                    .setMessage("Êtes-vous sûr de ne pas vouloir activer la localisation ?")
                    .setPositiveButton("Choisir") { y, _ ->
                        y.dismiss()
                        requestPermission()
                    }
                    .create()
                    .show()
            }

        } else {
            requestPermission()
        }
    }

    // Permissions
    private fun requestPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = MapFragmentBinding.inflate(inflater, container, false)

        mapView = binding.mapView
        // Récupère le nom de la carte sélectionnée
        val name = (activity as MainActivity).currentMap
        val newMap = (activity as MainActivity).newMap

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Carte " + name

        var mapStyle = Style.MAPBOX_STREETS
        val nightModeFlags =
            context!!.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> mapStyle = Style.DARK
        }

        // Appel à l'API pour les points
        if (!newMap) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response = ApiClient.apiService.getGameById(name)

                    if (response.isSuccessful && response.body() != null) {
                        val points = response.body()!!
                        (activity as MainActivity).points = points
                        displayMap(mapStyle, points)
                    }
                } catch (e: Exception) {
                    displayMap(mapStyle)
                }
            }
        } else {
            displayMap(mapStyle)
            (activity as MainActivity).newMap = false
        }
        return binding.root
    }


    private fun displayMap(mapStyle: String, points: MutableList<MarkerPoint> = arrayListOf()) {
        mapView.getMapboxMap().apply {
            loadStyleUri(mapStyle) {
                initLocationComponent()
                setupGesturesListener()
                addOnMapClickListener(this@MapFragment)

                println(points)
                if (points.size >= 1) {
                    // Défini la position de la caméra sur le premier point
                    setCamera(
                        CameraOptions.Builder()
                            .zoom(13.0)
                            .center(Point.fromLngLat(points[0].longitude, points[0].latitude))
                            .build()
                    )

                    for (element in points) {
                        println(element)
                        addAnnotationToMap(element.longitude, element.latitude)
                    }
                }
            }
        }
    }

    override fun onMapClick(point: Point): Boolean {
        val modalBottomSheet = AddPointModalBottomSheet(point.latitude(), point.longitude())

        addAnnotationToMap(point.longitude(), point.latitude())
        modalBottomSheet.show(
            (activity as MainActivity).supportFragmentManager,
            AddPointModalBottomSheet.TAG
        )
        return true
    }

    private fun onCameraTrackingDismissed() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = this@MapFragment.context?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.mapbox_user_icon,
                    )
                },
                shadowImage = this@MapFragment.context?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.mapbox_user_stroke_icon,
                    )
                },
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(10.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
}
