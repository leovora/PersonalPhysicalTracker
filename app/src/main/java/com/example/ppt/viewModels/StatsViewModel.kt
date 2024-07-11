package com.example.ppt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityRepository
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: ActivityRepository): ViewModel() {

    val selectedDate = MutableLiveData<Long>()
    val filterType = MutableLiveData<String?>()
    val filterDuration = MutableLiveData<Int?>()

    fun getFilteredActivities(date: Long, type: String?, duration: Int?): LiveData<List<Activity>>{
        return repository.getFilteredActivities(date, type, duration)
    }

    fun getActivitiesByType(type: String): LiveData<List<Activity>> {
        return repository.getActivitiesByType(type)
    }

    fun getActivitiesByMonth(): LiveData<List<Activity>>{
        return repository.getActivitiesByMonth()
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

    fun setDate(date: Long) {
        selectedDate.value = date
    }

    fun setType(type: String?) {
        filterType.value = type
    }

    fun setDuration(duration: Int?) {
        filterDuration.value = duration
    }
}