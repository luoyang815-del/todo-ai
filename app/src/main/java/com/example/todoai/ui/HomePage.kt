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
import com.example.todoai.data.TodoRepo
import com.example.todoai.net.OpenAIClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomePage(vm: HomeViewModel = viewModel()) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
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
    var bulkRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("首页", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("输入代办或原始文本（两种模式见下）") },
            minLines = 3
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                val text = input.text.trim()
                if (text.isNotBlank()) {
                    TodoRepo.addLocal(ctx, text)
                    TodoWidgetProvider.refresh(ctx)
                    Toast.makeText(ctx, "已写入本地代办", Toast.LENGTH_SHORT).show()
                }
            }) { Text("直接入库（本地）") }

            Button(onClick = { requestPinWidget(ctx) }) { Text("添加到桌面小组件") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { vm.send(ctx, settings, input.text.trim()) }) { Text("发送到 OpenAI 并通知") }

            Button(enabled = !bulkRunning, onClick = {
                bulkRunning = true
                scope.launch {
                    try {
                        val un = withContext(Dispatchers.IO) { TodoRepo.unprocessed(ctx) }
                        if (un.isEmpty()) {
                            Toast.makeText(ctx, "没有需要整理的条目", Toast.LENGTH_SHORT).show()
                        } else {
                            val client = OpenAIClient()
                            val plain = un.joinToString("\n") { it.content }
                            val prompt = "请把以下多条文本整理成待办清单，输出为每行一个简洁的待办（不要编号、不要解释）：\n" + plain
                            val reply = withContext(Dispatchers.IO) { client.chatOnce(settings, prompt) }
                            val todos = reply.split('\n').map { it.trim() }.filter { it.isNotBlank() }
                            withContext(Dispatchers.IO) { TodoRepo.addBatch(ctx, todos) }
                            TodoWidgetProvider.refresh(ctx)
                            Toast.makeText(ctx, "已整理并写入 " + todos.size + " 条代办", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Throwable) {
                        Toast.makeText(ctx, "整理失败：" + (e.message ?: "未知错误"), Toast.LENGTH_LONG).show()
                    } finally {
                        bulkRunning = false
                    }
                }
            }) { Text(if (bulkRunning) "整理中…" else "整理未处理并入库") }
        }

        Divider()

        Text("最近 10 条（仅展示会话）：", style = MaterialTheme.typography.titleMedium)
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
    val cn = ComponentName(ctx, com.example.todoai.widget.TodoWidgetProvider::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(cn, null, null)
        Toast.makeText(ctx, "已请求添加，请在弹出的系统对话框确认。", Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(ctx, "你的桌面不支持一键固定，请长按桌面空白处 → 添加小组件 → Todo AI。", Toast.LENGTH_LONG).show()
    }
}
