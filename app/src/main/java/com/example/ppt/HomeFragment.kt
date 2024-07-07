package com.example.ppt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ppt.Service.DrivingActivityService

class HomeFragment : Fragment() {

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

    private val locationRequestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startDrivingActivity()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.Q)
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
               startWalkingActivity()
            }
        }

        stopWalkingButton.setOnClickListener {
            if (isWalking) {
                stopWalkingActivity()
            }
        }

        startDrivingButton.setOnClickListener {
            if (!doingActivity) {
               startDrivingActivity()
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

    private fun checkLocationPermissionAndStartDrivingActivity() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startDrivingActivity()
            }
            else -> {
                locationRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
}