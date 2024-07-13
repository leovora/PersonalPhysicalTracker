package com.example.ppt.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.R
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.ActivityRepository
import com.example.ppt.other.StatsViewModelFactory
import com.example.ppt.services.DrivingActivityService
import com.example.ppt.services.SittingActivityService
import com.example.ppt.services.UnknownActivityService
import com.example.ppt.services.WalkingActivityService
import com.example.ppt.viewModels.ActivityViewModel
import com.example.ppt.viewModels.SharedViewModel
import com.example.ppt.viewModels.StatsViewModel

class HomeFragment : Fragment() {

    private val viewModel: ActivityViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var database: ActivityDatabase
    private lateinit var repository: ActivityRepository
    private lateinit var factory: StatsViewModelFactory
    private lateinit var statsViewmodel: StatsViewModel

    private lateinit var startWalkingButton: Button
    private lateinit var stopWalkingButton: Button
    private lateinit var startDrivingButton: Button
    private lateinit var stopDrivingButton: Button
    private lateinit var startSittingButton: Button
    private lateinit var stopSittingButton: Button
    private lateinit var walkingStepsText: TextView
    private lateinit var walkingMinsText: TextView
    private lateinit var drivingMinsText: TextView
    private lateinit var sittingMinsText: TextView

    private var doingActivity = false
    private var isWalking = false
    private var isDriving = false
    private var isSitting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        database = ActivityDatabase.getDatabase(requireContext())
        repository = ActivityRepository(database.getDao())
        factory = StatsViewModelFactory(repository)
        statsViewmodel = ViewModelProvider(this, factory).get(StatsViewModel::class.java)
    }

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
        walkingStepsText = view.findViewById(R.id.walking_steps_text)
        walkingMinsText = view.findViewById(R.id.walking_mins_text)
        drivingMinsText = view.findViewById(R.id.driving_mins_text)
        sittingMinsText = view.findViewById(R.id.sitting_mins_text)

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

        sharedViewModel.isAutoRecognitionActive.observe(viewLifecycleOwner) { isActive ->
            updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
        }

        viewModel.walkingSteps.observe(viewLifecycleOwner) { steps ->
            walkingStepsText.text = steps.toString()
        }

        viewModel.walkingMins.observe(viewLifecycleOwner) { mins ->
            walkingMinsText.text = (mins / 60000).toString()
        }

        viewModel.drivingMins.observe(viewLifecycleOwner) { mins ->
            drivingMinsText.text = (mins / 60000).toString()
        }

        viewModel.sittingMins.observe(viewLifecycleOwner) { mins ->
            sittingMinsText.text = (mins / 60000).toString()
        }

        updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
        updateDailyStats()
        return view
    }

    private fun startWalkingActivity() {
        stopUnknownActivityService()
        val intent = Intent(requireContext(), WalkingActivityService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        isWalking = true
        doingActivity = true
        stopWalkingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
    }

    private fun stopWalkingActivity() {
        val intent = Intent(requireContext(), WalkingActivityService::class.java)
        requireContext().stopService(intent)
        isWalking = false
        doingActivity = false
        stopWalkingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        startUnknownActivityService()
        updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
        updateDailyStats()
    }

    private fun startDrivingActivity() {
        stopUnknownActivityService()
        val intent = Intent(requireContext(), DrivingActivityService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        isDriving = true
        doingActivity = true
        stopDrivingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
    }

    private fun stopDrivingActivity() {
        val intent = Intent(requireContext(), DrivingActivityService::class.java)
        requireContext().stopService(intent)
        isDriving = false
        doingActivity = false
        stopDrivingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        startUnknownActivityService()
        updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
        updateDailyStats()
    }

    private fun startSittingActivity() {
        stopUnknownActivityService()
        val intent = Intent(requireContext(), SittingActivityService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        isSitting = true
        doingActivity = true
        stopSittingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
    }

    private fun stopSittingActivity() {
        val intent = Intent(requireContext(), SittingActivityService::class.java)
        requireContext().stopService(intent)
        isSitting = false
        doingActivity = false
        stopSittingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        startUnknownActivityService()
        updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
        updateDailyStats()
    }

    private fun updateButtons(autoRecognitionActive: Boolean) {
        startDrivingButton.isEnabled = !autoRecognitionActive && !doingActivity
        startWalkingButton.isEnabled = !autoRecognitionActive && !doingActivity
        startSittingButton.isEnabled = !autoRecognitionActive && !doingActivity

        stopWalkingButton.isEnabled = !autoRecognitionActive && isWalking
        stopDrivingButton.isEnabled = !autoRecognitionActive && isDriving
        stopSittingButton.isEnabled = !autoRecognitionActive && isSitting
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
    }private val stepsRequestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startWalkingActivity()
        } else {
            Toast.makeText(requireContext(), "Activity permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun stopUnknownActivityService() {
        val intent = Intent(requireContext(), UnknownActivityService::class.java)
        requireContext().stopService(intent)
    }

    private fun startUnknownActivityService() {
        val intent = Intent(requireContext(), UnknownActivityService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun updateDailyStats() {
        val currentMillis = System.currentTimeMillis()
        statsViewmodel.getTotalStepsForDay(currentMillis).observe(viewLifecycleOwner) { steps ->
            steps?.let {
                viewModel.updateWalkingSteps(it)
            }
        }
        statsViewmodel.getTotalWalkingTimeForDay(currentMillis).observe(viewLifecycleOwner) { mins ->
            mins?.let {
                viewModel.updateWalkingMins(it)
            }
        }
        statsViewmodel.getTotalDrivingTimeForDay(currentMillis).observe(viewLifecycleOwner) { mins ->
            mins?.let {
                viewModel.updateDrivingMins(it)
            }
        }
        statsViewmodel.getTotalSittingTimeForDay(currentMillis).observe(viewLifecycleOwner) { mins ->
            mins?.let {
                viewModel.updateSittingMins(it)
            }
        }
    }
}