// SPDX-License-Identifier: MIT
package com.example.pocketassistant.settings

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
import com.example.pocketassistant.smart.SmartOrganizer

private const val PREF_FILE = "pocket_prefs"

private fun prefs(context: Context): SharedPreferences =
    context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

private fun loadString(p: SharedPreferences, key: String, def: String = ""): String =
    p.getString(key, def) ?: def

private fun loadInt(p: SharedPreferences, key: String, def: Int = 0): Int =
    try { p.getInt(key, def) } catch (_: Exception) {
        val s = p.getString(key, null)
        s?.toIntOrNull() ?: def
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    val context = LocalContext.current
    val p = remember { prefs(context) }

    val proxyTypes = listOf("HTTP", "HTTPS", "SOCKS5", "Gateway")
    val models = listOf("gpt-3.5-turbo", "gpt-4-turbo", "gpt-4o", "gpt-4o-mini", "gpt-5", "gpt-5-turbo")

    var endpoint by rememberSaveable { mutableStateOf("") }
    var apiKey by rememberSaveable { mutableStateOf("") }
    var proxyType by rememberSaveable { mutableStateOf(proxyTypes.first()) }
    var proxyHost by rememberSaveable { mutableStateOf("") }
    var proxyPortText by rememberSaveable { mutableStateOf("") }
    var proxyUser by rememberSaveable { mutableStateOf("") }
    var proxyPass by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf(models.first()) }

    LaunchedEffect(Unit) {
        endpoint = loadString(p, "endpoint", "https://api.openai.com")
        apiKey = loadString(p, "api_key", "")
        proxyType = loadString(p, "proxy_type", proxyTypes.first()).let { t -> if (t in proxyTypes) t else proxyTypes.first() }
        proxyHost = loadString(p, "proxy_host", "")
        val port = loadInt(p, "proxy_port", 0)
        proxyPortText = if (port > 0) port.toString() else ""
        proxyUser = loadString(p, "proxy_user", "")
        proxyPass = loadString(p, "proxy_pass", "")
        model = loadString(p, "model", models.first()).let { m -> if (m in models) m else models.first() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("设置") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = endpoint,
                onValueChange = { endpoint = it },
                label = { Text("接口地址（Endpoint）") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdown(label = "代理/网关类型", options = proxyTypes, selected = proxyType) { proxyType = it }
            ExposedDropdown(label = "模型选择", options = models, selected = model) { model = it }

            OutlinedTextField(
                value = proxyHost,
                onValueChange = { proxyHost = it },
                label = { Text("代理/网关 Host") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = proxyPortText,
                onValueChange = { proxyPortText = it.filter { ch -> ch.isDigit() }.take(5) },
                label = { Text("端口") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = proxyUser,
                onValueChange = { proxyUser = it },
                label = { Text("用户名（可选）") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = proxyPass,
                onValueChange = { proxyPass = it },
                label = { Text("密码（可选）") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    val port = proxyPortText.toIntOrNull() ?: 0
                    p.edit()
                        .putString("endpoint", endpoint.trim())
                        .putString("api_key", apiKey.trim())
                        .putString("proxy_type", proxyType)
                        .putString("proxy_host", proxyHost.trim())
                        .putInt("proxy_port", port)
                        .putString("proxy_user", proxyUser.trim())
                        .putString("proxy_pass", proxyPass)
                        .putString("model", model)
                        .apply()
                    Toast.makeText(context, "已保存设置", Toast.LENGTH_SHORT).show()
                }) {
                    Text("保存设置")
                }

                Button(onClick = {
                    try {
                        val port = proxyPortText.toIntOrNull() ?: 0
                        val settings = AppSettings(
                            endpoint = endpoint.trim(),
                            apiKey = apiKey.trim(),
                            proxyType = proxyType,
                            proxyHost = proxyHost.trim(),
                            proxyPort = port,
                            proxyUser = proxyUser.trim(),
                            proxyPass = proxyPass,
                            model = model
                        )
                        SmartOrganizer.save(context, settings)
                        Toast.makeText(context, "已保存到智能整理", Toast.LENGTH_SHORT).show()
                    } catch (e: Throwable) {
                        Toast.makeText(context, "保存失败: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text("保存到智能整理")
                }
            }

            Text(
                text = "提示：代理/网关请通过下拉进行选择；模型已包含 GPT-5。",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelected(option)
                    expanded = false
                })
            }
        }
    }
}

data class AppSettings(
    val endpoint: String,
    val apiKey: String,
    val proxyType: String,
    val proxyHost: String,
    val proxyPort: Int,
    val proxyUser: String,
    val proxyPass: String,
    val model: String
)
