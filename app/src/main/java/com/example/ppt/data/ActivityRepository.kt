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

    fun getActivitiesByDate(dateInMillis: Long): LiveData<List<Activity>> {
        val startOfDay = dateInMillis / 86400000 * 86400000 // Start of the day in milliseconds
        val endOfDay = startOfDay + 86400000 - 1 // End of the day in milliseconds
        return activityDao.getActivitiesByDate(startOfDay, endOfDay)
    }
}