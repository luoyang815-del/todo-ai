package com.example.todoai.net

import android.content.Context
import com.example.todoai.settings.AppSettings

object OpenAIClient {
    fun getApiKey(context: Context): String {
        val s = AppSettings.load(context)
        return s.apiKey.trim()
    }
}
