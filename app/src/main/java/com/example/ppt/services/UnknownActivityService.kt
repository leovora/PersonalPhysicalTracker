package com.example.ppt.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.ppt.R
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Servizio per monitorare e gestire i periodi tra un'attività e l'altra
 */

class UnknownActivityService : Service() {

    private var startTime: Long = 0
    private lateinit var notificationManager: NotificationManager

    // Lazy initialization del database delle attività
    private val db by lazy {
        ActivityDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService() // Avvia il servizio in foreground
    }

    // Configura il canale di notifica e avvia il servizio in foreground
    private fun startForegroundService() {
        notificationManager = getSystemService(NotificationManager::class.java)

        val notificationChannelId = "UNKNOWN_ACTIVITY_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Unknown Activity",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Unknown Activity Service"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Unknown Activity")
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
        saveActivityToDatabase() // Salva l'attività nel database quando il servizio viene distrutto
        super.onDestroy()
    }

    // Salva l'attività nel database
    private fun saveActivityToDatabase() {
        val activity = Activity(
            type = "Unknown",
            startTimeMillis = startTime,
            endTimeMillis = System.currentTimeMillis()
        )

        // Utilizza CoroutineScope per eseguire l'inserimento nel database in background
        CoroutineScope(Dispatchers.IO).launch {
            db.getDao().insert(activity)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}