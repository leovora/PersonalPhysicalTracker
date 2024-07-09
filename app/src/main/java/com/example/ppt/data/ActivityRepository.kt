package com.example.ppt.data

import androidx.lifecycle.LiveData
import javax.inject.Inject

class ActivityRepository(
    private val activityDao: ActivityDao
) {

    suspend fun insertActivity(activity: Activity) {
        activityDao.insert(activity)
    }

    fun getAllActivities(): LiveData<List<Activity>> {
        return activityDao.getAllActivities()
    }

    fun getActivitiesByType(type: String): LiveData<List<Activity>> {
        return activityDao.getActivitiesByType(type)
    }

    fun getActivitiesByDate(date: Long): LiveData<List<Activity>> {
        return activityDao.getActivitiesBytDate(date)
    }
}