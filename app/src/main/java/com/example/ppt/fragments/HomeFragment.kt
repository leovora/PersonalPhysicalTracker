package com.example.ppt.fragments

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ppt.R
import com.example.ppt.services.DrivingActivityService
import com.example.ppt.services.SittingActivityService
import com.example.ppt.services.WalkingActivityService
import com.example.ppt.viewModels.ActivityViewModel

class HomeFragment : Fragment() {

    private val viewModel : ActivityViewModel by viewModels()

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
                viewModel.startWalkingActivity()
            }
        }

        stopWalkingButton.setOnClickListener {
            if (isWalking) {
                stopWalkingActivity()
                viewModel.stopWalkingActivity()
            }
        }

        startDrivingButton.setOnClickListener {
            if (!doingActivity) {
                startDrivingActivity()
                viewModel.startDrivingActivity()
            }
        }

        stopDrivingButton.setOnClickListener {
            if (isDriving) {
                stopDrivingActivity()
                viewModel.stopDrivingActivity()
            }
        }

        startSittingButton.setOnClickListener {
            if (!doingActivity) {
                startSittingActivity()
                viewModel.startSittingActivity()
            }
        }

        stopSittingButton.setOnClickListener {
            if (isSitting) {
                stopSittingActivity()
                viewModel.stopSittingActivity()
            }
        }

        viewModel.isWalking.observe(viewLifecycleOwner) { isWalking ->
            stopWalkingButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isWalking) R.color.red else R.color.white
                )
            )
        }

        viewModel.isDriving.observe(viewLifecycleOwner) { isDriving ->
            stopDrivingButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isDriving) R.color.red else R.color.white
                )
            )
        }

        viewModel.isSitting.observe(viewLifecycleOwner) { isSitting ->
            stopSittingButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isSitting) R.color.red else R.color.white
                )
            )
        }

        updateButtons()
        return view
    }

    private fun startWalkingActivity() {
        val intent = Intent(requireContext(), WalkingActivityService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        isWalking = true
        doingActivity = true
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
        val intent = Intent(requireContext(), SittingActivityService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        isSitting = true
        doingActivity = true
        stopSittingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        updateButtons()
    }

    private fun stopSittingActivity() {
        val intent = Intent(requireContext(), SittingActivityService::class.java)
        requireContext().stopService(intent)
        isSitting = false
        doingActivity = false
        stopSittingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        updateButtons()
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
}