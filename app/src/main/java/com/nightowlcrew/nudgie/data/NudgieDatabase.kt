package com.nightowlcrew.nudgie.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HabitEntity::class, HabitLogEntity::class, ScreenTimeRecord::class], version = 2, exportSchema = false)
abstract class NudgieDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun screenTimeDao(): ScreenTimeDao

    companion object {
        @Volatile
        private var INSTANCE: NudgieDatabase? = null

        fun getDatabase(context: Context): NudgieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NudgieDatabase::class.java,
                    "nudgie_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
