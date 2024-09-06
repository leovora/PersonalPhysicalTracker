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
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Servizio per monitorare e gestire le attività di camminata
 */

class WalkingActivityService : Service(), SensorEventListener {

    private var startTime: Long = 0
    private var stepsAtStart = 0
    private var stepsSinceStart = 0
    private val stepsLengthInMeters = 0.762f // Lunghezza del passo in metri

    // Lazy initialization del database delle attività
    private val db by lazy {
        ActivityDatabase.getDatabase(this)
    }

    // Lazy initialization del sensor manager
    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // Lazy initialization del sensore di conteggio dei passi
    private val stepCounterSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService() // Avvia il servizio in foreground
        registerStepCounterSensor() // Registra il listener per il sensore di conteggio dei passi
    }

    // Configura il canale di notifica e avvia il servizio in foreground
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

    // Registra il listener per il sensore di conteggio dei passi
    private fun registerStepCounterSensor() {
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: run {
            Log.e("WalkingActivityService", "Step counter sensor is not present on this device")
            startTime = System.currentTimeMillis() // Se il sensore non è presente, inizializza startTime
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        return START_STICKY // Indica che il servizio deve essere riavviato se viene interrotto
    }

    // Gestisce gli eventi del sensore
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (stepsAtStart == 0) {
                    stepsAtStart = it.values[0].toInt() // Imposta i passi iniziali al primo valore del sensore
                }
                stepsSinceStart = it.values[0].toInt() - stepsAtStart // Calcola i passi dall'inizio

                val elapsedTime = System.currentTimeMillis() - startTime // Tempo trascorso

                Log.d("WalkingActivityService", "Tempo trascorso: $elapsedTime ms, Passi: $stepsSinceStart")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Non fare nulla
    }

    override fun onDestroy() {
        saveActivityToDatabase() // Salva l'attività nel database quando il servizio viene distrutto
        sensorManager.unregisterListener(this) // Disiscrive il listener dal sensore
        super.onDestroy()
    }

    // Salva l'attività di camminata nel database
    private fun saveActivityToDatabase() {
        val activity = Activity(
            type = "Walking",
            startTimeMillis = startTime,
            endTimeMillis = System.currentTimeMillis(),
            stepsCount = stepsSinceStart,
            kilometers = stepsSinceStart * stepsLengthInMeters / 1000
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