package com.example.ppt.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * DAO per eseguire operazioni CRUD sulla tabella "activities" nel database.
 */
@Dao
interface ActivityDao {

    /**
     * Inserisce un'attività nel database.
     * La funzione è `suspend` per poter essere eseguita in modo asincrono.
     */
    @Insert
    suspend fun insert(activity: Activity)

    /**
     * Restituisce tutte le attività ordinate per timestamp di inizio (più recente per primo).
     */
    @Query("SELECT * FROM activities ORDER BY startTimeMillis DESC")
    fun getAllActivities(): LiveData<List<Activity>>

    /**
     * Restituisce tutte le attività filtrate per tipo, ordinate per timestamp di inizio.
     */
    @Query("SELECT * FROM activities WHERE type = :type ORDER BY startTimeMillis DESC")
    fun getActivitiesByType(type: String): LiveData<List<Activity>>

    /**
     * Restituisce tutte le attività che si sono svolte in una determinata giornata.
     */
    @Query("SELECT * FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay")
    fun getActivitiesByDate(startOfDay: Long, endOfDay: Long): LiveData<List<Activity>>

    /**
     * Restituisce tutte le attività svolte in un determinato mese.
     */
    @Query("SELECT * FROM activities WHERE startTimeMillis >= :startOfMonth AND startTimeMillis <= :endOfMonth")
    fun getActivitiesByMonth(startOfMonth: Long, endOfMonth: Long): LiveData<List<Activity>>

    /**
     * Restituisce attività filtrate per tipo e durata in minuti. Se `type` o `duration` sono null,
     * verranno ignorati nel filtro.
     */
    @Query("SELECT * FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay" +
            " AND (:type IS NULL OR :type = 'All' OR type = :type)" +
            " AND (:duration IS NULL OR ((endTimeMillis - startTimeMillis) / 60000) = :duration)")
    fun getFilteredActivities(startOfDay: Long, endOfDay: Long, type: String?, duration: Int?): LiveData<List<Activity>>

    /**
     * Restituisce il totale dei passi effettuati durante una giornata.
     */
    @Query("SELECT SUM(stepsCount) FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay AND type = 'Walking'")
    fun getTotalStepsForDay(startOfDay: Long, endOfDay: Long): LiveData<Int>

    /**
     * Restituisce il tempo totale (in millisecondi) speso camminando durante una giornata.
     */
    @Query("SELECT SUM(endTimeMillis - startTimeMillis) FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay AND type = 'Walking'")
    fun getTotalWalkingTimeForDay(startOfDay: Long, endOfDay: Long): LiveData<Int>

    /**
     * Restituisce il tempo totale (in millisecondi) speso guidando durante una giornata.
     */
    @Query("SELECT SUM(endTimeMillis - startTimeMillis) FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay AND type = 'Driving'")
    fun getTotalDrivingTimeForDay(startOfDay: Long, endOfDay: Long): LiveData<Int>

    /**
     * Restituisce il tempo totale (in millisecondi) speso seduti durante una giornata.
     */
    @Query("SELECT SUM(endTimeMillis - startTimeMillis) FROM activities WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay AND type = 'Sitting'")
    fun getTotalSittingTimeForDay(startOfDay: Long, endOfDay: Long): LiveData<Int>
}