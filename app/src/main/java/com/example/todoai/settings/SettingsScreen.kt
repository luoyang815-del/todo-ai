package com.example.todoai.settings

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var cfg by remember { mutableStateOf(AppConfig()) }
    LaunchedEffect(Unit) {
        cfg = SettingsStore.read(ctx)
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("设置", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        Row {
            Text("启用代理", modifier = Modifier.weight(1f))
            Switch(checked = cfg.proxyEnabled, onCheckedChange = { cfg = cfg.copy(proxyEnabled = it) })
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(cfg.proxyHost, { cfg = cfg.copy(proxyHost = it) }, label = { Text("代理地址") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(if (cfg.proxyPort==0) "" else cfg.proxyPort.toString(),
            { v -> cfg = cfg.copy(proxyPort = v.toIntOrNull() ?: 0) },
            label = { Text("代理端口") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(cfg.baseUrl, { cfg = cfg.copy(baseUrl = it) }, label = { Text("网关 Base URL") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        Text("同步模式")
        Spacer(Modifier.height(8.dp))
        Row {
            listOf("manual","auto","off").forEach { m ->
                AssistChip(onClick = { cfg = cfg.copy(syncMode = m) }, label = { Text(m) }, modifier = Modifier.padding(end = 8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    SettingsStore.write(ctx, cfg)
                }
                onBack()
            }) { Text("保存") }
            Spacer(Modifier.width(12.dp))
            OutlinedButton(onClick = onBack) { Text("返回") }
        }
    }
}
