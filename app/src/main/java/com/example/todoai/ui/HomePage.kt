// SPDX-License-Identifier: MIT
package com.example.todoai.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoai.home.HomeViewModel
import com.example.todoai.home.ChatItem
import com.example.todoai.settings.AppSettings
import com.example.todoai.widget.TodoWidgetProvider

@Composable
fun HomePage(vm: HomeViewModel = viewModel()) {
    val ctx = LocalContext.current
    val p = ctx.getSharedPreferences("todoai_prefs", Context.MODE_PRIVATE)

    val settings = remember {
        AppSettings(
            endpoint = p.getString("endpoint", "https://api.openai.com") ?: "https://api.openai.com",
            apiKey = p.getString("api_key", "") ?: "",
            useProxy = p.getBoolean("use_proxy", false),
            proxyType = p.getString("proxy_type", "HTTP") ?: "HTTP",
            proxyHost = p.getString("proxy_host", "") ?: "",
            proxyPort = try { p.getInt("proxy_port", 0) } catch (_: Exception) { (p.getString("proxy_port", "0") ?: "0").toIntOrNull() ?: 0 },
            proxyUser = p.getString("proxy_user", "") ?: "",
            proxyPass = p.getString("proxy_pass", "") ?: "",
            model = p.getString("model", "gpt-5") ?: "gpt-5"
        )
    }

    val items by vm.items.collectAsState()
    var input by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("首页", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("向 GPT 发送内容（走设置里的代理/网关与模型）") },
            minLines = 3
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { vm.send(ctx, settings, input.text.trim()) }) { Text("发送到 OpenAI 并通知") }
            Button(onClick = { requestPinWidget(ctx) }) { Text("添加到桌面小组件") }
        }

        Divider()

        Text("最近 10 条：", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { itx -> ChatCard(itx) }
        }
    }
}

@Composable
private fun ChatCard(item: ChatItem) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("提问：${item.prompt}")
            Text("回复：${item.reply}")
        }
    }
}

private fun requestPinWidget(ctx: Context) {
    val mgr = AppWidgetManager.getInstance(ctx)
    val cn = ComponentName(ctx, TodoWidgetProvider::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(cn, null, null)
        Toast.makeText(ctx, "已请求添加，请在弹出的系统对话框确认。", Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(ctx, "你的桌面不支持一键固定，请长按桌面空白处 → 添加小组件 → Todo AI。", Toast.LENGTH_LONG).show()
    }
}
