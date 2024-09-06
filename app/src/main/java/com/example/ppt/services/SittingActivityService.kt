package com.example.ppt.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.ppt.R
import com.example.ppt.activities.MainActivity
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Servizio per monitorare e gestire le attività di seduta
 */

class SittingActivityService : Service() {

    private var startTime: Long = 0
    private lateinit var notificationManager: NotificationManager

    //handler e runnable che servono per mandare una notifica posticipata
    private lateinit var handler: Handler
    private lateinit var notificationRunnable: Runnable

    // Lazy initialization del database delle attività
    private val db by lazy {
        ActivityDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService() // Avvia il servizio in foreground
        handler = Handler(Looper.getMainLooper())
        // Crea un Runnable per inviare una notifica
        notificationRunnable = Runnable {
            sendNotification()
        }
        // Posticipa l'esecuzione del Runnable di 30 minuti
        handler.postDelayed(notificationRunnable, 30 * 60 * 1000)
    }

    /// Configura il servizio per essere in esecuzione in foreground
    private fun startForegroundService() {
        notificationManager = getSystemService(NotificationManager::class.java)

        val notificationChannelId = "SITTING_ACTIVITY_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Sitting Activity",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Sitting Activity Service"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Sitting Activity")
            .setSmallIcon(R.drawable.run_icon)
            .build()

        startForeground(1, notification)
    }

    // Viene chiamato quando il servizio viene avviato
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        return START_STICKY // Indica che il servizio deve essere riavviato se viene interrotto
    }

    override fun onDestroy() {
        handler.removeCallbacks(notificationRunnable) // Rimuove il Runnable per evitare chiamate future
        saveActivityToDatabase()
        super.onDestroy()
    }

    // Salva l'attività nel database
    private fun saveActivityToDatabase() {
        val activity = Activity(
            type = "Sitting",
            startTimeMillis = startTime,
            endTimeMillis = System.currentTimeMillis()
        )

        // Utilizza CoroutineScope per eseguire l'inserimento nel database in background
        CoroutineScope(Dispatchers.IO).launch {
            db.getDao().insert(activity)
        }
    }

    // Invia una notifica per ricordare all'utente di muoversi
    private fun sendNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, "SITTING_ACTIVITY_CHANNEL")
            .setContentTitle("Time to Move!")
            .setContentText("You've been sitting for 30 minutes. Stand up and take a walk!")
            .setSmallIcon(R.drawable.notification_icn)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}