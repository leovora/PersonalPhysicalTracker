package com.example.ppt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private val _isWalking = MutableLiveData(false)
    private val _isDriving = MutableLiveData(false)
    private val _isSitting = MutableLiveData(false)
    private val _isDoingActivity = MutableLiveData(false)
    private val _walkingSteps = MutableLiveData<Int>()
    private val _walkingMins = MutableLiveData<Int>()
    private val _drivingMins = MutableLiveData<Int>()
    private val _sittingMins = MutableLiveData<Int>()
    private val _isAutoRecognitionActive = MutableLiveData(false)
    private val _dailyGoal = MutableLiveData<Float>(2500f)
    private val _isDailyGoalReached = MutableLiveData(false)

    val isAutoRecognitionActive: LiveData<Boolean>
        get() = _isAutoRecognitionActive

    val walkingSteps: LiveData<Int>
        get() = _walkingSteps

    val walkingMins: LiveData<Int>
        get() = _walkingMins

    val drivingMins: LiveData<Int>
        get() = _drivingMins

    val sittingMins: LiveData<Int>
        get() = _sittingMins

    val isWalking: LiveData<Boolean>
        get() = _isWalking

    val isDriving: LiveData<Boolean>
        get() = _isDriving

    val isSitting: LiveData<Boolean>
        get() = _isSitting

    val isDoingActivity: LiveData<Boolean>
        get() = _isDoingActivity

    val dailyGoal: LiveData<Float>
        get() = _dailyGoal

    val isDailyGoalReached: LiveData<Boolean>
        get() = _isDailyGoalReached

    fun updateWalkingSteps(steps: Int) {
        _walkingSteps.value = steps
    }

    fun updateWalkingMins(mins: Int) {
        _walkingMins.value = mins
    }

    fun updateDrivingMins(mins: Int) {
        _drivingMins.value = mins
    }

    fun updateSittingMins(mins: Int) {
        _sittingMins.value = mins
    }

    fun startWalkingActivity() {
        _isWalking.value = true
    }

    fun stopWalkingActivity() {
        _isWalking.value = false
    }

    fun startDrivingActivity() {
        _isDriving.value = true
    }

    fun stopDrivingActivity() {
        _isDriving.value = false
    }

    fun startSittingActivity() {
        _isSitting.value = true
    }

    fun stopSittingActivity() {
        _isSitting.value = false
    }

    fun startDoingActivityActivity() {
        _isDoingActivity.value = true
    }

    fun stopDoingActivityActivity() {
        _isDoingActivity.value = false
    }

    fun resetActivities() {
        _isWalking.value = false
        _isDriving.value = false
        _isSitting.value = false
        _isDoingActivity.value = false
    }

    fun setAutoRecognitionActive(active: Boolean) {
        _isAutoRecognitionActive.value = active
    }

    fun checkDailyGoalReached(steps: Int) {
        _isDailyGoalReached.value = steps >= (_dailyGoal.value ?: 0f)
    }

    fun updateDailyGoal(goal: Float) {
        _dailyGoal.value = goal
    }

}