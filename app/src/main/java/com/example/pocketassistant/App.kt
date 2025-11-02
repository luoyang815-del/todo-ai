
package com.example.pocketassistant
import android.app.Application
import com.example.pocketassistant.data.AppDatabase
class App: Application() { val db by lazy { AppDatabase.get(this) } }
