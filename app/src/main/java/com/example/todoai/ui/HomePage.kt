// SPDX-License-Identifier: MIT
package com.example.todoai.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun HomePage() {
    val context = LocalContext.current
    val p = context.getSharedPreferences("todoai_prefs", Context.MODE_PRIVATE)
    var total by remember { mutableStateOf(p.getInt("todo_total", 0)) }
    var important by remember { mutableStateOf(p.getInt("todo_important", 0)) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("首页", style = MaterialTheme.typography.headlineSmall)
        Text("今日代办总数：$total")
        Text("重要代办：$important")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                total += 1
                p.edit().putInt("todo_total", total).apply()
            }) { Text("总数 +1") }
            Button(onClick = {
                important += 1
                p.edit().putInt("todo_important", important).apply()
            }) { Text("重要 +1") }
        }

        Text(
            text = "提示：调整上面两个数值后，重新添加/刷新桌面小组件就能看到统计同步。",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
