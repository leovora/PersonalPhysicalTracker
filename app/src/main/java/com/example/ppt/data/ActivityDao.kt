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

    @Query("SELECT * FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay" +
            " AND (:type IS NULL OR :type = 'All' OR type = :type)" +
            " AND (:duration IS NULL OR ((endTimeMillis - startTimeMillis) / 60000) = :duration)")
    fun getFilteredActivities(startOfDay: Long, endOfDay: Long, type: String?, duration: Int?): LiveData<List<Activity>>

    //TODO: aggiungere query per grafici
    // "https://www.youtube.com/watch?v=TP3uxBLzlhU&list=PLQkwcJG4YTCQ6emtoqSZS2FVwZR9FT3BV&index=3"
}