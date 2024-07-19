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

class GeofenceHelper(private val context: Context) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    @SuppressLint("MissingPermission")
    fun addOrUpdateGeofence(geofence: Geofence) {

        removeGeofence(geofence.requestId)

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d("GeofenceHelper", "Geofence added/updated successfully")
            }
            .addOnFailureListener {
                Log.e("GeofenceHelper", "Failed to add/update geofence: ${it.message}")
            }
    }

    fun removeGeofence(geofenceId: String) {
        geofencingClient.removeGeofences(listOf(geofenceId))
            .addOnSuccessListener {
                Log.d("GeofenceHelper", "Geofence removed successfully: $geofenceId")
            }
            .addOnFailureListener {
                Log.e("GeofenceHelper", "Failed to remove geofence: ${it.message}")
            }
    }

    fun removeGeofences() {
        val geofencingClient = LocationServices.getGeofencingClient(context)
        val pendingIntent = getGeofencePendingIntent()

        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                clearGeofencePrefs()
                Log.d("GeofenceHelper", "Geofences removed successfully")
            }
            .addOnFailureListener {
                Log.e("GeofenceHelper", "Failed to remove geofences: ${it.message}")
            }
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun clearGeofencePrefs() {
        val sharedPreferences = context.getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}