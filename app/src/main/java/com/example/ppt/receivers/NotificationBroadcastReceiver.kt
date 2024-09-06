package com.example.ppt.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.ppt.activities.MainActivity
import com.example.ppt.R

/**
 *  BroadcastReceiver per gestire la visualizzazione delle notifiche
 */

class NotificationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "NotificationChannel"
        private const val NOTIFICATION_ID = 27
    }

    // Metodo chiamato quando il broadcast viene ricevuto
    override fun onReceive(context: Context, intent: Intent?) {
        // Crea un canale di notifica se necessario
        createNotificationChannel(context)

        // Ottiene il NotificationManager per gestire le notifiche
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crea un Intent per aprire MainActivity quando la notifica viene cliccata
        val notificationIntent = Intent(context, MainActivity::class.java)
        // Crea un PendingIntent per avviare l'Intent
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        // Costruisce la notifica
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icn)
            .setContentTitle("Have you done some activity today?")
            .setContentText("Come check your daily stats")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    // Metodo per creare un canale di notifica su Android Oreo e versioni superiori
    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NotificationChannel"
            val descriptionText = "Channel for activity reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}