package com.teachmeskills.lesson31playground

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

fun Context.checkLocationPermission(): Boolean {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return false
    }
    return true
}

//    Проверяем есть ли разрешение на определение местоположения в background
fun Context.checkBackgroundLocationPermission(): Boolean {
//    Эта проверка нужна только для api больше 29
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        return true
    }
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return false
    }
    return true
}