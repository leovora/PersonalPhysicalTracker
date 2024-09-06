package com.example.ppt.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.R
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.ActivityRepository
import com.example.ppt.other.StatsViewModelFactory
import com.example.ppt.viewModels.StatsViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class PieChartActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var pieChart: PieChart
    private lateinit var viewModel: StatsViewModel

    /**
     * Inizializza il layout, imposta il pulsante "indietro" per chiudere l'attività,
     * configura il ViewModel e crea un grafico a torta basato sui dati recuperati dal ViewModel.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piechart)

        // Inizializzazione del pulsante "indietro" e chiusura dell'attività al click
        backButton = findViewById(R.id.back_button_piechart)
        backButton.setOnClickListener {
            finish()
        }

        // Inizializzazione del database e del repository
        val database = ActivityDatabase.getDatabase(this)
        val repository = ActivityRepository(database.getDao())

        // Configurazione del ViewModel tramite la ViewModelFactory
        val factory = StatsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StatsViewModel::class.java]

        pieChart = findViewById(R.id.PieChart)

        // Osserva i dati delle attività dal ViewModel e aggiorna il grafico a torta
        viewModel.getActivitiesByMonth().observe(this) { activities ->
            activities?.let {
                // Mappa per tenere traccia della durata delle attività per ogni tipo
                val typeMap = HashMap<String, Float>()
                var totalActivities = 0L

                // Calcolo della durata totale per ogni tipo di attività
                for (activity in activities) {
                    var duration = activity.endTimeMillis?.minus(activity.startTimeMillis)!!
                    typeMap[activity.type] = typeMap.getOrDefault(activity.type, 0f) + duration
                    totalActivities += duration
                }

                // Crea le voci del grafico a torta basate sulla percentuale di ciascun tipo
                val pieEntries = ArrayList<PieEntry>()
                for ((type, count) in typeMap) {
                    val percentage = (count / totalActivities) * 100f
                    pieEntries.add(PieEntry(percentage, type))
                }

                // Configura il dataset per il grafico a torta
                val dataSet = PieDataSet(pieEntries, "")
                // Imposta i colori per ogni sezione del grafico
                dataSet.setColors(
                    intArrayOf(
                        R.color.pie1,
                        R.color.pie2,
                        R.color.pie3,
                        R.color.pie4,
                    ), this
                )
                dataSet.valueTextSize = 12f

                // Imposta i dati nel grafico a torta e alcune proprietà di visualizzazione
                val pieData = PieData(dataSet)
                pieChart.data = pieData
                pieChart.description.isEnabled = false
                pieChart.isDrawHoleEnabled = true
                pieChart.setTransparentCircleAlpha(0)
                pieChart.setHoleColor(android.R.color.transparent)
                pieChart.legend.isEnabled = false
                pieChart.animateY(1000)
                pieChart.invalidate()
            }
        }
    }
}