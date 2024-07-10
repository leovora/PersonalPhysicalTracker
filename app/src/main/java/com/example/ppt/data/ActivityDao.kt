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

    //TODO: aggiungere query per grafici
    // "https://www.youtube.com/watch?v=TP3uxBLzlhU&list=PLQkwcJG4YTCQ6emtoqSZS2FVwZR9FT3BV&index=3"
}