
package com.example.pocketassistant.data
import android.content.Context
import androidx.room.*
@Database(entities=[Entry::class,Event::class],version=2,exportSchema=false)
abstract class AppDatabase: RoomDatabase(){
  abstract fun entryDao(): EntryDao
  abstract fun eventDao(): EventDao
  companion object{
    @Volatile private var INSTANCE: AppDatabase?=null
    fun get(context: Context): AppDatabase =
      INSTANCE ?: synchronized(this){
        INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "pocket-assistant.db")
          .fallbackToDestructiveMigration().build().also{ INSTANCE=it }
      }
  }
}
