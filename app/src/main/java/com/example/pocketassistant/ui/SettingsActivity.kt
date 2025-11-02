
package com.example.pocketassistant.ui

import android.widget.Toast
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class SettingsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { SettingsScreen() } }
    }
}

@Composable
fun SettingsScreen(vm: SettingsViewModel = viewModel()) {
    val ctx = LocalContext.current
    val useGateway by vm.useGatewayKey.collectAsState(initial = true)
    val apiKey by vm.apiKey.collectAsState(initial = "")
    val baseUrl by vm.baseUrl.collectAsState(initial = "https://api.openai.com/")
    val model by vm.model.collectAsState(initial = "gpt-4.1-mini")
    val proxyType by vm.proxyType.collectAsState(initial = "NONE")
    val proxyHost by vm.proxyHost.collectAsState(initial = "")
    val proxyPort by vm.proxyPort.collectAsState(initial = 0)
    val proxyUser by vm.proxyUser.collectAsState(initial = "")
    val proxyPass by vm.proxyPass.collectAsState(initial = "")

    var apiKeyLocal by remember { mutableStateOf(apiKey) }
    var baseUrlLocal by remember { mutableStateOf(baseUrl) }
    var modelLocal by remember { mutableStateOf(model) }
    var proxyTypeLocal by remember { mutableStateOf(proxyType) }
    var proxyHostLocal by remember { mutableStateOf(proxyHost) }
    var proxyPortLocal by remember { mutableStateOf(proxyPort.toString()) }
    var proxyUserLocal by remember { mutableStateOf(proxyUser) }
    var proxyPassLocal by remember { mutableStateOf(proxyPass) }
    var useGatewayLocal by remember { mutableStateOf(useGateway) }
    var expanded by remember { mutableStateOf(false) }
    val models = listOf("gpt-4.1-mini","gpt-4o-mini","o3-mini","gpt-4.1")

    Column(Modifier.padding(16.dp)) {
        Text("GPT / Whisper 设置（可指向你的网关；如选“使用网关中的 Key”，App 不再保存本地 Key）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            Checkbox(checked = useGatewayLocal, onCheckedChange = { useGatewayLocal = it })
            Spacer(Modifier.width(8.dp))
            Text("使用网关中的 API Key（推荐）")
        }
        OutlinedTextField(apiKeyLocal, { apiKeyLocal = it }, enabled = !useGatewayLocal,
            label = { Text("OpenAI API Key（若走网关可留空）") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(baseUrlLocal, { baseUrlLocal = it }, label = { Text("Base URL（可填网关）") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Text("模型选择", style = MaterialTheme.typography.titleMedium)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = modelLocal,
                onValueChange = {},
                readOnly = true,
                label = { Text("选择模型") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                models.forEach { m ->
                    DropdownMenuItem(text = { Text(m) }, onClick = {
                        modelLocal = m; expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("代理/网关", style = MaterialTheme.typography.titleMedium)
        Row {
            OutlinedTextField(proxyTypeLocal, { proxyTypeLocal = it }, label = { Text("类型：NONE/HTTP/HTTPS/SOCKS5") })
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(proxyHostLocal, { proxyHostLocal = it }, label = { Text("Host") })
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(proxyPortLocal, { proxyPortLocal = it }, label = { Text("Port") })
        }
        Row {
            OutlinedTextField(proxyUserLocal, { proxyUserLocal = it }, label = { Text("用户名") })
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(proxyPassLocal, { proxyPassLocal = it }, label = { Text("密码") })
        }

        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = {
                vm.save(
                    useGatewayLocal,
                    apiKeyLocal,
                    baseUrlLocal,
                    modelLocal,
                    proxyTypeLocal,
                    proxyHostLocal,
                    proxyPortLocal.toIntOrNull() ?: 0,
                    proxyUserLocal,
                    proxyPassLocal
                )
                Toast.makeText(ctx, "已保存", Toast.LENGTH_SHORT).show()
            }) { Text("保存") }
            Spacer(Modifier.width(12.dp))
            Button(onClick = {
                Toast.makeText(ctx, "测试代理与 API（示例占位）", Toast.LENGTH_SHORT).show()
            }) { Text("测试代理与 API") }
        }
        Spacer(Modifier.height(16.dp))
        Text("提示：勾选“使用网关中的 Key”后，App 不再保存 Key；如需直连 OpenAI，请关闭后填写 Key。")
    }
}
