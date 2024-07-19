package com.example.ppt.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ppt.services.DrivingActivityService
import com.example.ppt.services.SittingActivityService
import com.example.ppt.services.WalkingActivityService
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ActivityTransitionReceiver", "onReceive called")

        if (ActivityTransitionResult.hasResult(intent)) {
            Log.d("ActivityTransitionReceiver", "ActivityTransitionResult has result")

            val result = ActivityTransitionResult.extractResult(intent)!!
            for (event in result.transitionEvents) {
                Log.d("ActivityTransitionReceiver", "Activity transition: ${event.activityType} ${event.transitionType}")
                when (event.activityType) {
                    DetectedActivity.IN_VEHICLE -> handleInVehicleTransition(context, event.transitionType)
                    DetectedActivity.WALKING -> handleWalkingTransition(context, event.transitionType)
                    DetectedActivity.RUNNING -> handleRunningTransition(context, event.transitionType)
                    DetectedActivity.STILL -> handleStillTransition(context, event.transitionType)
                }
            }
        } else {
            Log.d("ActivityTransitionReceiver", "No ActivityTransitionResult in intent")
        }
    }

    private fun handleInVehicleTransition(context: Context, transitionType: Int) {
        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            context.startService(Intent(context, DrivingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Started DrivingActivityService")
        } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            context.stopService(Intent(context, DrivingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Stopped DrivingActivityService")
        }
    }

    private fun handleWalkingTransition(context: Context, transitionType: Int) {
        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            context.startService(Intent(context, WalkingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Started WalkingActivityService")
        } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            context.stopService(Intent(context, WalkingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Stopped WalkingActivityService")
        }
    }

    private fun handleRunningTransition(context: Context, transitionType: Int) {
        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            context.startService(Intent(context, WalkingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Started WalkingActivityService for Running")
        } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            context.stopService(Intent(context, WalkingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Stopped WalkingActivityService for Running")
        }
    }

    private fun handleStillTransition(context: Context, transitionType: Int) {
        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            context.startService(Intent(context, SittingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Started SittingActivityService")
        } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            context.stopService(Intent(context, SittingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Stopped SittingActivityService")
        }
    }
}