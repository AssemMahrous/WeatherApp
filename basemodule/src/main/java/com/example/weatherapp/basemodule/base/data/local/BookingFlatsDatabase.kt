package com.example.weatherapp.basemodule.base.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.basemodule.base.data.model.FlatDbEntity

@Database(
    entities = [FlatDbEntity::class],
    version = 3,
    exportSchema = false
)
abstract class weatherappDatabase : RoomDatabase() {

    abstract fun flatsDao(): FlatsDao

    companion object {

        @Volatile
        private var INSTANCE: weatherappDatabase? = null

        fun getInstance(context: Context): weatherappDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                weatherappDatabase::class.java, "weatherapp.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
