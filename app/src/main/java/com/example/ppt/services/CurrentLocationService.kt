package com.example.ppt.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.*

/**
 * Servizio per ottenere la posizione attuale dell'utente
 */

class CurrentLocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient // Client per ottenere le informazioni sulla posizione
    private lateinit var locationRequest: LocationRequest // Richiesta di aggiornamenti sulla posizione
    private lateinit var locationCallback: LocationCallback // Callback per gestire gli aggiornamenti della posizione
    private lateinit var handlerThread: HandlerThread // Thread per gestire le operazioni di background
    private lateinit var serviceHandler: Handler // Handler per eseguire operazioni nel thread di background

    override fun onCreate() {
        super.onCreate()
        // Inizializza il client per la posizione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configura la richiesta di aggiornamenti sulla posizione
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        )
            .setMinUpdateIntervalMillis(5000)
            .build()

        // Definisce la callback per gestire gli aggiornamenti della posizione
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return

                // Itera su tutte le posizioni ottenute e le logga
                for (location in locationResult.locations) {
                    val currentLocation = "${location.latitude}, ${location.longitude}"
                    Log.d("CurrentLocationService", "Current location: $currentLocation")
                }
            }
        }

        // Crea e avvia un thread per gestire le operazioni in background
        handlerThread = HandlerThread("LocationHandlerThread").apply {
            start()
        }
        serviceHandler = Handler(handlerThread.looper)

        // Avvia gli aggiornamenti sulla posizione
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // Richiede aggiornamenti sulla posizione al client
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, serviceHandler.looper)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Rimuove gli aggiornamenti sulla posizione e termina il thread
        fusedLocationClient.removeLocationUpdates(locationCallback)
        handlerThread.quitSafely() // Termina il thread in modo sicuro
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}