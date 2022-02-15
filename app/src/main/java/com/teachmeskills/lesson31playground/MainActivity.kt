package com.teachmeskills.lesson31playground

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

    private var binder: LocationBinder? = null

    //    Callback для подключения к сервису
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder) {
//            Принимаем биндер из сервиса
            binder = p1 as LocationBinder
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }

    //    Пермишены на местоположение
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->


        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            startBackgroundLocationUpdates()
        }
    }

    //    Пермишены на определение местоположения в background
    private val backgroundLocationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->


        if (isGranted) {
            startBackgroundLocationUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Присоединяемся к сервису
        bindService(
            Intent(this, LocationService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.startLocationUpdates).setOnClickListener {
            startBackgroundLocationUpdates()
        }

        findViewById<Button>(R.id.stopLocationUpdates).setOnClickListener {
            binder?.service?.stopLocationUpdates()
        }


    }

    override fun onDestroy() {
//        Отсоединяемся от сервиса
        unbindService(serviceConnection)
        super.onDestroy()
    }

//    private fun getLastLocation() {
//        if (checkLocationPermission()) {
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    Log.i("ttt", "latitude=${location?.latitude} longitude=${location?.longitude}")
//                }
//        } else {
//            requestPermissions()
//        }
//    }

    private fun requestLocationPermissions() {
//        Запрашиваем разрегение на местоположение
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun requestBackgroundLocationPermission() {
//        Запрашиваем разрешение на местоположение в background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationPermissionRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }




    private fun startBackgroundLocationUpdates() {
        if (checkLocationPermission()) {
            if (checkBackgroundLocationPermission()) {
                val client: SettingsClient = LocationServices.getSettingsClient(this)
                val locationRequest = LocationRequest.create().apply {
                    interval = 10000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)

                val task: Task<LocationSettingsResponse> =
                    client.checkLocationSettings(builder.build())
                task.addOnSuccessListener {
                    binder?.service?.startLocationUpdates(locationRequest)
                }

                task.addOnFailureListener {
                    if (it is ResolvableApiException) {
                        it.startResolutionForResult(this, 1)
                    }
                }
            } else {
                requestBackgroundLocationPermission()
            }
        } else {
            requestLocationPermissions()
        }
    }

}