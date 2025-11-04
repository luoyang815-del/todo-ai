package com.example.todoai.smart

import android.content.Context
import com.example.todoai.settings.AppSettings

class SmartOrganizer(private val context: Context) {
    private val settings = AppSettings.load(context)
    fun getModel(): String = settings.model
}
