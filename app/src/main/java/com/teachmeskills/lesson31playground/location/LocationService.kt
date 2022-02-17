package com.teachmeskills.lesson31playground.location

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.teachmeskills.lesson31playground.MainActivity
import com.teachmeskills.lesson31playground.R
import com.teachmeskills.lesson31playground.checkLocationPermission

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationListeners = mutableSetOf<LocationListener>()
    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations.isEmpty()) {
                return
            }

            val location = locationResult.locations.last()
            locationListeners.forEach {
                it.onLocationChange(location)
            }
        }
    }

    fun addListener(listener: LocationListener) {
        locationListeners.add(listener)
    }

    fun removeListener(listener: LocationListener) {
        locationListeners.remove(listener)
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return LocationBinder(this)
    }

    fun startLocationUpdates(locationRequest: LocationRequest) {
        if (checkLocationPermission()) {

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification())
            }
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
    }

    private fun createNotification(): Notification {

        val pIntent = PendingIntent.getActivity(
            this,
            CONTENT_REQUEST_CODE,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("User location in progress")
            .setContentText("detecting location")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_baseline_audiotrack)
            .setAutoCancel(true)
            .setContentIntent(pIntent)
            .build()
        createChannel()
        return notification
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Audio channel"
            val descriptionText = "Audio channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        private const val EXTRA_KEY = "key"
        private const val EXTRA_PLAY = "play"
        private const val EXTRA_PAUSE = "pause"
        private const val NOTIFICATION_ID = 1
        private const val CONTENT_REQUEST_CODE = 2
        private const val CHANNEL_ID = "player"
    }

}


interface LocationListener {
    fun onLocationChange(location: Location)
}

class LocationBinder(
    val service: LocationService
) : Binder()