package com.example.ppt.other

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.viewModels.GoalViewModel

/**
 * Factory per creare istanze del ViewModel GoalViewModel
 */
class ActivityViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    // Metodo per creare una nuova istanza del ViewModel richiesto
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(GoalViewModel::class.java)) { // Verifica se il ViewModel richiesto è di tipo GoalViewModel
            @Suppress("UNCHECKED_CAST")
            return GoalViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}