package com.teachmeskills.lesson31playground.map

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.teachmeskills.lesson31playground.R
import com.teachmeskills.lesson31playground.base.BaseLocationActivity
import com.teachmeskills.lesson31playground.databinding.ActivityMapsBinding
import com.teachmeskills.lesson31playground.location.LocationListener

class MapsActivity : BaseLocationActivity(), OnMapReadyCallback, LocationListener {

    private var mMap: GoogleMap? = null
    private var currentLocation: Marker? = null
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    override fun onServiceConnected() {
        binder?.service?.addListener(this)
    }

    override fun onStop() {
        binder?.service?.removeListener(this)
        super.onStop()
    }

    override fun onLocationChange(location: Location) {
        Log.i("ttt", "latitude=${location.latitude} longitude=${location.longitude}")

        val newLocation = LatLng(location.latitude, location.longitude)
        mMap?.let { mMap ->

            currentLocation?.remove()
            currentLocation = mMap.addMarker(
                MarkerOptions()
                    .position(newLocation)
                    .rotation(0.5f)
                    .title("My location")
            )


            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
        }

    }


    companion object {
        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, MapsActivity::class.java)
        }
    }
}