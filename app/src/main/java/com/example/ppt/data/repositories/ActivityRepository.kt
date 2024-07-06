package com.example.ppt.data.repositories

import androidx.lifecycle.LiveData
import com.example.ppt.data.dao.ActivityDao
import com.example.ppt.data.entities.Activity

class ActivityRepository(private val activityDao: ActivityDao) {

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