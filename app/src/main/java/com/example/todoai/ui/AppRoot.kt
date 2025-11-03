// SPDX-License-Identifier: MIT
package com.example.todoai.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todoai.settings.SettingsPage

@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val items = listOf(
        BottomItem("home", "首页", Icons.Filled.Home),
        BottomItem("todos", "代办", Icons.Filled.ViewList),
        BottomItem("settings", "设置", Icons.Filled.Settings),
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStack by nav.currentBackStackEntryAsState()
                val current = backStack?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick = { nav.navigate(item.route) { launchSingleTop = true } },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { HomePage() }
            composable("todos") { TodoListPage() }
            composable("settings") { SettingsPage() }
        }
    }
}

data class BottomItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
