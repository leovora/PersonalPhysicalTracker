package com.example.ppt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel per condividere lo stato delle attività e dell'obiettivo giornaliero tra i fragment
 */

class SharedViewModel : ViewModel() {

    // MutableLiveData per monitorare lo stato delle varie attività
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

    // LiveData per l'accesso in sola lettura dello stato di riconoscimento automatico
    val isAutoRecognitionActive: LiveData<Boolean>
        get() = _isAutoRecognitionActive

    // LiveData per l'accesso in sola lettura ai dati delle varie attività e obiettivo giornaliero
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

    val isDailyGoalReached: LiveData<Boolean>
        get() = _isDailyGoalReached

    // Aggiorna il numero di passi percorsi
    fun updateWalkingSteps(steps: Int) {
        _walkingSteps.value = steps
    }

    // Aggiorna i minuti di camminata
    fun updateWalkingMins(mins: Int) {
        _walkingMins.value = mins
    }

    // Aggiorna i minuti di guida
    fun updateDrivingMins(mins: Int) {
        _drivingMins.value = mins
    }

    // Aggiorna i minuti di seduta
    fun updateSittingMins(mins: Int) {
        _sittingMins.value = mins
    }

    // Avvia l'attività di camminata
    fun startWalkingActivity() {
        _isWalking.value = true
    }

    // Ferma l'attività di camminata
    fun stopWalkingActivity() {
        _isWalking.value = false
    }

    // Avvia l'attività di guida
    fun startDrivingActivity() {
        _isDriving.value = true
    }

    // Ferma l'attività di guida
    fun stopDrivingActivity() {
        _isDriving.value = false
    }

    // Avvia l'attività di seduta
    fun startSittingActivity() {
        _isSitting.value = true
    }

    // Ferma l'attività di seduta
    fun stopSittingActivity() {
        _isSitting.value = false
    }

    // Avvia un'attività generica
    fun startDoingActivity() {
        _isDoingActivity.value = true
    }

    // Ferma un'attività generica
    fun stopDoingActivity() {
        _isDoingActivity.value = false
    }

    // Reimposta tutti gli stati delle attività a inattivo
    fun resetActivities() {
        _isWalking.value = false
        _isDriving.value = false
        _isSitting.value = false
        _isDoingActivity.value = false
    }

    // Imposta lo stato di riconoscimento automatico
    fun setAutoRecognitionActive(active: Boolean) {
        _isAutoRecognitionActive.value = active
    }

    // Controlla se l'obiettivo giornaliero è stato raggiunto
    fun checkDailyGoalReached(steps: Int) {
        _isDailyGoalReached.value = steps >= (_dailyGoal.value ?: 0f)
    }

    // Aggiorna l'obiettivo giornaliero
    fun updateDailyGoal(goal: Float) {
        _dailyGoal.value = goal
    }
}