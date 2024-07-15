package com.example.ppt.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.example.ppt.services.AutoRecognitionService
import com.example.ppt.services.DrivingActivityService
import com.example.ppt.services.SittingActivityService
import com.example.ppt.services.UnknownActivityService
import com.example.ppt.services.WalkingActivityService
import com.example.ppt.viewModels.ActivityViewModel

class SettingsFragment : Fragment() {

    private lateinit var autoRecognitionSwitch: SwitchCompat
    private lateinit var goalInput: NumberPicker
    private lateinit var saveButton: Button
    private lateinit var activityViewModel: ActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val application = requireActivity().application
        val factory = ActivityViewModelFactory(application)
        activityViewModel = ViewModelProvider(this, factory)[ActivityViewModel::class.java]

        goalInput = view.findViewById(R.id.dailyGoal_NumberPicker)
        saveButton = view.findViewById(R.id.saveDailyGoal_button)
        autoRecognitionSwitch = view.findViewById(R.id.AutomaticTracker_switch)

        goalInput.minValue = 0
        goalInput.maxValue = 100000

        activityViewModel.dailyGoal.observe(viewLifecycleOwner) { goalValue ->
            goalInput.value = goalValue.toInt()
        }

        saveButton.setOnClickListener {
            val newGoal = goalInput.value
            activityViewModel.updateDailyGoal(newGoal.toFloat())
            Toast.makeText(requireContext(), "Daily goal updated to $newGoal steps", Toast.LENGTH_SHORT).show()
        }

        autoRecognitionSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkActivityPermissionAndStartAutoRecognition()
            } else {
                stopAutoRecognitionService()
            }
        }

        return view
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

    private fun startAutoRecognitionService() {
        stopServices()
        activityViewModel.resetActivities()

        val intent = Intent(requireContext(), AutoRecognitionService::class.java)
        requireContext().startService(intent)
        activityViewModel.setAutoRecognitionActive(true)
    }

    private fun stopAutoRecognitionService() {
        val intent = Intent(requireContext(), AutoRecognitionService::class.java)
        requireContext().stopService(intent)
        activityViewModel.setAutoRecognitionActive(false)
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
            Toast.makeText(requireContext(), "Activity permission denied", Toast.LENGTH_SHORT).show()
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
}