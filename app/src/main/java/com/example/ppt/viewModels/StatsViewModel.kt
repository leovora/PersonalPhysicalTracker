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

    fun getActivitiesByMonth(): LiveData<List<Activity>>{
        return repository.getActivitiesByMonth()
    }

    fun getActivitiesByDate(date: Long): LiveData<List<Activity>> {
        return repository.getActivitiesByDate(date)
    }

    fun getTotalStepsForDay(dateInMillis: Long): LiveData<Int> {
        return repository.getTotalStepsForDay(dateInMillis)
    }

    fun getTotalWalkingTimeForDay(dateInMillis: Long): LiveData<Int> {
        return repository.getTotalWalkingTimeForDay(dateInMillis)
    }

    fun getTotalDrivingTimeForDay(dateInMillis: Long): LiveData<Int> {
        return repository.getTotalDrivingTimeForDay(dateInMillis)
    }

    fun getTotalSittingTimeForDay(dateInMillis: Long): LiveData<Int> {
        return repository.getTotalSittingTimeForDay(dateInMillis)
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