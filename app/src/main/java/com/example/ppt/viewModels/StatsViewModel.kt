package com.example.ppt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityRepository
import kotlinx.coroutines.launch

/**
 * ViewModel per gestire e fornire i dati delle statistiche
 */

class StatsViewModel(private val repository: ActivityRepository) : ViewModel() {

    // MutableLiveData per la data selezionata
    val selectedDate = MutableLiveData<Long>()

    // MutableLiveData per il tipo di filtro applicato
    val filterType = MutableLiveData<String?>()

    // MutableLiveData per il filtro di durata
    val filterDuration = MutableLiveData<Int?>()

    // Restituisce una LiveData con la lista di attività filtrate in base alla data, tipo e durata
    fun getFilteredActivities(date: Long, type: String?, duration: Int?): LiveData<List<Activity>> {
        return repository.getFilteredActivities(date, type, duration)
    }

    // Restituisce una LiveData con la lista delle attività del mese corrente
    fun getActivitiesByMonth(): LiveData<List<Activity>> {
        return repository.getActivitiesByMonth()
    }

    // Restituisce una LiveData con la lista delle attività per una data specifica
    fun getActivitiesByDate(date: Long): LiveData<List<Activity>> {
        return repository.getActivitiesByDate(date)
    }

    // Restituisce una LiveData con il totale dei passi per un giorno specifico
    fun getTotalStepsForDay(dateInMillis: Long): LiveData<Int> {
        return repository.getTotalStepsForDay(dateInMillis)
    }

    // Restituisce una LiveData con il totale del tempo di camminata per un giorno specifico
    fun getTotalWalkingTimeForDay(dateInMillis: Long): LiveData<Int> {
        return repository.getTotalWalkingTimeForDay(dateInMillis)
    }

    // Restituisce una LiveData con il totale del tempo di guida per un giorno specifico
    fun getTotalDrivingTimeForDay(dateInMillis: Long): LiveData<Int> {
        return repository.getTotalDrivingTimeForDay(dateInMillis)
    }

    // Restituisce una LiveData con il totale del tempo di seduta per un giorno specifico
    fun getTotalSittingTimeForDay(dateInMillis: Long): LiveData<Int> {
        return repository.getTotalSittingTimeForDay(dateInMillis)
    }

    // Imposta la data selezionata e aggiorna la LiveData
    fun setDate(date: Long) {
        selectedDate.value = date
    }

    // Imposta il filtro di tipologia e aggiorna la LiveData
    fun setType(type: String?) {
        filterType.value = type
    }

    // Imposta il filtro di durata e aggiorna la LiveData
    fun setDuration(duration: Int?) {
        filterDuration.value = duration
    }
}