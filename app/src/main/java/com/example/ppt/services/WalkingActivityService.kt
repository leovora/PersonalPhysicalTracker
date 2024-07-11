package com.example.ppt.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ppt.R
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WalkingActivityService : Service(), SensorEventListener {

    private var startTime: Long = 0
    private var stepsAtStart = 0
    private var stepsSinceStart = 0
    private val stepsLengthInMeters = 0.762f

    private val db by lazy {
        ActivityDatabase.getDatabase(this)
    }

    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val stepCounterSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        registerStepCounterSensor()
    }

    private fun startForegroundService() {
        val notificationChannelId = "WALKING_ACTIVITY_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Walking Activity",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Walking Activity Service"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Walking Activity")
            .setContentText("Tracking your steps")
            .setSmallIcon(R.drawable.run_icon)
            .build()

        startForeground(1, notification)
    }

    private fun registerStepCounterSensor() {
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: run {
            Log.e("WalkingActivityService", "Step counter sensor is not present on this device")
            startTime = System.currentTimeMillis()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (stepsAtStart == 0) {
                    stepsAtStart = it.values[0].toInt()
                }
                stepsSinceStart = it.values[0].toInt() - stepsAtStart

                val elapsedTime = System.currentTimeMillis() - startTime

                Log.d("WalkingActivityService", "Tempo trascorso: $elapsedTime ms, Passi: $stepsSinceStart")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onDestroy() {
        saveActivityToDatabase()
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    private fun saveActivityToDatabase() {
        val activity = Activity(
            type = "Walking",
            startTimeMillis = startTime,
            endTimeMillis = System.currentTimeMillis(),
            stepsCount = stepsSinceStart,
            kilometers = stepsSinceStart * stepsLengthInMeters / 1000
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.getDao().insert(activity)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}