package com.example.ppt.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.R
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.ActivityRepository
import com.example.ppt.viewModels.StatsViewModel
import com.example.ppt.other.StatsViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*
import kotlin.collections.ArrayList

class LineChartActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var lineChart: LineChart
    private lateinit var viewModel: StatsViewModel

    /**
     * Inizializza l'attività, imposta la UI e configura il LineChart per visualizzare
     * i dati relativi ai passi giornalieri in un mese.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linechart)

        // Collega il bottone "Indietro" alla UI e definisce la sua azione (chiusura activity)
        backButton = findViewById(R.id.back_button_linechart)
        backButton.setOnClickListener {
            finish()
        }

        // Inizializza il database, il repository e il ViewModel
        val database = ActivityDatabase.getDatabase(this)
        val repository = ActivityRepository(database.getDao())
        val factory = StatsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StatsViewModel::class.java]

        // Collega il LineChart dalla UI
        lineChart = findViewById(R.id.LineChart)

        // Configura l'asse X del grafico
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 2f
        xAxis.labelCount = 31

        // Disabilita l'asse destro Y
        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false

        // Osserva i dati relativi alle attività del mese nel ViewModel
        viewModel.getActivitiesByMonth().observe(this, Observer { activities ->
            activities?.let {
                // Crea una mappa per memorizzare i passi per ogni giorno del mese
                val stepsPerDay = mutableMapOf<Int, Int>()
                val calendar = Calendar.getInstance()

                // Inizializza la mappa con tutti i giorni del mese impostati a 0 passi
                for (day in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    stepsPerDay[day] = 0
                }

                // Cicla attraverso le attività e aggiunge i passi al giorno corrispondente
                for (activity in activities) {
                    if (activity.type == "Walking") {
                        calendar.timeInMillis = activity.startTimeMillis
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        stepsPerDay[day] = stepsPerDay.getOrDefault(day, 0) + activity.stepsCount!!
                    }
                }

                // Crea una lista di Entry per il grafico, con il giorno del mese sull'asse X e i passi sull'asse Y
                val entries = ArrayList<Entry>()
                for ((day, steps) in stepsPerDay) {
                    entries.add(Entry(day.toFloat(), steps.toFloat()))
                }

                // Configura i dati del grafico (LineDataSet e LineData)
                val dataSet = LineDataSet(entries, "")
                dataSet.color = R.color.green
                val lineData = LineData(dataSet)

                // Imposta i dati nel LineChart e aggiorna la visualizzazione
                lineChart.data = lineData
                lineChart.invalidate()
                lineChart.legend.isEnabled = false
                lineChart.animate()
                lineChart.setGridBackgroundColor(0)
                lineChart.description.isEnabled = false
            }
        })
    }
}