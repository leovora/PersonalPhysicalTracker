package com.example.ppt.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ppt.R
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.ActivityRepository
import com.example.ppt.viewModels.StatsViewModel
import com.example.ppt.other.StatsViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*
import kotlin.collections.ArrayList

class LineChartActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var lineChart: LineChart
    private lateinit var viewModel: StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linechart)

        backButton = findViewById(R.id.back_button_linechart)
        backButton.setOnClickListener {
            finish()
        }

        val database = ActivityDatabase.getDatabase(this)
        val repository = ActivityRepository(database.getDao())
        val factory = StatsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StatsViewModel::class.java]

        lineChart = findViewById(R.id.LineChart)

        //X Axis
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 2f
        xAxis.labelCount = 31

        //Y Axis
        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false

        viewModel.getActivitiesByMonth().observe(this, Observer { activities ->
            activities?.let {
                val stepsPerDay = mutableMapOf<Int, Int>()
                val calendar = Calendar.getInstance()

                for (day in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    stepsPerDay[day] = 0
                }

                for (activity in activities) {
                    if (activity.type == "Walking") {
                        calendar.timeInMillis = activity.startTimeMillis
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        stepsPerDay[day] = stepsPerDay.getOrDefault(day, 0) + activity.stepsCount!!
                    }
                }

                val entries = ArrayList<Entry>()
                for ((day, steps) in stepsPerDay) {
                    entries.add(Entry(day.toFloat(), steps.toFloat()))
                }

                val dataSet = LineDataSet(entries, "")
                dataSet.color = R.color.green
                val lineData = LineData(dataSet)
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