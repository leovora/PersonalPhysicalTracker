package com.example.ppt.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.R
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.ActivityRepository
import com.example.ppt.other.ActivityViewModelFactory
import com.example.ppt.other.StatsViewModelFactory
import com.example.ppt.viewModels.GoalViewModel
import com.example.ppt.viewModels.StatsViewModel
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class DailyGoalActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var progressBar: CircularProgressBar
    private lateinit var statsViewModel: StatsViewModel
    private lateinit var activityViewModel: GoalViewModel
    private lateinit var currentSteps: TextView
    private lateinit var goal: TextView

    /**
     * Inizializza l'attività, imposta la UI, configura i ViewModel e aggiorna la UI
     * con i dati osservati relativi agli obiettivi e ai passi dell'utente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dailygoal)

        // Collega le TextView della UI agli elementi definiti nel layout
        goal = findViewById(R.id.stepsGoal_text)
        currentSteps = findViewById(R.id.progressSteps_text)

        // Configura il bottone "Indietro" per terminare l'attività quando viene premuto
        backButton = findViewById(R.id.back_button_dailygoal)
        backButton.setOnClickListener {
            finish()
        }

        // Inizializza il database, il repository e crea i ViewModel per gestire i dati
        val database = ActivityDatabase.getDatabase(this)
        val repository = ActivityRepository(database.getDao())
        val statsFactory = StatsViewModelFactory(repository)
        statsViewModel = ViewModelProvider(this, statsFactory)[StatsViewModel::class.java]

        // Inizializza il GoalViewModel per gestire l'obiettivo giornaliero
        val application = this.application
        val factory = ActivityViewModelFactory(application)
        activityViewModel = ViewModelProvider(this, factory)[GoalViewModel::class.java]

        // Collega la CircularProgressBar dalla UI
        progressBar = findViewById(R.id.progressBar)

        // Osserva il valore dell'obiettivo giornaliero e aggiorna la progress bar e la TextView
        activityViewModel.dailyGoal.observe(this) { goalValue ->
            progressBar.progressMax = goalValue
            goal.text = "/ " + (goalValue.toInt()).toString()
        }

        // Osserva il numero di passi totalizzati in un giorno e aggiorna la progress bar con l'animazione
        statsViewModel.getTotalStepsForDay(System.currentTimeMillis()).observe(this) { steps ->
            steps?.let {
                progressBar.setProgressWithAnimation(it.toFloat())
                currentSteps.text = it.toString()
            }
        }
    }
}