package com.example.ppt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ppt.data.entities.Activity

@Dao
interface ActivityDao {

    @Insert
    suspend fun insert(activity: Activity)

    @Query("SELECT * FROM activities ORDER BY startTimeMillis DESC")
    fun getAllActivities(): LiveData<List<Activity>>

    @Query("SELECT * FROM activities WHERE type = :type ORDER BY startTimeMillis DESC")
    fun getActivitiesByType(type: String): LiveData<List<Activity>>

    @Query("SELECT * FROM activities WHERE startTimeMillis = :date")
    fun getActivitiesBytDate(date: Long): LiveData<List<Activity>>
}