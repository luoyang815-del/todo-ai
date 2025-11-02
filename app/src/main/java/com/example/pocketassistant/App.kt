
package com.example.pocketassistant
import android.app.Application
import com.example.pocketassistant.data.AppDatabase
import com.example.pocketassistant.notify.Notifier
class App: Application() { 
  val db by lazy { AppDatabase.get(this) }
  override fun onCreate() { super.onCreate(); Notifier.ensureChannel(this) }
}
