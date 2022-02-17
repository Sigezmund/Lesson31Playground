package com.teachmeskills.lesson31playground.base

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.teachmeskills.lesson31playground.location.LocationBinder
import com.teachmeskills.lesson31playground.location.LocationService

abstract class BaseLocationActivity : AppCompatActivity() {

    protected var binder: LocationBinder? = null

    //    Callback для подключения к сервису
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder) {
//            Принимаем биндер из сервиса
            binder = p1 as LocationBinder
            onServiceConnected()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }

    open fun onServiceConnected() {

    }


    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, LocationService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }


}