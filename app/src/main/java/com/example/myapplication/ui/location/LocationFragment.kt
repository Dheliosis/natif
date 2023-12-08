package com.example.myapplication.ui.location

import android.Manifest
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLocationBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import java.lang.Exception

class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mMap : GoogleMap

    // Map
    private var mGoogleMap: GoogleMap? = null

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Map
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity().baseContext)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,false)
                        || permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(this.requireActivity().baseContext, "Location access granted", Toast.LENGTH_SHORT).show()

                    if (isLocationEnabled()) {
                        val result = fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            CancellationTokenSource().token
                        )
                        result.addOnCompleteListener {
                            val location =
                                "Latitude ${it.result.latitude}, Longitude ${it.result.longitude}"
                            binding.locationData.text = location
                            addUserLocationMarker(LatLng(it.result.latitude, it.result.longitude))
                        }
                    } else {
                        Toast.makeText(this.requireActivity().baseContext, "Please turn ON the location", Toast.LENGTH_SHORT)
                            .show()
                        createLocationRequest()
                    }
                } else -> {
                    Toast.makeText(this.requireActivity().baseContext, "No location access", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.buttonGetLocation.setOnClickListener {
            println("Get user location")
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        addMarker(LatLng(0.0,0.0))
        addDraggableMarker(LatLng(10.0,10.0))

        mGoogleMap?.setOnMapClickListener {
            addMarker(it)
        }

        mGoogleMap?.setOnMapLongClickListener {
            addDraggableMarker(it)
        }

        mGoogleMap?.setOnMarkerClickListener {
            it.remove()
            false
        }


    }

    private fun addMarker(position: LatLng): Marker {
        println("Add fix marker")
        val marker = mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Fix marker")
        )

        return marker!!
    }

    private fun addDraggableMarker(position: LatLng): Marker {
        println("Add draggable marker")
        val marker = mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Draggable marker")
            .draggable(true)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
        )

        return marker!!
    }

    private fun addUserLocationMarker(position: LatLng): Marker {
        val marker = mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("You are here !")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location_marker)))

        return marker!!
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).setMinUpdateIntervalMillis(5000).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this.requireActivity().baseContext)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
        }

        task.addOnFailureListener { e ->
            if( e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this.requireActivity(),
                        100
                    )
                } catch (sendEx: java.lang.Exception) {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}