package com.example.todoai.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("todo_ai_prefs", Context.MODE_PRIVATE)
    var apiKey by remember { mutableStateOf(prefs.getString("api_key", "") ?: "") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("OpenAI API Key") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            prefs.edit().putString("api_key", apiKey).apply()
        }) {
            Text("保存")
        }
    }
}
