package com.example.todoai.net

import android.content.Context
import com.example.todoai.settings.AppSettings

object OpenAIClient {
    fun getApiKey(context: Context): String {
        return AppSettings.load(context).apiKey.trim()
    }
}
