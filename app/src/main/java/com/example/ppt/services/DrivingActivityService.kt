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


class DrivingActivityService : Service(){

    private var startTime: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var handler: Handler
    private lateinit var notificationRunnable: Runnable


    private val db by lazy {
        ActivityDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        handler = Handler(Looper.getMainLooper())
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationRunnable = Runnable {
            sendNotification()
        }
        handler.postDelayed(notificationRunnable, 60 * 60 * 1000 ) // 60 minutes delay
    }

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        return START_STICKY
    }

    override fun onDestroy() {
        saveActivityToDatabase()
        super.onDestroy()
    }

    private fun saveActivityToDatabase() {
        val activity = Activity(
            type = "Driving",
            startTimeMillis = startTime,
            endTimeMillis = System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.getDao().insert(activity)
        }
    }

    private fun sendNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, "DRIVING_ACTIVITY_CHANNEL")
            .setContentTitle("Time to Move!")
            .setContentText("You've been driving for an hour. Stop and take a walk!")
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