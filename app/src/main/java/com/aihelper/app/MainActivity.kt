package com.aihelper.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
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
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("代办") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("聊天") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("设置") })
            }
            Spacer(Modifier.height(12.dp))
            when (tab) {
                0 -> TodoTab { log = it }
                1 -> ChatTab { log = it }
                2 -> SettingsTab { log = it }
            }
            Spacer(Modifier.height(8.dp))
            if (log.isNotBlank()) Text(log)
        }
    }
}

@Composable
fun TodoTab(onLog: (String) -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var todos by remember { mutableStateOf(listOf<Todo>()) }

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

    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = {
            scope.launch {
                if (title.isBlank() && content.isBlank()) {
                    Toast.makeText(ctx, "请输入内容", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val now = System.currentTimeMillis() / 1000
                val t = Todo(
                    id = now.toString(),
                    title = title.ifBlank { "(无标题)" },
                    content = content,
                    updated_at = now
                )
                todos = listOf(t) + todos
                title = ""
                content = ""
                onLog("已写入代办并刷新列表")
                Toast.makeText(ctx, "已写入代办", Toast.LENGTH_SHORT).show()
            }
        }) { Text("写入本地") }

        Button(onClick = {
            scope.launch {
                onLog("已导出（示例占位），请替换为 Repo(ctx).exportJson()")
                Toast.makeText(ctx, "导出完成(示例)", Toast.LENGTH_SHORT).show()
            }
        }) { Text("导出数据") }
    }

    Spacer(Modifier.height(8.dp))
    Divider()
    Text(
        "最近代办",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
    ) {
        items(todos) { t ->
            Text("• " + (t.title.ifBlank { t.content }.take(60)))
        }
    }
}

@Composable
fun ChatTab(onLog: (String) -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("请总结今天的代办，并生成 3 条下一步待办。") }
    var model by remember { mutableStateOf("gpt-4o-mini") }
    var reply by remember { mutableStateOf("") }

    OutlinedTextField(
        value = model,
        onValueChange = { model = it },
        label = { Text("模型") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = input,
        onValueChange = { input = it },
        label = { Text("输入") },
        modifier = Modifier.fillMaxWidth()
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = {
            scope.launch {
                reply = "【示例回复】这是根据你的输入生成的总结与 3 条待办建议。"
                onLog("已收到回复并保存（本地）")
            }
        }) { Text("发送") }

        Button(onClick = {
            scope.launch {
                if (reply.isBlank()) {
                    Toast.makeText(ctx, "暂无回复内容", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                onLog("已将回复保存为代办（示例）")
                Toast.makeText(ctx, "已转为代办", Toast.LENGTH_SHORT).show()
            }
        }) { Text("转为代办") }
    }

    if (reply.isNotBlank()) {
        Spacer(Modifier.height(8.dp))
        Text("回复：\n$reply")
    }
}

@Composable
fun SettingsTab(onLog: (String) -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var server by remember { mutableStateOf("http://127.0.0.1:8000") }
    var gateway by remember { mutableStateOf("https://api.openai.com") }
    var proxyHost by remember { mutableStateOf("") }
    var proxyPort by remember { mutableStateOf("") }
    var proxyUser by remember { mutableStateOf("") }
    var proxyPass by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var syncStatus by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onLog("已保存服务器/Token"); syncStatus = "" }) { Text("保存同步") }
                Button(onClick = { onLog("立即同步: ok"); syncStatus = "成功" }) { Text("立即同步") }
            }
            if (syncStatus.isNotBlank()) {
                val color =
                    if (syncStatus == "成功") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                Text("同步状态：$syncStatus", color = color)
            }
            Divider(Modifier.padding(vertical = 6.dp))
        }
        item {
            Text("代理 / 网关 / 证书", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = gateway,
                onValueChange = { gateway = it },
                label = { Text("网关 BaseURL") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = proxyHost,
                    onValueChange = { proxyHost = it },
                    label = { Text("代理主机") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = proxyPort,
                    onValueChange = { proxyPort = it },
                    label = { Text("端口") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = proxyUser,
                    onValueChange = { proxyUser = it },
                    label = { Text("用户名") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = proxyPass,
                    onValueChange = { proxyPass = it },
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { }) { Text("无") }
                Button(onClick = { }) { Text("HTTP") }
                Button(onClick = { }) { Text("HTTPS") }
                Button(onClick = { }) { Text("SOCKS") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onLog("已保存代理/网关") }) { Text("保存代理设置") }
                Button(onClick = { onLog("自签CA：切换") }) { Text("自签CA(切换)") }
            }
        }
    }
}
