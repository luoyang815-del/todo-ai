package com.example.todoai

import android.app.Application
import com.example.todoai.core.Notifications
import timber.log.Timber

class TodoAiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Notifications.init(this)
    }
}
