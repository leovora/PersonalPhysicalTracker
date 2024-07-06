package com.example.ppt.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long?,
    val stepsCount: Int? = null,
    val kilometers: Float? = null,
)