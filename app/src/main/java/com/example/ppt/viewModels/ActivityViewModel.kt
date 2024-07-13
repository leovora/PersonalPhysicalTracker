package com.example.ppt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel: ViewModel() {
    private val _isWalking = MutableLiveData(false)
    private val _isDriving = MutableLiveData(false)
    private val _isSitting = MutableLiveData(false)

    val isWalking: LiveData<Boolean>
        get() = _isWalking

    val isDriving: LiveData<Boolean>
        get() = _isDriving

    val isSitting: LiveData<Boolean>
        get() = _isSitting

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