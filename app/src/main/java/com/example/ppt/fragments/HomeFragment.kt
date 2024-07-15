package com.example.ppt.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.R
import com.example.ppt.activities.DailyGoalActivity
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.ActivityRepository
import com.example.ppt.other.ActivityViewModelFactory
import com.example.ppt.other.StatsViewModelFactory
import com.example.ppt.services.DrivingActivityService
import com.example.ppt.services.SittingActivityService
import com.example.ppt.services.UnknownActivityService
import com.example.ppt.services.WalkingActivityService
import com.example.ppt.viewModels.GoalViewModel
import com.example.ppt.viewModels.SharedViewModel
import com.example.ppt.viewModels.StatsViewModel

class HomeFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var database: ActivityDatabase
    private lateinit var repository: ActivityRepository
    private lateinit var statsFactory: StatsViewModelFactory
    private lateinit var statsViewmodel: StatsViewModel
    private lateinit var goalViewModel: GoalViewModel

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
    private lateinit var progressBarCard: CardView
    private lateinit var messageTitle: TextView
    private lateinit var messageDescription: TextView

    private var doingActivity = false
    private var isWalking = false
    private var isDriving = false
    private var isSitting = false

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        database = ActivityDatabase.getDatabase(requireContext())
        repository = ActivityRepository(database.getDao())
        statsFactory = StatsViewModelFactory(repository)
        statsViewmodel = ViewModelProvider(this, statsFactory).get(StatsViewModel::class.java)

        val application = requireActivity().application
        val factory = ActivityViewModelFactory(application)
        goalViewModel = ViewModelProvider(this, factory)[GoalViewModel::class.java]
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
        progressBarCard = view.findViewById(R.id.messageCard)
        messageTitle = view.findViewById(R.id.message_title)
        messageDescription = view.findViewById(R.id.message_description)

        startWalkingButton.setOnClickListener {
            if (!doingActivity) {
                checkActivityPermissionAndStartWalkingActivity()
                sharedViewModel.startWalkingActivity()
            }
        }

        stopWalkingButton.setOnClickListener {
            if (isWalking) {
                stopWalkingActivity()
                sharedViewModel.stopWalkingActivity()
            }
        }

        startDrivingButton.setOnClickListener {
            if (!doingActivity) {
                startDrivingActivity()
                sharedViewModel.startDrivingActivity()
            }
        }

        stopDrivingButton.setOnClickListener {
            if (isDriving) {
                stopDrivingActivity()
                sharedViewModel.stopDrivingActivity()
            }
        }

        startSittingButton.setOnClickListener {
            if (!doingActivity) {
                startSittingActivity()
                sharedViewModel.startSittingActivity()
            }
        }

        stopSittingButton.setOnClickListener {
            if (isSitting) {
                stopSittingActivity()
                sharedViewModel.stopSittingActivity()
            }
        }

        sharedViewModel.isWalking.observe(viewLifecycleOwner) { isWalking ->
            stopWalkingButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isWalking) R.color.red else R.color.white
                )
            )
        }

        sharedViewModel.isDriving.observe(viewLifecycleOwner) { isDriving ->
            stopDrivingButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isDriving) R.color.red else R.color.white
                )
            )
        }

        sharedViewModel.isSitting.observe(viewLifecycleOwner) { isSitting ->
            stopSittingButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isSitting) R.color.red else R.color.white
                )
            )
        }

        statsViewmodel.getTotalStepsForDay(System.currentTimeMillis()).observe(viewLifecycleOwner) { steps ->
            steps?.let {
                sharedViewModel.updateWalkingSteps(it)
                sharedViewModel.checkDailyGoalReached(it)
            }
        }

        sharedViewModel.isDoingActivity.observe(viewLifecycleOwner) { isDoingActivity ->
            doingActivity = isDoingActivity
            updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
        }

        sharedViewModel.isAutoRecognitionActive.observe(viewLifecycleOwner) { isActive ->
            updateButtons(sharedViewModel.isAutoRecognitionActive.value == true)
        }

        sharedViewModel.walkingSteps.observe(viewLifecycleOwner) { steps ->
            walkingStepsText.text = steps.toString()
            goalViewModel.checkDailyGoalReached(steps)
        }

        sharedViewModel.walkingMins.observe(viewLifecycleOwner) { mins ->
            walkingMinsText.text = (mins / 60000).toString()
        }

        sharedViewModel.drivingMins.observe(viewLifecycleOwner) { mins ->
            drivingMinsText.text = (mins / 60000).toString()
        }

        sharedViewModel.sittingMins.observe(viewLifecycleOwner) { mins ->
            sittingMinsText.text = (mins / 60000).toString()
        }

        progressBarCard.setOnClickListener {
            startActivity(Intent(requireContext(), DailyGoalActivity::class.java))
        }

        sharedViewModel.isDailyGoalReached.observe(viewLifecycleOwner) { isReached ->
            if (isReached) {
                messageTitle.text = "You\'re doing great!"
                messageDescription.text = "Keep it up! You completed your daily goal, but I think you can do even more!"
            } else {
                messageTitle.text = "You\'re kinda static!"
                messageDescription.text = "You still haven\'t completed your daily goal. Take a walk!"
            }
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
    }

    private val stepsRequestPermissionLauncher = registerForActivityResult(
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
                sharedViewModel.updateWalkingSteps(it)
                goalViewModel.checkDailyGoalReached(it)
            }
        }
        statsViewmodel.getTotalWalkingTimeForDay(currentMillis).observe(viewLifecycleOwner) { mins ->
            mins?.let {
                sharedViewModel.updateWalkingMins(it)
            }
        }
        statsViewmodel.getTotalDrivingTimeForDay(currentMillis).observe(viewLifecycleOwner) { mins ->
            mins?.let {
                sharedViewModel.updateDrivingMins(it)
            }
        }
        statsViewmodel.getTotalSittingTimeForDay(currentMillis).observe(viewLifecycleOwner) { mins ->
            mins?.let {
                sharedViewModel.updateSittingMins(it)
            }
        }
        statsViewmodel.getTotalStepsForDay(System.currentTimeMillis()).value?.let { steps ->
            sharedViewModel.checkDailyGoalReached(steps)
        }
    }
}