// SPDX-License-Identifier: MIT
package com.example.pocketassistant.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pocketassistant.settings.SettingsPage

@Composable
fun AppRoot() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SettingsPage()
    }
}
