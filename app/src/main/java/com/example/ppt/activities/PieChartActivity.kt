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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piechart)

        backButton = findViewById(R.id.back_button_piechart)
        backButton.setOnClickListener {
            finish()
        }

        val database = ActivityDatabase.getDatabase(this)
        val repository = ActivityRepository(database.getDao())

        val factory = StatsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StatsViewModel::class.java]

        pieChart = findViewById(R.id.PieChart)

        viewModel.getActivitiesByMonth().observe(this) { activities ->
            activities?.let {
                val typeMap = HashMap<String, Float>()
                var totalActivities = 0L

                for (activity in activities) {
                    var duration = activity.endTimeMillis?.minus(activity.startTimeMillis)!!
                    typeMap[activity.type] = typeMap.getOrDefault(activity.type, 0f) + duration
                    totalActivities += duration
                }

                val pieEntries = ArrayList<PieEntry>()
                for ((type, count) in typeMap) {
                    val percentage = (count / totalActivities) * 100f
                    pieEntries.add(PieEntry(percentage, type))
                }

                val dataSet = PieDataSet(pieEntries, "")
                dataSet.setColors(
                    intArrayOf(
                        R.color.pie1,
                        R.color.pie2,
                        R.color.pie3,
                        R.color.pie4,
                    ), this
                )
                dataSet.valueTextSize = 12f

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