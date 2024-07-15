package com.example.ppt.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class GoalViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("com.example.ppt", Context.MODE_PRIVATE)
    private val _dailyGoal = MutableLiveData<Float>(sharedPreferences.getFloat("dailyGoal", 2500f))
    private val _isDailyGoalReached = MutableLiveData(false)

    val isDailyGoalReached: LiveData<Boolean>
        get() = _isDailyGoalReached

    val dailyGoal: LiveData<Float>
        get() = _dailyGoal

    fun checkDailyGoalReached(steps: Int) {
        _isDailyGoalReached.value = steps >= (_dailyGoal.value ?: 0f)
    }

    fun updateDailyGoal(goal: Float) {
        _dailyGoal.value = goal
        sharedPreferences.edit().putFloat("dailyGoal", goal).apply()
    }

}