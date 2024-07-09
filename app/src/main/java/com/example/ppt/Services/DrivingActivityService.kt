package com.example.ppt.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.ppt.R
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DrivingActivityService : Service(){

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
        startTime = SystemClock.elapsedRealtime()
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