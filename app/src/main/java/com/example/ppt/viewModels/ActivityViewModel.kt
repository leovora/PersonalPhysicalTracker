package com.example.ppt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel : ViewModel() {

    private val _isWalking = MutableLiveData(false)
    private val _isDriving = MutableLiveData(false)
    private val _isSitting = MutableLiveData(false)
    private val _walkingSteps = MutableLiveData<Int>()
    private val _walkingMins = MutableLiveData<Int>()
    private val _drivingMins = MutableLiveData<Int>()
    private val _sittingMins = MutableLiveData<Int>()

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

    fun resetActivities() {
        _isWalking.value = false
        _isDriving.value = false
        _isSitting.value = false
    }
}