package com.example.ppt.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.*

class CurrentLocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var handlerThread: HandlerThread
    private lateinit var serviceHandler: Handler

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000 // 10 seconds
        )
            .setMinUpdateIntervalMillis(5000) // 5 seconds
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val currentLocation = "${location.latitude}, ${location.longitude}"
                    Log.d("CurrentLocationService", "Current location: $currentLocation")
                }
            }
        }

        handlerThread = HandlerThread("LocationHandlerThread").apply {
            start()
        }
        serviceHandler = Handler(handlerThread.looper)

        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, serviceHandler.looper)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        handlerThread.quitSafely()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}