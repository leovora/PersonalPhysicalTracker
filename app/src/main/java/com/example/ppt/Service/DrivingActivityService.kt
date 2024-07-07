package com.example.ppt.Service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.ppt.R
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.entities.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrivingActivityService : Service(), LocationListener {

    private var startTime: Long = 0
    private var distanceTravelled = 0f

    private val db by lazy {
        ActivityDatabase.getDatabase(this)
    }

    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startLocationUpdates()
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
            .setContentText("Tracking your distance")
            .setSmallIcon(R.drawable.run_icon)
            .build()

        startForeground(1, notification)
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                updateInterval,
                0f,
                this
            )
        } else {
            Log.e("DrivingActivityService", "Location permission not granted")
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        return START_STICKY
    }

    override fun onLocationChanged(location: Location) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val previousLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            previousLocation?.let {
                distanceTravelled += it.distanceTo(location)
                Log.d("DrivingActivityService", "Distanza percorsa: $distanceTravelled metri")
            }
        } else {
            Log.e("DrivingActivityService", "Permission not granted for ACCESS_FINE_LOCATION")
        }
    }

    override fun onDestroy() {
        saveActivityToDatabase()
        locationManager.removeUpdates(this)
        showActivityToast()
        super.onDestroy()
    }

    private fun saveActivityToDatabase() {
        val activity = Activity(
            type = "Driving",
            startTimeMillis = startTime,
            endTimeMillis = System.currentTimeMillis(),
            kilometers = distanceTravelled,
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.dao.insert(activity)
        }
    }

    private fun showActivityToast() {
        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTime
        val minutes = (elapsedTime / 1000) / 60
        val distance = distanceTravelled / 1000
        val message = "Stopped activity: $minutes minutes, $distance km"
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    }