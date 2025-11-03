// SPDX-License-Identifier: MIT
package com.example.todoai.settings

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todoai.net.NetDiag
import kotlinx.coroutines.launch

private const val PREF_FILE = "todoai_prefs"
private fun prefs(context: Context): SharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    val context = LocalContext.current
    val p = remember { prefs(context) }
    val scope = rememberCoroutineScope()
    var testing by remember { mutableStateOf(false) }

    var endpoint by rememberSaveable { mutableStateOf(p.getString("endpoint", "https://api.openai.com") ?: "") }
    var apiKey by rememberSaveable { mutableStateOf(p.getString("api_key", "") ?: "") }
    var useProxy by rememberSaveable { mutableStateOf(p.getBoolean("use_proxy", false)) }
    var proxyType by rememberSaveable { mutableStateOf(p.getString("proxy_type", "HTTP") ?: "HTTP") }
    var proxyHost by rememberSaveable { mutableStateOf(p.getString("proxy_host", "") ?: "") }
    var proxyPortText by rememberSaveable { mutableStateOf(p.getInt("proxy_port", 0).takeIf { it > 0 }?.toString() ?: "") }
    var proxyUser by rememberSaveable { mutableStateOf(p.getString("proxy_user", "") ?: "") }
    var proxyPass by rememberSaveable { mutableStateOf(p.getString("proxy_pass", "") ?: "") }
    var model by rememberSaveable { mutableStateOf(p.getString("model", "gpt-5") ?: "gpt-5") }

    Scaffold(topBar = { TopAppBar(title = { Text("设置") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = endpoint, onValueChange = { endpoint = it }, label = { Text("接口地址") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, label = { Text("API Key") }, modifier = Modifier.fillMaxWidth())
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("使用代理/网关", modifier = Modifier.weight(1f))
                Switch(checked = useProxy, onCheckedChange = { useProxy = it })
            }
            OutlinedTextField(enabled = useProxy, value = proxyHost, onValueChange = { proxyHost = it }, label = { Text("代理Host") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(enabled = useProxy, value = proxyPortText, onValueChange = { proxyPortText = it.filter { c -> c.isDigit() }.take(5) }, label = { Text("端口") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(enabled = useProxy, value = proxyUser, onValueChange = { proxyUser = it }, label = { Text("用户名") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(enabled = useProxy, value = proxyPass, onValueChange = { proxyPass = it }, label = { Text("密码") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("模型名称") }, modifier = Modifier.fillMaxWidth())

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    val port = proxyPortText.toIntOrNull() ?: 0
                    p.edit().putString("endpoint", endpoint).putString("api_key", apiKey)
                        .putBoolean("use_proxy", useProxy)
                        .putString("proxy_type", proxyType).putString("proxy_host", proxyHost)
                        .putInt("proxy_port", port).putString("proxy_user", proxyUser)
                        .putString("proxy_pass", proxyPass).putString("model", model).apply()
                    Toast.makeText(context, "已保存设置", Toast.LENGTH_SHORT).show()
                }) { Text("保存设置") }

                Button(enabled = !testing, onClick = {
                    testing = true
                    val port = proxyPortText.toIntOrNull() ?: 0
                    val s = AppSettings(endpoint, apiKey, useProxy, proxyType, proxyHost, port, proxyUser, proxyPass, model)
                    scope.launch {
                        val r = NetDiag.ping(s)
                        Toast.makeText(context, r, Toast.LENGTH_LONG).show()
                        testing = false
                    }
                }) {
                    if (testing) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("连通性测试")
                }
            }
        }
    }
}

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
