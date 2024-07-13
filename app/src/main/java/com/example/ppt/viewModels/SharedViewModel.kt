package com.example.ppt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private val _isAutoRecognitionActive = MutableLiveData(false)

    val isAutoRecognitionActive: LiveData<Boolean>
        get() = _isAutoRecognitionActive

    fun setAutoRecognitionActive(active: Boolean) {
        _isAutoRecognitionActive.value = active
    }
}