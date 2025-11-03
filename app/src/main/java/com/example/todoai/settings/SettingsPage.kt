// SPDX-License-Identifier: MIT
package com.example.todoai.settings

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todoai.smart.SmartOrganizer
import com.example.todoai.net.NetDiag
import kotlinx.coroutines.launch

private const val PREF_FILE = "todoai_prefs"
private fun prefs(context: Context): SharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
private fun loadString(p: SharedPreferences, key: String, def: String = ""): String = p.getString(key, def) ?: def
private fun loadInt(p: SharedPreferences, key: String, def: Int = 0): Int = try { p.getInt(key, def) } catch (_: Exception) {
    p.getString(key, null)?.toIntOrNull() ?: def }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    val context = LocalContext.current
    val p = remember { prefs(context) }
    val scope = rememberCoroutineScope()

    val proxyTypes = listOf("HTTP", "HTTPS", "SOCKS5", "Gateway")
    val models = listOf("gpt-3.5-turbo", "gpt-4-turbo", "gpt-4o", "gpt-4o-mini", "gpt-5", "gpt-5-turbo")

    var endpoint by rememberSaveable { mutableStateOf("") }
    var apiKey by rememberSaveable { mutableStateOf("") }
    var useProxy by rememberSaveable { mutableStateOf(false) }
    var proxyType by rememberSaveable { mutableStateOf(proxyTypes.first()) }
    var proxyHost by rememberSaveable { mutableStateOf("") }
    var proxyPortText by rememberSaveable { mutableStateOf("") }
    var proxyUser by rememberSaveable { mutableStateOf("") }
    var proxyPass by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf(models.first()) }

    LaunchedEffect(Unit) {
        endpoint = loadString(p, "endpoint", "https://api.openai.com")
        apiKey = loadString(p, "api_key", "")
        useProxy = p.getBoolean("use_proxy", false)
        proxyType = loadString(p, "proxy_type", proxyTypes.first()).let { if (it in proxyTypes) it else proxyTypes.first() }
        proxyHost = loadString(p, "proxy_host", "")
        proxyPortText = loadInt(p, "proxy_port", 0).takeIf { it > 0 }?.toString() ?: ""
        proxyUser = loadString(p, "proxy_user", "")
        proxyPass = loadString(p, "proxy_pass", "")
        model = loadString(p, "model", models.first()).let { if (it in models) it else models.first() }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("设置") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = endpoint, onValueChange = { endpoint = it }, label = { Text("接口地址（Endpoint）") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, label = { Text("API Key") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            // 使用代理开关
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("使用代理/网关", modifier = Modifier.weight(1f))
                Switch(checked = useProxy, onCheckedChange = { useProxy = it })
            }

            ExposedDropdown(label = "代理/网关类型", options = proxyTypes, selected = proxyType, enabled = useProxy) { proxyType = it }
            OutlinedTextField(enabled = useProxy, value = proxyHost, onValueChange = { proxyHost = it }, label = { Text("代理/网关 Host") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(enabled = useProxy, value = proxyPortText, onValueChange = { proxyPortText = it.filter { ch -> ch.isDigit() }.take(5) }, label = { Text("端口") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(enabled = useProxy, value = proxyUser, onValueChange = { proxyUser = it }, label = { Text("用户名（可选）") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(enabled = useProxy, value = proxyPass, onValueChange = { proxyPass = it }, label = { Text("密码（可选）") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            ExposedDropdown(label = "模型选择", options = models, selected = model) { model = it }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    val port = proxyPortText.toIntOrNull() ?: 0
                    p.edit()
                        .putString("endpoint", endpoint.trim())
                        .putString("api_key", apiKey.trim())
                        .putBoolean("use_proxy", useProxy)
                        .putString("proxy_type", proxyType)
                        .putString("proxy_host", proxyHost.trim())
                        .putInt("proxy_port", port)
                        .putString("proxy_user", proxyUser.trim())
                        .putString("proxy_pass", proxyPass)
                        .putString("model", model)
                        .apply()
                    Toast.makeText(context, "已保存设置", Toast.LENGTH_SHORT).show()
                }) { Text("保存设置") }

                Button(onClick = {
                    val port = proxyPortText.toIntOrNull() ?: 0
                    val s = AppSettings(endpoint.trim(), apiKey.trim(), useProxy, proxyType, proxyHost.trim(), port, proxyUser.trim(), proxyPass, model)
                    scope.launch {
                        val result = NetDiag.ping(context, s)
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                    }
                }) { Text("连通性测试") }

                Button(onClick = {
                    val port = proxyPortText.toIntOrNull() ?: 0
                    val s = AppSettings(endpoint.trim(), apiKey.trim(), useProxy, proxyType, proxyHost.trim(), port, proxyUser.trim(), proxyPass, model)
                    try {
                        SmartOrganizer.save(context, s)
                        Toast.makeText(context, "已保存到智能整理", Toast.LENGTH_SHORT).show()
                    } catch (e: Throwable) {
                        Toast.makeText(context, "保存失败: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }) { Text("保存到智能整理") }
            }

            Text(text = "提示：代理/网关请通过下拉进行选择；模型已包含 GPT-5。", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdown(label: String, options: List<String>, selected: String, enabled: Boolean = true, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if (enabled) expanded = !expanded }) {
        OutlinedTextField(readOnly = true, value = selected, onValueChange = {},
            label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(), enabled = enabled)
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false }) }
        }
    }
}

// 统一 AppSettings 定义，供全局使用
data class AppSettings(
    val endpoint: String,
    val apiKey: String,
    val useProxy: Boolean,
    val proxyType: String,
    val proxyHost: String,
    val proxyPort: Int,
    val proxyUser: String,
    val proxyPass: String,
    val model: String
)
