package com.example.ppt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Definizione del database Room per l'entità `Activity`.
 */
@Database(entities = [Activity::class], version = 1, exportSchema = false)
abstract class ActivityDatabase : RoomDatabase() {

    /**
     * Fornisce l'accesso ai metodi definiti in `ActivityDao`.
     */
    abstract fun getDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: ActivityDatabase? = null

        /**
         * Restituisce una singola istanza del database, creando una nuova istanza
         * solo se non esiste già. Utilizza il pattern Singleton.
         */
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