package com.example.ppt.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppt.data.dao.ActivityDao
import com.example.ppt.data.entities.Activity
import com.example.ppt.data.repositories.ActivityRepository
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

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