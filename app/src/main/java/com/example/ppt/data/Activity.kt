package com.example.ppt.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta un'entità della tabella "activities" nel database Room.
 */
@Entity(tableName = "activities")
data class Activity(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // Tipo di attività
    val type: String,

    //Inizio dell'attività in millisecondi
    val startTimeMillis: Long,

    // Fine dell'attività in millisecondi (può essere nullo)
    val endTimeMillis: Long?,

    // Numero di passi effettuati durante l'attività (può essere null, non tutte le attività contano i passi)
    val stepsCount: Int? = null,

    // Distanza percorsa durante l'attività in chilometri (può essere null, dipende dall'attività)
    val kilometers: Float? = null,
)