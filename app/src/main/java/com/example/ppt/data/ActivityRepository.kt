package com.example.ppt.data

import android.icu.util.Calendar
import androidx.lifecycle.LiveData

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

    fun getActivitiesByMonth(): LiveData<List<Activity>> {
        val calendar = Calendar.getInstance()
        val startOfMonth = calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
        val endOfMonth = calendar.apply { set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) }.timeInMillis
        return activityDao.getActivitiesByMonth(startOfMonth, endOfMonth)
    }

    fun getFilteredActivities(dateInMillis: Long, type: String?, duration: Int?): LiveData<List<Activity>> {
        val startOfDay = dateInMillis / 86400000 * 86400000
        val endOfDay = startOfDay + 86400000 - 1
        return activityDao.getFilteredActivities(startOfDay, endOfDay, type, duration)
    }


}