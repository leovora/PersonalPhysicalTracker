package com.example.ppt.other

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ppt.receivers.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

/**
 * Classe helper per la gestione dei geofences
 */

class GeofenceHelper(private val context: Context) {

    // Client per la gestione dei geofences
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    // Metodo per aggiungere un geofence
    @SuppressLint("MissingPermission")
    fun addGeofence(geofence: Geofence) {
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        // Crea un Intent per il GeofenceBroadcastReceiver
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // Aggiunge il geofence tramite il GeofencingClient
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d("GeofenceHelper", "Geofence added successfully")
            }
            .addOnFailureListener {
                Log.e("GeofenceHelper", "Failed to add geofence: ${it.message}")
            }
    }
}