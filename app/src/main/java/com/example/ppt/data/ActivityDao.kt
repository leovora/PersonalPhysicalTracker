package com.example.ppt.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {

    @Insert
    suspend fun insert(activity: Activity)

    @Query("SELECT * FROM activities ORDER BY startTimeMillis DESC")
    fun getAllActivities(): LiveData<List<Activity>>

    @Query("SELECT * FROM activities WHERE type = :type ORDER BY startTimeMillis DESC")
    fun getActivitiesByType(type: String): LiveData<List<Activity>>

    @Query("SELECT * FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay")
    fun getActivitiesByDate(startOfDay: Long, endOfDay: Long): LiveData<List<Activity>>

    @Query("SELECT * FROM activities WHERE startTimeMillis >= :startOfMonth AND startTimeMillis <= :endOfMonth")
    fun getActivitiesByMonth(startOfMonth: Long, endOfMonth: Long): LiveData<List<Activity>>

    @Query("SELECT * FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay" +
            " AND (:type IS NULL OR :type = 'All' OR type = :type)" +
            " AND (:duration IS NULL OR ((endTimeMillis - startTimeMillis) / 60000) = :duration)")
    fun getFilteredActivities(startOfDay: Long, endOfDay: Long, type: String?, duration: Int?): LiveData<List<Activity>>

    @Query("SELECT SUM(stepsCount) FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay AND type = 'Walking'")
    fun getTotalStepsForDay(startOfDay: Long, endOfDay: Long): LiveData<Int>

    @Query("SELECT SUM(endTimeMillis - startTimeMillis) FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay AND type = 'Walking'")
    fun getTotalWalkingTimeForDay(startOfDay: Long, endOfDay: Long): LiveData<Int>

    @Query("SELECT SUM(endTimeMillis - startTimeMillis) FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay AND type = 'Driving'")
    fun getTotalDrivingTimeForDay(startOfDay: Long, endOfDay: Long): LiveData<Int>

    @Query("SELECT SUM(endTimeMillis - startTimeMillis) FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay AND type = 'Sitting'")
    fun getTotalSittingTimeForDay(startOfDay: Long, endOfDay: Long): LiveData<Int>
}