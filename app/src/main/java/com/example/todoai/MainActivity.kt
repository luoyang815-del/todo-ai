package com.example.todoai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.compose.foundation.layout.fillMaxSize
import com.example.todoai.ui.ChatScreen
import com.example.todoai.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = true,
                            onClick = { navController.navigate("chat") },
                            label = { Text("聊天") },
                            icon = {}
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("settings") },
                            label = { Text("设置") },
                            icon = {}
                        )
                    }
                }
            ) {
                NavHost(navController, startDestination = "chat", Modifier.fillMaxSize()) {
                    composable("chat") { ChatScreen() }
                    composable("settings") { SettingsScreen() }
                }
            }
        }
    }
}
