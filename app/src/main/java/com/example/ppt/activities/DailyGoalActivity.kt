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
import com.example.ppt.viewModels.ActivityViewModel
import com.example.ppt.viewModels.StatsViewModel
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class DailyGoalActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var progressBar: CircularProgressBar
    private lateinit var statsViewModel: StatsViewModel
    private lateinit var activityViewModel: ActivityViewModel

    private lateinit var currentSteps: TextView
    private lateinit var goal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dailygoal)

        goal = findViewById(R.id.stepsGoal_text)
        currentSteps = findViewById(R.id.progressSteps_text)
        backButton = findViewById(R.id.back_button_dailygoal)
        backButton.setOnClickListener {
            finish()
        }

        val database = ActivityDatabase.getDatabase(this)
        val repository = ActivityRepository(database.getDao())
        val statsFactory = StatsViewModelFactory(repository)
        statsViewModel = ViewModelProvider(this, statsFactory)[StatsViewModel::class.java]

        val application = this.application
        val factory = ActivityViewModelFactory(application)
        activityViewModel = ViewModelProvider(this, factory)[ActivityViewModel::class.java]

        progressBar = findViewById(R.id.progressBar)

        activityViewModel.dailyGoal.observe(this) { goalValue ->
            progressBar.progressMax = goalValue
            goal.text = "/ " + (goalValue.toInt()).toString()
        }

        statsViewModel.getTotalStepsForDay(System.currentTimeMillis()).observe(this) { steps ->
            steps?.let {
                progressBar.setProgressWithAnimation(it.toFloat())
                currentSteps.text = it.toString()
            }
        }
    }
}