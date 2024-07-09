package com.example.ppt.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActivityViewModel(
    val repository: ActivityRepository
) : ViewModel() {

    fun getAllActivities(): LiveData<List<Activity>> {
        return repository.getAllActivities()
    }

    fun getActivitiesByType(type: String): LiveData<List<Activity>> {
        return repository.getActivitiesByType(type)
    }

    fun getActivitiesByDate(date: Long): LiveData<List<Activity>> {
        return repository.getActivitiesByDate(date)
    }

    //insert with coroutine
    fun insertActivity(activity: Activity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
        }
    }
}