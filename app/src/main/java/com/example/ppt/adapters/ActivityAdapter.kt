package com.example.ppt.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ppt.R
import com.example.ppt.data.Activity
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter : ListAdapter<Activity, ActivityAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = getItem(position)
        holder.bind(activity)
    }

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeTextView: TextView = itemView.findViewById(R.id.ActivityType_text)
        private val dateTextView: TextView = itemView.findViewById(R.id.ActivityDate_text)
        private val durationTextView: TextView = itemView.findViewById(R.id.ActivityDuration_text)
        private val stepsTextView: TextView = itemView.findViewById(R.id.ActivitySteps_text)
        private val kilometersTextView: TextView = itemView.findViewById(R.id.ActivityDistance_text)

        fun bind(activity: Activity) {
            typeTextView.text = activity.type
            dateTextView.text = formatDate(activity.startTimeMillis)

            Log.d("ActivityAdapter", "startTimeMillis: ${activity.startTimeMillis}, endTimeMillis: ${activity.endTimeMillis}")

            activity.endTimeMillis?.let { endTime ->
                val durationMillis = endTime - activity.startTimeMillis
                durationTextView.text = "Duration: ${millisToMinutes(durationMillis)} mins"
            } ?: run {
                durationTextView.text = "Duration: N/A"
            }

            if (activity.stepsCount != null) {
                stepsTextView.visibility = View.VISIBLE
                stepsTextView.text = "Steps: ${activity.stepsCount}"
            } else {
                stepsTextView.visibility = View.GONE
            }

            if (activity.kilometers != null) {
                kilometersTextView.visibility = View.VISIBLE
                kilometersTextView.text = "Distance: ${activity.kilometers} km"
            } else {
                kilometersTextView.visibility = View.GONE
            }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy \n HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        private fun millisToMinutes(millis: Long): Long {
            return millis / 60000
        }
    }

    class ActivityDiffCallback : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }
    }
}