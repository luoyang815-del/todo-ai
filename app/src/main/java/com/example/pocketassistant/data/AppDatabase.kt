
package com.example.pocketassistant.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Entry::class, Event::class], version = 2, exportSchema = false)
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
                )
                // Demo/CI 环境下直接破坏式迁移，避免旧版 schema 导致崩溃
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
    }
}
