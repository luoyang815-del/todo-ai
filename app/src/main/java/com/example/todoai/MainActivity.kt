package com.example.todoai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoai.core.Gateway
import com.example.todoai.core.Notifications
import com.example.todoai.settings.SettingsScreen
import com.example.todoai.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var input by remember { mutableStateOf("") }
                var output by remember { mutableStateOf("") }
                var showSettings by remember { mutableStateOf(false) }

                if (showSettings) {
                    SettingsScreen(
                        onBack = { showSettings = false }
                    )
                } else {
                    Column(Modifier.fillMaxSize().padding(16.dp)) {
                        Text("AI 助手（演示版，功能完整）", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            label = { Text("输入要发送的内容") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Row {
                            Button(onClick = {
                                val ctx = this@MainActivity
                                CoroutineScope(Dispatchers.IO).launch {
                                    val cfg = SettingsStore.read(ctx)
                                    val resp = Gateway.sendText(ctx, cfg, input)
                                    output = resp
                                    Notifications.show(ctx, "AI 助手回复", resp)
                                }
                            }) { Text("发送") }
                            Spacer(Modifier.width(12.dp))
                            OutlinedButton(onClick = { showSettings = true }) { Text("设置") }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("回复：")
                        Text(output)
                    }
                }
            }
        }
    }
}
