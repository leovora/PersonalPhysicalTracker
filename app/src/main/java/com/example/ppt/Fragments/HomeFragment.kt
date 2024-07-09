package com.example.ppt.Fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ppt.R
import com.example.ppt.Services.DrivingActivityService
import com.example.ppt.Services.TrackingService
import com.example.ppt.Services.WalkingActivityService
import com.example.ppt.ViewModels.ActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: ActivityViewModel by viewModels()

    private lateinit var startWalkingButton: Button
    private lateinit var stopWalkingButton: Button
    private lateinit var startDrivingButton: Button
    private lateinit var stopDrivingButton: Button
    private lateinit var startSittingButton: Button
    private lateinit var stopSittingButton: Button

    private var doingActivity = false
    private var isWalking = false
    private var isDriving = false
    private var isSitting = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        startWalkingButton = view.findViewById(R.id.StartWalking_btn)
        stopWalkingButton = view.findViewById(R.id.StopWalking_btn)
        startDrivingButton = view.findViewById(R.id.StartDriving_btn)
        stopDrivingButton = view.findViewById(R.id.StopDriving_btn)
        startSittingButton = view.findViewById(R.id.StartSitting_btn)
        stopSittingButton = view.findViewById(R.id.StopSitting_btn)

        startWalkingButton.setOnClickListener {
            if (!doingActivity) {
                checkActivityPermissionAndStartWalkingActivity()
            }
        }

        stopWalkingButton.setOnClickListener {
            if (isWalking) {
                stopWalkingActivity()
            }
        }

        startDrivingButton.setOnClickListener {
            if (!doingActivity) {
                startLocationUpdates()
            }
        }

        stopDrivingButton.setOnClickListener {
            if (isDriving) {
                stopDrivingActivity()
            }
        }

        startSittingButton.setOnClickListener {
            if (!doingActivity) {
                startSittingActivity()
            }
        }

        stopSittingButton.setOnClickListener {
            if (isSitting) {
                stopSittingActivity()
            }
        }

        updateButtons()
        return view
    }

    private fun startWalkingActivity() {
        val intent = Intent(requireContext(), WalkingActivityService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        isWalking = true
        doingActivity = true
        startWalkingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        stopWalkingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        updateButtons()
    }

    private fun stopWalkingActivity() {
        val intent = Intent(requireContext(), WalkingActivityService::class.java)
        requireContext().stopService(intent)
        isWalking = false
        doingActivity = false
        stopWalkingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        updateButtons()
    }

    private fun startDrivingActivity() {
        val intent = Intent(requireContext(), DrivingActivityService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        isDriving = true
        doingActivity = true
        startDrivingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        stopDrivingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        updateButtons()
    }

    private fun stopDrivingActivity() {
        val intent = Intent(requireContext(), DrivingActivityService::class.java)
        requireContext().stopService(intent)
        isDriving = false
        doingActivity = false
        stopDrivingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        updateButtons()
    }

    private fun startSittingActivity() {
        // Implement the logic to start sitting activity
    }

    private fun stopSittingActivity() {
        // Implement the logic to stop sitting activity
    }

    private fun updateButtons() {
        startDrivingButton.isEnabled = !doingActivity
        startWalkingButton.isEnabled = !doingActivity
        startSittingButton.isEnabled = !doingActivity
    }

    private fun checkActivityPermissionAndStartWalkingActivity() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startWalkingActivity()
            }
            else -> {
                stepsRequestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    private val stepsRequestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startWalkingActivity()
        } else {
            Toast.makeText(requireContext(), "Activity permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startLocationUpdates()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                startLocationUpdates()
            } else -> {
            startLocationUpdates()
        }
        }
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }
        else{
            Toast.makeText(requireContext(), "Starting Activity", Toast.LENGTH_SHORT).show()
            //startDrivingActivity()
        }

    }
}