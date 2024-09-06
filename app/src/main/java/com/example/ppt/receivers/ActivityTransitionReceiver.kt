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

/**
 * Receiver per gestire i cambiamenti di attività
 */

class ActivityTransitionReceiver : BroadcastReceiver() {

    // Metodo chiamato quando il broadcast viene ricevuto
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ActivityTransitionReceiver", "onReceive called")

        // Controlla se l'intent contiene un risultato di transizione dell'attività
        if (ActivityTransitionResult.hasResult(intent)) {
            Log.d("ActivityTransitionReceiver", "ActivityTransitionResult has result")

            // Estrae il risultato dell'attività dall'intent
            val result = ActivityTransitionResult.extractResult(intent)!!
            // Itera attraverso gli eventi di transizione dell'attività
            for (event in result.transitionEvents) {
                Log.d("ActivityTransitionReceiver", "Activity transition: ${event.activityType} ${event.transitionType}")
                // Gestisce ciascun tipo di attività
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

    // Gestisce la transizione dell'attività quando si guida
    private fun handleInVehicleTransition(context: Context, transitionType: Int) {
        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            context.startService(Intent(context, DrivingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Started DrivingActivityService")
        } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            context.stopService(Intent(context, DrivingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Stopped DrivingActivityService")
        }
    }

    // Gestisce la transizione dell'attività quando si cammina
    private fun handleWalkingTransition(context: Context, transitionType: Int) {
        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            context.startService(Intent(context, WalkingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Started WalkingActivityService")
        } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            context.stopService(Intent(context, WalkingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Stopped WalkingActivityService")
        }
    }

    // Gestisce la transizione dell'attività quando si corre
    private fun handleRunningTransition(context: Context, transitionType: Int) {
        if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            context.startService(Intent(context, WalkingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Started WalkingActivityService for Running")
        } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            context.stopService(Intent(context, WalkingActivityService::class.java))
            Log.d("ActivityTransitionReceiver", "Stopped WalkingActivityService for Running")
        }
    }

    // Gestisce la transizione dell'attività quando si è seduti
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