package com.example.ppt.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ppt.R
import com.example.ppt.activities.MainActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

/**
 *  BroadcastReceiver per gestire eventi di geofence
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "geofence_channel"
        private const val NOTIFICATION_ID = 12345
        private const val PREFS_NAME = "GeofencePrefs"
        private const val KEY_ENTER_TIME = "enter_time"
        private const val KEY_GEOFENCE_NAME = "GeofenceName"
    }

    // Metodo chiamato quando il broadcast viene ricevuto
    override fun onReceive(context: Context?, intent: Intent) {
        // Estrae l'evento di geofence dall'intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        val geofenceTransition = geofencingEvent?.geofenceTransition

        // Controlla se la transizione è di tipo ENTER o EXIT
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Ottiene le preferenze condivise
            val sharedPreferences = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            // Ottiene il nome del geofence dalle preferenze condivise
            val geofenceName = sharedPreferences?.getString(KEY_GEOFENCE_NAME, "Unknown Geofence")?.replace("_", " ")

            // Gestisce la transizione di ingresso in un geofence
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.d("GEOFENCE", "Entered geofence: $geofenceName")
                // Memorizza l'ora di ingresso nelle preferenze condivise
                val enterTime = System.currentTimeMillis()
                sharedPreferences?.edit()?.putLong(KEY_ENTER_TIME, enterTime)?.apply()

                sendNotification(context, "Entered $geofenceName")
            }

            // Gestisce la transizione di uscita da un geofence
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.d("GEOFENCE", "Left geofence: $geofenceName")
                // Calcola la durata della permanenza
                val enterTime = sharedPreferences?.getLong(KEY_ENTER_TIME, -1)
                if (enterTime != null && enterTime != -1L) {
                    val duration = System.currentTimeMillis() - enterTime
                    val durationMinutes = (duration / 1000 / 60).toString()

                    sendNotification(context, "Left $geofenceName. Duration: $durationMinutes minutes")
                } else {
                    sendNotification(context, "Left $geofenceName")
                }
            }
        } else {
            // Logga un errore se il tipo di transizione è non valido
            Log.e("GEOFENCE", "Invalid transition type: $geofenceTransition")
        }
    }

    // Metodo per inviare una notifica
    private fun sendNotification(context: Context?, message: String) {
        if (context == null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Geofence Channel"
            val descriptionText = "Channel for geofence notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.home_icon)
            .setContentTitle("GeoTracking Event")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Crea un PendingIntent per aprire MainActivity quando la notifica viene cliccata
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        notificationBuilder.setContentIntent(pendingIntent)

        // Invia la notifica
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}