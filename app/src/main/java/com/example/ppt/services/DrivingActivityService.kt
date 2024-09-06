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
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.ppt.R
import com.example.ppt.activities.MainActivity
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Servizio per monitorare e gestire le attività di guida
 */

class DrivingActivityService : Service() {

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
        startForegroundService() // Avvia il servizio come foreground service
        handler = Handler(Looper.getMainLooper()) // Inizializza il handler
        notificationManager = getSystemService(NotificationManager::class.java) // Inizializza il NotificationManager
        notificationRunnable = Runnable {
            sendNotification()
        }
        handler.postDelayed(notificationRunnable, 60 * 60 * 1000) // 60 minuti di ritardo
    }

    // Configura il servizio per essere in esecuzione in foreground
    private fun startForegroundService() {
        val notificationChannelId = "DRIVING_ACTIVITY_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Driving Activity",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Driving Activity Service"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Driving Activity")
            .setSmallIcon(R.drawable.run_icon)
            .build()

        startForeground(1, notification)
    }

    // Viene chiamato quando il servizio viene avviato
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        return START_STICKY // Indica che il servizio deve essere riavviato se viene interrotto
    }

    // Viene chiamato quando il servizio viene distrutto
    override fun onDestroy() {
        saveActivityToDatabase() // Salva l'attività nel database
        super.onDestroy()
    }

    // Salva i dati dell'attività nel database
    private fun saveActivityToDatabase() {
        val activity = Activity(
            type = "Driving",
            startTimeMillis = startTime,
            endTimeMillis = System.currentTimeMillis() // Memorizza il tempo di fine dell'attività
        )

        // Utilizza CoroutineScope per eseguire l'inserimento nel database in background
        CoroutineScope(Dispatchers.IO).launch {
            db.getDao().insert(activity)
        }
    }

    // Invia una notifica dopo un'ora guida
    private fun sendNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, "DRIVING_ACTIVITY_CHANNEL")
            .setContentTitle("Time to Move!")
            .setContentText("You've been driving for an hour. Stop and take a walk!")
            .setSmallIcon(R.drawable.notification_icn)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification) // Mostra la notifica
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}