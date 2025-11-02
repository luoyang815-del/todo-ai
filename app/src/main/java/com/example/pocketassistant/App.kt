
package com.example.pocketassistant

import android.app.Application
import com.example.pocketassistant.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App: Application() {
    val appScope = CoroutineScope(SupervisorJob())
    val db by lazy { AppDatabase.get(this) }
}
