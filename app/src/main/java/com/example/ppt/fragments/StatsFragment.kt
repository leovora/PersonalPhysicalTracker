package com.example.ppt.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppt.R
import com.example.ppt.adapters.ActivityAdapter
import com.example.ppt.data.Activity
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.ActivityRepository
import com.example.ppt.other.StatsViewModelFactory
import com.example.ppt.viewModels.StatsViewModel

class StatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter
    private lateinit var viewModel: StatsViewModel
    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        // CALENDARVIEW
        calendarView = view.findViewById(R.id.calendar)

        // RECYCLERVIEW
        recyclerView = view.findViewById(R.id.Activity_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        activityAdapter = ActivityAdapter()
        recyclerView.adapter = activityAdapter

        val database = ActivityDatabase.getDatabase(requireContext())
        val repository = ActivityRepository(database.getDao())

        val factory = StatsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(StatsViewModel::class.java)

        // Observe all activities initially
        viewModel.getActivitiesByDate(System.currentTimeMillis()).observe(viewLifecycleOwner) { activities ->
            activities?.let {
                activityAdapter.submitList(it)
            }
        }

        // Set the CalendarView date change listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateInMillis = calendar.timeInMillis
            updateActivitiesByDate(dateInMillis)
        }

        return view
    }

    private fun updateActivitiesByDate(dateInMillis: Long) {
        viewModel.getActivitiesByDate(dateInMillis).observe(viewLifecycleOwner) { activities ->
            activities?.let {
                activityAdapter.submitList(it)
            }
        }
    }
}