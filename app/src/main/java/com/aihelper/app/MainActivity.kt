
package com.aihelper.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    var tab by remember { mutableStateOf(0) }
    var log by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            TabRow(selectedTabIndex = tab) {
                Tab(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    text = { Text("代办") }
                )
                Tab(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    text = { Text("聊天") }
                )
                Tab(
                    selected = tab == 2,
                    onClick = { tab = 2 },
                    text = { Text("设置") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (tab) {
                0 -> TodoTab { msg -> log = msg }
                1 -> ChatTab { msg -> log = msg }
                2 -> SettingsTab { msg -> log = msg }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(log)
        }
    }
}

@Composable
fun TodoTab(onLog: (String) -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("示例代办") }
    var content by remember { mutableStateOf("来自安卓端") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("标题") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("内容") },
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                scope.launch {
                    Repo(ctx).addTodo(title, content)
                    onLog("已写入代办")
                    Noti.notify(ctx, "代办", "已写入本地")
                }
            }
        ) {
            Text("写入本地")
        }

        Button(
            onClick = {
                scope.launch {
                    val f = Repo(ctx).exportJson()
                    onLog("已导出: ${f.absolutePath}")
                    Noti.notify(ctx, "导出", "JSON 已生成")
                }
            }
        ) {
            Text("导出数据")
        }
    }
}

@Composable
fun ChatTab(onLog: (String) -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf("你好，帮我总结今天代办") }
    var model by remember { mutableStateOf("gpt-4o-mini") }

    OutlinedTextField(
        value = model,
        onValueChange = { model = it },
        label = { Text("模型") },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("输入") },
        modifier = Modifier.fillMaxWidth()
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                scope.launch {
                    Repo(ctx).addMessage("user", text)
                    onLog("已保存聊天（本地）")
                }
            }
        ) {
            Text("保存")
        }
    }
}

@Composable
fun SettingsTab(onLog: (String) -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var server by remember { mutableStateOf("http://127.0.0.1:8000") }
    var token by remember { mutableStateOf("") }
    var lastTodo by remember { mutableStateOf(0L) }
    var lastMsg by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        serverFlow(ctx).collect { server = it }
    }
    LaunchedEffect(Unit) {
        lastSyncTodoFlow(ctx).collect { lastTodo = it }
    }
    LaunchedEffect(Unit) {
        lastSyncMsgFlow(ctx).collect { lastMsg = it }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("同步设置", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = server,
            onValueChange = { server = it },
            label = { Text("服务器") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Token（仅本机）") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Text("last_sync:  todos=$lastTodo, messages=$lastMsg")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        saveServer(ctx, server)
                        encryptedPrefs(ctx).edit().putString("token", token).apply()
                        onLog("已保存服务器/Token")
                    }
                }
            ) {
                Text("保存同步")
            }

            Button(
                onClick = {
                    scope.launch {
                        val proxy = proxyFlow(ctx).first()
                        val tk = encryptedPrefs(ctx).getString("token", "") ?: ""
                        val res = Repo(ctx).syncNow(
                            token = tk,
                            server = server,
                            proxy = proxy,
                            sinceTodo = lastTodo,
                            sinceMsg = lastMsg
                        )
                        onLog("立即同步: $res")
                        Noti.notify(ctx, "同步", res)
                    }
                }
            ) {
                Text("立即同步")
            }
        }
    }
}
