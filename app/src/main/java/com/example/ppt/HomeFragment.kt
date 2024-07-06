package com.example.ppt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private fun startWalkingActivity() {
        CoroutineScope(Dispatchers.IO).launch {
            val intent = Intent(requireContext(), WalkingActivityService::class.java)
            requireContext().startService(intent)
        }
        isWalking = true
        doingActivity = true
        startWalkingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        stopWalkingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        updateButtons()
    }

    private fun stopWalkingActivity() {
        CoroutineScope(Dispatchers.IO).launch {
            val intent = Intent(requireContext(), WalkingActivityService::class.java)
            requireContext().stopService(intent)
        }
        isWalking = false
        doingActivity = false
        stopWalkingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        updateButtons()
    }

    private fun startDrivingActivity() {
        // Implement the logic to start driving activity
    }

    private fun stopDrivingActivity() {
        // Implement the logic to stop driving activity
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