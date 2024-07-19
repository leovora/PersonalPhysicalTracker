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
import com.example.ppt.services.CurrentLocationService
import com.example.ppt.services.DrivingActivityService
import com.example.ppt.services.SittingActivityService
import com.example.ppt.services.UnknownActivityService
import com.example.ppt.services.WalkingActivityService
import com.example.ppt.viewModels.GoalViewModel
import com.example.ppt.viewModels.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

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
    private lateinit var resetGeofences: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val application = requireActivity().application
        val factory = ActivityViewModelFactory(application)
        goalViewModel = ViewModelProvider(this, factory)[GoalViewModel::class.java]

        goalInput = view.findViewById(R.id.dailyGoal_NumberPicker)
        saveButton = view.findViewById(R.id.saveDailyGoal_button)
        autoRecognitionSwitch = view.findViewById(R.id.AutomaticTracker_switch)
        saveGeofenceButton = view.findViewById(R.id.saveGeoFence_button)
        geofenceSwitch = view.findViewById(R.id.Geofence_switch)
        geofenceName = view.findViewById(R.id.GeofenceName)
        resetGeofences = view.findViewById(R.id.resetGeofence_button)

        goalInput.minValue = 0
        goalInput.maxValue = 100000

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentLocation()

        goalViewModel.dailyGoal.observe(viewLifecycleOwner) { goalValue ->
            goalInput.value = goalValue.toInt()
        }

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

        saveGeofenceButton.setOnClickListener {
            saveGeofence()
        }

        resetGeofences.setOnClickListener {
            resetGeofences()
        }

        autoRecognitionSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkLocationPermission()
                checkActivityPermissionAndStartAutoRecognition()
            } else {
                stopAutoRecognitionService()
            }
        }

        geofenceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startGeofenceNotificationService()
            } else {
                stopGeofenceNotificationService()
            }
        }

        checkBackgroundLocationPermission()
        return view
    }

    private fun startGeofenceNotificationService() {
        val intent = Intent(requireContext(), CurrentLocationService::class.java)
        requireContext().startService(intent)
        Log.d("SettingsFragment", "GeofenceNotificationService started")
    }

    private fun stopGeofenceNotificationService() {
        val intent = Intent(requireContext(), CurrentLocationService::class.java)
        requireContext().stopService(intent)
        Log.d("SettingsFragment", "GeofenceNotificationService stopped")
    }

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
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

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

    private fun startAutoRecognitionService() {
        stopServices()
        sharedViewModel.resetActivities()
        sharedViewModel.setAutoRecognitionActive(true)

        val intent = Intent(requireContext(), AutoRecognitionService::class.java)
        requireContext().startService(intent)
    }

    private fun stopAutoRecognitionService() {
        stopServices()
        val intent = Intent(requireContext(), AutoRecognitionService::class.java)
        requireContext().stopService(intent)
        sharedViewModel.setAutoRecognitionActive(false)
        val intentUnknownActivity = Intent(requireContext(), UnknownActivityService::class.java)
        requireContext().startService(intentUnknownActivity)
    }

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

    private fun stopServices() {
        stopService(WalkingActivityService::class.java)
        stopService(DrivingActivityService::class.java)
        stopService(SittingActivityService::class.java)
        stopService(UnknownActivityService::class.java)
    }

    private fun stopService(serviceClass: Class<*>) {
        val intent = Intent(requireContext(), serviceClass)
        requireContext().stopService(intent)
    }

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
                .setCircularRegion(currentLocation!!.latitude, currentLocation!!.longitude, 300f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()

            geofenceHelper.addOrUpdateGeofence(geofence)

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

    private fun resetGeofences() {
        val geofenceHelper = GeofenceHelper(requireContext())
        geofenceHelper.removeGeofences()
    }
}