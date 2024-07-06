package com.example.ppt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ppt.data.dao.ActivityDao
import com.example.ppt.data.entities.Activity

@Database(entities = [Activity::class], version = 1)
abstract class ActivityDatabase: RoomDatabase() {
    abstract val dao: ActivityDao

    companion object {
        @Volatile private var INSTANCE: ActivityDatabase? = null

        fun getDatabase(context: Context): ActivityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ActivityDatabase::class.java,
                    "activity_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}