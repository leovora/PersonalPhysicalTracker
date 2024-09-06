package com.example.ppt.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.data.ActivityRepository
import com.example.ppt.viewModels.StatsViewModel

/**
 * Factory per la creazione di istanze di StatsViewModel
 */

class StatsViewModelFactory(private val repository: ActivityRepository) : ViewModelProvider.Factory {

    // Metodo per creare un'istanza di ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) { // Verifica se il ViewModel richiesto è StatsViewModel
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}