package com.example.ppt.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.R
import com.example.ppt.other.ActivityViewModelFactory
import com.example.ppt.other.GeofenceHelper
import com.example.ppt.services.AutoRecognitionService
import com.example.ppt.services.DrivingActivityService
import com.example.ppt.services.CurrentLocationService
import com.example.ppt.services.SittingActivityService
import com.example.ppt.services.UnknownActivityService
import com.example.ppt.services.WalkingActivityService
import com.example.ppt.viewModels.GoalViewModel
import com.example.ppt.viewModels.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.UUID

class SettingsFragment : Fragment() {

    private lateinit var autoRecognitionSwitch: SwitchCompat
    private lateinit var goalInput: NumberPicker
    private lateinit var saveButton: Button
    private lateinit var saveGeofenceButton: Button
    private lateinit var goalViewModel: GoalViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private lateinit var geofenceSwitch: SwitchCompat
    private lateinit var geofenceName: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflata il layout per il frammento
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inizializza il ViewModel per la gestione degli obiettivi
        val application = requireActivity().application
        val factory = ActivityViewModelFactory(application)
        goalViewModel = ViewModelProvider(this, factory)[GoalViewModel::class.java]

        // Inizializza i componenti dell'interfaccia utente
        goalInput = view.findViewById(R.id.dailyGoal_NumberPicker)
        saveButton = view.findViewById(R.id.saveDailyGoal_button)
        autoRecognitionSwitch = view.findViewById(R.id.AutomaticTracker_switch)
        saveGeofenceButton = view.findViewById(R.id.saveGeoFence_button)
        geofenceSwitch = view.findViewById(R.id.Geofence_switch)
        geofenceName = view.findViewById(R.id.GeofenceName)

        // Imposta i valori minimi e massimi per il NumberPicker
        goalInput.minValue = 0
        goalInput.maxValue = 100000

        // Client per la localizzazione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentLocation()

        // Osserva il cambiamento dell'obiettivo giornaliero e aggiorna l'interfaccia
        goalViewModel.dailyGoal.observe(viewLifecycleOwner) { goalValue ->
            goalInput.value = goalValue.toInt()
        }

        // Imposta il listener per il pulsante di salvataggio dell'obiettivo giornaliero
        saveButton.setOnClickListener {
            val newGoal = goalInput.value
            goalViewModel.updateDailyGoal(newGoal.toFloat())
            sharedViewModel.updateDailyGoal(newGoal.toFloat())
            Toast.makeText(
                requireContext(),
                "Daily goal updated to $newGoal steps",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Imposta il listener per il pulsante di salvataggio del geofence
        saveGeofenceButton.setOnClickListener {
            saveGeofence()
        }

        // Imposta il listener per il cambiamento dello stato dello switch di auto-riconoscimento
        autoRecognitionSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkLocationPermission()
                checkActivityPermissionAndStartAutoRecognition()
            } else {
                stopAutoRecognitionService()
            }
        }

        // Imposta il listener per il cambiamento dello stato dello switch del geofence
        geofenceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startGeofenceNotificationService()
            } else {
                stopGeofenceNotificationService()
            }
        }

        // Recupera e visualizza il geofence salvato se esiste
        val sharedPreferences =
            requireContext().getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE)
        val geofenceLat = sharedPreferences.getString("GeofenceLat", null)
        val geofenceLng = sharedPreferences.getString("GeofenceLng", null)
        if (geofenceLat != null && geofenceLng != null) {
            Log.d("SettingsFragment", "Saved geofence at: $geofenceLat, $geofenceLng")
        }

        checkBackgroundLocationPermission()
        return view
    }

    // Avvia il servizio di notifica geofence
    private fun startGeofenceNotificationService() {
        val intent = Intent(requireContext(), CurrentLocationService::class.java)
        requireContext().startService(intent)
        Log.d("SettingsFragment", "GeofenceNotificationService started")
    }

    // Arresta il servizio di notifica geofence
    private fun stopGeofenceNotificationService() {
        val intent = Intent(requireContext(), CurrentLocationService::class.java)
        requireContext().stopService(intent)
        Log.d("SettingsFragment", "GeofenceNotificationService stopped")
    }

    // Ottiene la posizione corrente del dispositivo
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(requireActivity()) { location ->
                    if (location != null) {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        Log.d("SettingsFragment", "Current location: $currentLocation")
                    } else {
                        Log.e("SettingsFragment", "Current location is null")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("SettingsFragment", "Error getting last location", e)
                }
        } else {
            // Richiede il permesso di localizzazione se non già concesso
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Launcher per la richiesta del permesso di localizzazione
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    // Controlla il permesso di riconoscimento delle attività e avvia il servizio di auto-riconoscimento
    private fun checkActivityPermissionAndStartAutoRecognition() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startAutoRecognitionService()
            }

            else -> {
                stepsRequestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    // Controlla il permesso di localizzazione
    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("permission", "Location permission granted")
            }

            else -> {
                stepsRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Controlla il permesso di localizzazione in background
    private fun checkBackgroundLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("permission", "Background location permission granted")
            }

            else -> {
                stepsRequestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    // Avvia il servizio di auto-riconoscimento
    private fun startAutoRecognitionService() {
        stopServices()
        sharedViewModel.resetActivities()
        sharedViewModel.setAutoRecognitionActive(true)

        val intent = Intent(requireContext(), AutoRecognitionService::class.java)
        requireContext().startService(intent)
    }

    // Arresta il servizio di auto-riconoscimento e avvia il servizio per attività sconosciute
    private fun stopAutoRecognitionService() {
        val intent = Intent(requireContext(), AutoRecognitionService::class.java)
        requireContext().stopService(intent)
        sharedViewModel.setAutoRecognitionActive(false)
        val intentUnknownActivity = Intent(requireContext(), UnknownActivityService::class.java)
        requireContext().startService(intentUnknownActivity)
    }

    // Launcher per la richiesta del permesso di riconoscimento delle attività
    private val stepsRequestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startAutoRecognitionService()
        } else {
            autoRecognitionSwitch.isChecked = false
            Toast.makeText(requireContext(), "Activity permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Arresta tutti i servizi di riconoscimento attività
    private fun stopServices() {
        stopService(WalkingActivityService::class.java)
        stopService(DrivingActivityService::class.java)
        stopService(SittingActivityService::class.java)
        stopService(UnknownActivityService::class.java)
    }

    // Arresta un servizio specificato dalla classe
    private fun stopService(serviceClass: Class<*>) {
        val intent = Intent(requireContext(), serviceClass)
        requireContext().stopService(intent)
    }

    // Salva un geofence basato sulla posizione corrente e sul nome fornito
    private fun saveGeofence() {
        if (currentLocation != null) {
            val geofenceNameInput = geofenceName.text.toString().trim()
            if (geofenceNameInput.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a name for the area of interest",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val geofenceId = geofenceNameInput.replace(" ", "_")

            val geofenceHelper = GeofenceHelper(requireContext())

            val geofence = Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(currentLocation!!.latitude, currentLocation!!.longitude, 500f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()

            geofenceHelper.addGeofence(geofence)

            val sharedPreferences =
                requireContext().getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("GeofenceLat", currentLocation!!.latitude.toString())
                putString("GeofenceLng", currentLocation!!.longitude.toString())
                putString("GeofenceName", geofenceId)
                apply()
            }

            val message =
                "Geofence saved at: ${currentLocation!!.latitude}, ${currentLocation!!.longitude} with name: $geofenceId"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Current location not available", Toast.LENGTH_SHORT)
                .show()
        }
    }
}