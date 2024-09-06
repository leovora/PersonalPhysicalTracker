package com.example.ppt.fragments

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ppt.R
import com.example.ppt.activities.LineChartActivity
import com.example.ppt.adapters.ActivityAdapter
import com.example.ppt.data.ActivityDatabase
import com.example.ppt.data.ActivityRepository
import com.example.ppt.other.StatsViewModelFactory
import com.example.ppt.viewModels.StatsViewModel
import com.example.ppt.activities.PieChartActivity

class StatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter
    private lateinit var viewModel: StatsViewModel
    private lateinit var calendarView: CalendarView
    private lateinit var typeFilter: Spinner
    private lateinit var durationFilter: EditText
    private lateinit var lineChartCard: CardView
    private lateinit var pieChartCard: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflata il layout per il frammento
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        lineChartCard = view.findViewById(R.id.LineChart_card)
        pieChartCard = view.findViewById(R.id.PieChart_card)

        // Imposta i listener sulle card view
        lineChartCard.setOnClickListener {
            startActivity(Intent(requireContext(), LineChartActivity::class.java))
        }

        pieChartCard.setOnClickListener {
            startActivity(Intent(requireContext(), PieChartActivity::class.java))
        }

        calendarView = view.findViewById(R.id.calendar)
        typeFilter = view.findViewById(R.id.filter_spinner)
        durationFilter = view.findViewById(R.id.DurationFilter_input)

        // Crea e imposta l'adapter per lo spinner del filtro del tipo di di attività
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.activity_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeFilter.adapter = adapter
        }

        // Inizializza il RecyclerView per visualizzare le attività
        recyclerView = view.findViewById(R.id.Activity_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        activityAdapter = ActivityAdapter()
        recyclerView.adapter = activityAdapter

        // Configura il database e il repository per il ViewModel
        val database = ActivityDatabase.getDatabase(requireContext())
        val repository = ActivityRepository(database.getDao())
        val factory = StatsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(StatsViewModel::class.java)

        // Osserva le attività della data corrente e le visualizza nel RecyclerView
        viewModel.getActivitiesByDate(System.currentTimeMillis()).observe(viewLifecycleOwner) { activities ->
            activities?.let {
                activityAdapter.submitList(it)
            }
        }

        // Imposta il listener per il cambio di data nel CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateInMillis = calendar.timeInMillis
            viewModel.setDate(dateInMillis)
            updateActivities(dateInMillis, getTypeFilter(), getDurationFilter())
        }

        // Imposta il listener per il filtro del tipo di attività
        typeFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateActivities(viewModel.selectedDate.value ?: System.currentTimeMillis(), getTypeFilter(), getDurationFilter())
                viewModel.setType(getTypeFilter())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Non fare nulla
            }
        }

        // Imposta il listener per il filtro della durata
        durationFilter.doOnTextChanged { _, _, _, _ ->
            updateActivities(viewModel.selectedDate.value ?: System.currentTimeMillis(), getTypeFilter(), getDurationFilter())
            viewModel.setDuration(getDurationFilter())
        }

        return view
    }

    // Funzione per aggiornare la lista delle attività in base ai filtri selezionati
    private fun updateActivities(dateInMillis: Long, type: String?, duration: Int?) {
        viewModel.getFilteredActivities(dateInMillis, type, duration).observe(viewLifecycleOwner) { activities ->
            activities?.let {
                activityAdapter.submitList(it)
            }
        }
    }

    // Funzione per ottenere il tipo di attività selezionato dallo Spinner
    private fun getTypeFilter(): String? {
        return typeFilter.selectedItem?.toString()
    }

    // Funzione per ottenere la durata inserita nella EditText, se presente
    private fun getDurationFilter(): Int? {
        val durationText = durationFilter.text.toString()
        return if (durationText.isNotEmpty()) durationText.toInt() else null
    }

}