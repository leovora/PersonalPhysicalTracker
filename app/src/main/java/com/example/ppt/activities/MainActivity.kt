package com.example.ppt.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.ppt.R
import com.example.ppt.receivers.NotificationBroadcastReceiver
import com.example.ppt.services.AutoRecognitionService
import com.example.ppt.services.CurrentLocationService
import com.example.ppt.services.DrivingActivityService
import com.example.ppt.services.SittingActivityService
import com.example.ppt.services.UnknownActivityService
import com.example.ppt.services.WalkingActivityService
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    /**
     * Inizializza il layout, imposta il padding per adattarsi alle barre di sistema,
     * configura la navigazione con il BottomNavigationView e avvia il servizio "UnknownActivityService".
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Gestione del padding per adattarsi alle barre di sistema
        val rootLayout: View = findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemInsets.left,
                top = systemInsets.top,
                right = systemInsets.right,
                bottom = systemInsets.bottom
            )
            insets
        }

        // Configurazione della navigazione dei frammenti tramite BottomNavigationView
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.btm_nav)
        val navController = Navigation.findNavController(this, R.id.host_fragment)
        NavigationUI.setupWithNavController(bottomNavigation, navController)

        // Avvio del servizio "UnknownActivityService"
        val intent = Intent(this, UnknownActivityService::class.java)
        startService(intent)

        // Avvio dell'allarme periodico per inviare notifiche periodiche
        startReminderAlarm()
    }

    /**
     * Metodo startReminderAlarm: configura un allarme ricorrente che invia una notifica ogni 15 minuti.
     * L'allarme utilizza un PendingIntent per inviare un broadcast tramite il NotificationBroadcastReceiver.
     */
    private fun startReminderAlarm() {
        val intent = Intent(this, NotificationBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES
        // Configura un allarme che si ripete ogni 15 minuti
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent)
    }

    /**
     * Metodo stopServices: interrompe tutti i servizi di monitoraggio delle attività.
     * Viene chiamato al termine dell'attività per fermare i servizi in esecuzione.
     */
    private fun stopServices() {
        stopService(WalkingActivityService::class.java)
        stopService(DrivingActivityService::class.java)
        stopService(SittingActivityService::class.java)
        stopService(UnknownActivityService::class.java)
        stopService(AutoRecognitionService::class.java)
        stopService(CurrentLocationService::class.java)
    }

    /**
     * Metodo stopService: ferma un servizio specifico passato come parametro.
     * @param serviceClass La classe del servizio che deve essere fermato.
     */
    private fun stopService(serviceClass: Class<*>) {
        val intent = Intent(this, serviceClass)
        this.stopService(intent)
    }

    /**
     * Oltre a chiamare il metodo della superclasse, interrompe tutti i servizi attivi.
     */
    override fun onDestroy() {
        super.onDestroy()
        stopServices()
    }
}