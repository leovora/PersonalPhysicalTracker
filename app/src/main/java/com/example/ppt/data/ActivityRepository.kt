package com.example.ppt.data

import android.icu.util.Calendar
import androidx.lifecycle.LiveData

/**
 * Repository per gestire le operazioni di accesso ai dati relativi alle attività.
 */
class ActivityRepository(
    private val activityDao: ActivityDao
) {

    /**
     * Restituisce le attività per un dato giorno.
     *
     * @param dateInMillis Timestamp del giorno desiderato in millisecondi.
     * @return `LiveData` che contiene la lista delle attività per quel giorno.
     */
    fun getActivitiesByDate(dateInMillis: Long): LiveData<List<Activity>> {
        val startOfDay = dateInMillis / 86400000 * 86400000 // Inizio del giorno in millisecondi
        val endOfDay = startOfDay + 86400000 - 1 // Fine del giorno in millisecondi
        return activityDao.getActivitiesByDate(startOfDay, endOfDay)
    }

    /**
     * Restituisce le attività per il mese corrente.
     *
     * @return `LiveData` che contiene la lista delle attività per il mese corrente.
     */
    fun getActivitiesByMonth(): LiveData<List<Activity>> {
        val calendar = Calendar.getInstance()
        val startOfMonth = calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
        val endOfMonth = calendar.apply { set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) }.timeInMillis
        return activityDao.getActivitiesByMonth(startOfMonth, endOfMonth)
    }

    /**
     * Restituisce le attività filtrate per un dato giorno, tipo e durata.
     *
     * @param dateInMillis Timestamp del giorno desiderato in millisecondi.
     * @param type Tipo di attività da filtrare (può essere `null` o "All").
     * @param duration Durata dell'attività in minuti (può essere `null`).
     * @return `LiveData` che contiene la lista delle attività filtrate.
     */
    fun getFilteredActivities(dateInMillis: Long, type: String?, duration: Int?): LiveData<List<Activity>> {
        val startOfDay = dateInMillis / 86400000 * 86400000
        val endOfDay = startOfDay + 86400000 - 1
        return activityDao.getFilteredActivities(startOfDay, endOfDay, type, duration)
    }

    /**
     * Restituisce il totale dei passi per un dato giorno.
     *
     * @param dateInMillis Timestamp del giorno desiderato in millisecondi.
     * @return `LiveData` che contiene il totale dei passi per quel giorno.
     */
    fun getTotalStepsForDay(dateInMillis: Long): LiveData<Int> {
        val startOfDay = dateInMillis / 86400000 * 86400000 // Inizio del giorno in millisecondi
        val endOfDay = startOfDay + 86400000 - 1 // Fine del giorno in millisecondi
        return activityDao.getTotalStepsForDay(startOfDay, endOfDay)
    }

    /**
     * Restituisce il tempo totale di camminata per un dato giorno.
     *
     * @param dateInMillis Timestamp del giorno desiderato in millisecondi.
     * @return `LiveData` che contiene il tempo totale di camminata per quel giorno in millisecondi.
     */
    fun getTotalWalkingTimeForDay(dateInMillis: Long): LiveData<Int> {
        val startOfDay = dateInMillis / 86400000 * 86400000 // Inizio del giorno in millisecondi
        val endOfDay = startOfDay + 86400000 - 1 // Fine del giorno in millisecondi
        return activityDao.getTotalWalkingTimeForDay(startOfDay, endOfDay)
    }

    /**
     * Restituisce il tempo totale di guida per un dato giorno.
     *
     * @param dateInMillis Timestamp del giorno desiderato in millisecondi.
     * @return `LiveData` che contiene il tempo totale di guida per quel giorno in millisecondi.
     */
    fun getTotalDrivingTimeForDay(dateInMillis: Long): LiveData<Int> {
        val startOfDay = dateInMillis / 86400000 * 86400000 // Inizio del giorno in millisecondi
        val endOfDay = startOfDay + 86400000 - 1 // Fine del giorno in millisecondi
        return activityDao.getTotalDrivingTimeForDay(startOfDay, endOfDay)
    }

    /**
     * Restituisce il tempo totale passato seduti per un dato giorno.
     *
     * @param dateInMillis Timestamp del giorno desiderato in millisecondi.
     * @return `LiveData` che contiene il tempo totale di sedentarietà per quel giorno in millisecondi.
     */
    fun getTotalSittingTimeForDay(dateInMillis: Long): LiveData<Int> {
        val startOfDay = dateInMillis / 86400000 * 86400000 // Inizio del giorno in millisecondi
        val endOfDay = startOfDay + 86400000 - 1 // Fine del giorno in millisecondi
        return activityDao.getTotalSittingTimeForDay(startOfDay, endOfDay)
    }

}