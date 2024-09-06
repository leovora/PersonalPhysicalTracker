package com.example.ppt.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * ViewModel per gestire gli obiettivi giornalieri dell'utente
 */

class GoalViewModel(application: Application) : AndroidViewModel(application) {

    // SharedPreferences per memorizzare e recuperare il valore dell'obiettivo giornaliero
    private val sharedPreferences = application.getSharedPreferences("com.example.ppt", Context.MODE_PRIVATE)

    private val _dailyGoal = MutableLiveData<Float>(sharedPreferences.getFloat("dailyGoal", 2500f))
    private val _isDailyGoalReached = MutableLiveData(false)

    // LiveData per l'accesso in sola lettura all'obiettivo giornaliero
    val dailyGoal: LiveData<Float>
        get() = _dailyGoal


    // Controlla se l'obiettivo giornaliero è stato raggiunto
    fun checkDailyGoalReached(steps: Int) {
        _isDailyGoalReached.value = steps >= (_dailyGoal.value ?: 0f)
    }

    // Aggiorna l'obiettivo giornaliero e lo salva nelle SharedPreferences
    fun updateDailyGoal(goal: Float) {
        _dailyGoal.value = goal
        sharedPreferences.edit().putFloat("dailyGoal", goal).apply()
    }
}