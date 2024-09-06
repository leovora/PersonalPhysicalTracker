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

/**
 * Adapter per la gestione e visualizzazione di una lista di oggetti `Activity`
 * utilizzando `RecyclerView`. Estende `ListAdapter`.
 */
class ActivityAdapter : ListAdapter<Activity, ActivityAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    /**
     * Metodo che crea una nuova `ActivityViewHolder` inflatando il layout per l'elemento della lista.
     * @param parent Il `ViewGroup` a cui l'elemento figlio sarà aggiunto.
     * @param viewType Tipo di vista dell'elemento (non utilizzato qui).
     * @return Un nuovo oggetto `ActivityViewHolder`.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ActivityViewHolder(view)
    }

    /**
     * Metodo che associa i dati di un oggetto `Activity` alla `ViewHolder` in una specifica posizione.
     * @param holder L'oggetto `ActivityViewHolder` che contiene la vista.
     * @param position La posizione dell'elemento nell'adapter.
     */
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = getItem(position)
        holder.bind(activity) // Associa i dati dell'attività alla vista
    }

    /**
     * `ViewHolder` che gestisce la visualizzazione dei dati per ogni elemento dell'attività.
     */
    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeTextView: TextView = itemView.findViewById(R.id.ActivityType_text)
        private val dateTextView: TextView = itemView.findViewById(R.id.ActivityDate_text)
        private val durationTextView: TextView = itemView.findViewById(R.id.ActivityDuration_text)
        private val stepsTextView: TextView = itemView.findViewById(R.id.ActivitySteps_text)
        private val kilometersTextView: TextView = itemView.findViewById(R.id.ActivityDistance_text)

        /**
         * Metodo per associare i dati dell'attività alla vista.
         * @param activity L'oggetto attività contenente i dati da visualizzare.
         */
        fun bind(activity: Activity) {
            // Imposta il tipo di attività (es. "Walking", "Running")
            typeTextView.text = activity.type
            // Formatta e visualizza la data e l'ora di inizio dell'attività
            dateTextView.text = formatDate(activity.startTimeMillis)

            Log.d("ActivityAdapter", "startTimeMillis: ${activity.startTimeMillis}, endTimeMillis: ${activity.endTimeMillis}")

            // Calcola e visualizza la durata dell'attività in minuti
            activity.endTimeMillis?.let { endTime ->
                val durationMillis = endTime - activity.startTimeMillis
                durationTextView.text = "Duration: ${millisToMinutes(durationMillis)} mins"
            } ?: run {
                durationTextView.text = "Duration: N/A" // Se manca la durata, visualizza "N/A"
            }

            // Visualizza il numero di passi se disponibile, altrimenti nasconde il campo
            if (activity.stepsCount != null) {
                stepsTextView.visibility = View.VISIBLE
                stepsTextView.text = "Steps: ${activity.stepsCount}"
            } else {
                stepsTextView.visibility = View.GONE
            }

            // Visualizza la distanza in chilometri se disponibile, altrimenti nasconde il campo
            if (activity.kilometers != null) {
                kilometersTextView.visibility = View.VISIBLE
                val formattedKilometers = String.format("%.2f", activity.kilometers) // Approssima a 2 cifre decimali
                kilometersTextView.text = "Distance: $formattedKilometers km"
            } else {
                kilometersTextView.visibility = View.GONE
            }
        }

        /**
         * Formatta il timestamp dell'attività in una stringa leggibile (dd/MM/yyyy HH:mm).
         * @param timestamp Il timestamp in millisecondi.
         * @return La data formattata come stringa.
         */
        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy \n HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        /**
         * Converte i millisecondi in minuti.
         * @param millis La durata in millisecondi.
         * @return La durata in minuti.
         */
        private fun millisToMinutes(millis: Long): Long {
            return millis / 60000
        }
    }

    /**
     * Callback utilizzato per confrontare gli elementi della lista in modo efficiente.
     * Aiuta il `ListAdapter` a determinare se gli elementi della lista sono cambiati.
     */
    class ActivityDiffCallback : DiffUtil.ItemCallback<Activity>() {
        /**
         * Controlla se due elementi `Activity` sono uguali in base al loro ID.
         * @param oldItem Il vecchio elemento.
         * @param newItem Il nuovo elemento.
         * @return `true` se gli elementi sono uguali, `false` altrimenti.
         */
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * Controlla se i contenuti di due elementi `Activity` sono identici.
         * @param oldItem Il vecchio elemento.
         * @param newItem Il nuovo elemento.
         * @return `true` se i contenuti sono uguali, `false` altrimenti.
         */
        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }
    }
}