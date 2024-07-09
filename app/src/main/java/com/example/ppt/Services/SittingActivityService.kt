package com.example.ppt.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.ppt.R
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SittingActivityService : Service() {

    private var startTime: Long = 0

    private val db by lazy {
        ActivityDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    private fun startForegroundService() {
        val notificationChannelId = "DRIVING_ACTIVITY_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Sitting Activity",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Sitting Activity Service"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Sitting Activity")
            .setSmallIcon(R.drawable.run_icon)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = SystemClock.elapsedRealtime()
        return START_STICKY
    }

    override fun onDestroy() {
        saveActivityToDatabase()
        super.onDestroy()
    }

    private fun saveActivityToDatabase() {
        val activity = Activity(
            type = "Sitting",
            startTimeMillis = startTime,
            endTimeMillis = SystemClock.elapsedRealtime(),
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.getDao().insert(activity)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}