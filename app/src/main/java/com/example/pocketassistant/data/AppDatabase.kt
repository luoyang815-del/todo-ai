
package com.example.pocketassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Entry::class, Event::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun eventDao(): EventDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "pocket-assistant.db"
                ).build().also { INSTANCE = it }
            }
    }
}
