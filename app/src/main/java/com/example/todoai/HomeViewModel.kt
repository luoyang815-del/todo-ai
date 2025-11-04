package com.example.todoai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.todoai.settings.AppSettings

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    val settings = AppSettings.load(app.applicationContext)
}
