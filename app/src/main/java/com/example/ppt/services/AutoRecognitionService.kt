package com.example.ppt.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ppt.R
import com.google.android.gms.location.*
import com.example.ppt.receivers.ActivityTransitionReceiver
import kotlinx.coroutines.*

class AutoRecognitionService : Service() {

    private lateinit var pendingIntent: PendingIntent
    private lateinit var notificationManager: NotificationManager
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        Log.d("AutoRecognitionService", "Service created")
        startForegroundService()
        registerActivityTransitions()
    }

    private fun startForegroundService() {
        notificationManager = getSystemService(NotificationManager::class.java)

        val notificationChannelId = "AUTO_RECOGNITION_ACTIVITY_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Auto Recognition",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Auto Recognition Service"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Auto Recognition")
            .setSmallIcon(R.drawable.run_icon)
            .build()

        startForeground(1, notification)
    }

    @SuppressLint("MissingPermission")
    private fun registerActivityTransitions() {
        serviceScope.launch {
            val transitions = mutableListOf<ActivityTransition>()

            transitions += ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

            transitions += ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

            transitions += ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

            transitions += ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

            transitions += ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

            transitions += ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

            transitions += ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

            transitions += ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

            val request = ActivityTransitionRequest(transitions)

            val intent = Intent(this@AutoRecognitionService, ActivityTransitionReceiver::class.java).apply {
                action = "com.example.ppt.ACTION_PROCESS_ACTIVITY_TRANSITIONS"
            }
            pendingIntent = PendingIntent.getBroadcast(
                this@AutoRecognitionService,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val task = ActivityRecognition.getClient(this@AutoRecognitionService)
                .requestActivityTransitionUpdates(request, pendingIntent)

            task.addOnSuccessListener {
                Log.d("AutoRecognitionService", "Successfully registered for activity transitions")
            }

            task.addOnFailureListener { e: Exception ->
                Log.e("AutoRecognitionService", "Failed to register for activity transitions", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.launch {
            val task = ActivityRecognition.getClient(this@AutoRecognitionService)
                .removeActivityTransitionUpdates(pendingIntent)

            task.addOnSuccessListener {
                pendingIntent.cancel()
                Log.d("AutoRecognitionService", "Successfully removed activity transitions")
            }

            task.addOnFailureListener { e: Exception ->
                Log.e("AutoRecognitionService", "Failed to remove activity transitions", e)
            }
        }
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}