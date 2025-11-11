
package com.aihelper.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.aihelper.app.work.SyncWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
class MainActivity : ComponentActivity() { override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { App() } } }
@Composable fun App() {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var tab by remember { mutableStateOf(0) }
    var log by remember { mutableStateOf("") }
    MaterialTheme {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            TabRow(selectedTabIndex = tab) { Tab(selected = tab==0, onClick={tab=0}, text={ Text("代办") }); Tab(selected = tab==1, onClick={tab=1}, text={ Text("聊天") }); Tab(selected = tab==2, onClick={tab=2}, text={ Text("设置") }) }
            Spacer(Modifier.height(12.dp))
            when(tab){ 0 -> TodoTab { log = it }; 1 -> ChatTab { log = it }; 2 -> SettingsTab { log = it } }
            Spacer(Modifier.height(8.dp)); Text(log)
        }
    }
}
@Composable fun TodoTab(onLog:(String)->Unit){
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("示例代办") }
    var content by remember { mutableStateOf("来自安卓端") }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value=title, onValueChange={title=it}, label={Text("标题")}, modifier=Modifier.weight(1f))
        OutlinedTextField(value=content, onValueChange={content=it}, label={Text("内容")}, modifier=Modifier.weight(1f))
    }
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { scope.launch { Repo(ctx).addTodo(title, content); onLog("已写入代办"); Noti.notify(ctx,"代办","已写入本地") } }){ Text("写入本地") }
        Button(onClick = { scope.launch { Repo(ctx).exportJson().let { onLog("已导出: "+it.absolutePath); Noti.notify(ctx,"导出","JSON 已生成") } } ){ Text("导出数据") }
    }
}
@Composable fun ChatTab(onLog:(String)->Unit){
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("你好，帮我总结今天代办") }
    var model by remember { mutableStateOf("gpt-4o-mini") }
    var pinHost by remember { mutableStateOf("") }
    var pinSha256 by remember { mutableStateOf("") }
    var trustCA by remember { mutableStateOf(false) }
    OutlinedTextField(value=model, onValueChange={model=it}, label={Text("模型")}, modifier=Modifier.fillMaxWidth())
    OutlinedTextField(value=text, onValueChange={text=it}, label={Text("输入")}, modifier=Modifier.fillMaxWidth())
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value=pinHost, onValueChange={pinHost=it}, label={Text("Pin 主机（可选）")}, modifier=Modifier.weight(1f))
        OutlinedTextField(value=pinSha256, onValueChange={pinSha256=it}, label={Text("Pin SHA256（去掉sha256/前缀）")}, modifier=Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { FilterChip(selected=trustCA, onClick={ trustCA=!trustCA }, label={ Text("信任自定义 CA（res/raw/ca.pem）") }) }
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { scope.launch { Repo(ctx).addMessage("user", text); onLog("已保存聊天（本地）") } }){ Text("保存") }
        Button(onClick = {
            scope.launch {
                val proxy = proxyFlow(ctx).first()
                val gateway = gatewayFlow(ctx).first()
                val token = encryptedPrefs(ctx).getString("token","") ?: ""
                try { val ans = Repo(ctx).chatOnce(gateway, token, proxy, model, text, pinHost, pinSha256, trustCA); onLog("网关返回: " + ans.take(120)); Noti.notify(ctx, "聊天完成", ans.take(40)) }
                catch (e: Exception) { onLog("发送失败: ${e.message}") }
            }
        }){ Text("发送") }
    }
}
@Composable fun SettingsTab(onLog:(String)->Unit){
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var server by remember { mutableStateOf("http://127.0.0.1:8000") }
    var gateway by remember { mutableStateOf("https://api.openai.com") }
    var proxyType by remember { mutableStateOf(0) }
    var proxyHost by remember { mutableStateOf("") }
    var proxyPort by remember { mutableStateOf("") }
    var proxyUser by remember { mutableStateOf("") }
    var proxyPass by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var lastTodo by remember { mutableStateOf(0L) }
    var lastMsg by remember { mutableStateOf(0L) }
    var pinHost by remember { mutableStateOf("") }
    var pinSha256 by remember { mutableStateOf("") }
    var trustCA by remember { mutableStateOf(false) }
    LaunchedEffect(Unit){ serverFlow(ctx).collect{ server = it } }
    LaunchedEffect(Unit){ gatewayFlow(ctx).collect{ gateway = it } }
    LaunchedEffect(Unit){ lastSyncTodoFlow(ctx).collect{ lastTodo = it } }
    LaunchedEffect(Unit){ lastSyncMsgFlow(ctx).collect{ lastMsg = it } }
    LaunchedEffect(Unit){ pinFlow(ctx).collect{ pinHost = it.first; pinSha256 = it.second } }
    LaunchedEffect(Unit){ trustCAFlow(ctx).collect{ trustCA = it } }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("同步设置（分表增量 + 安全）", style=MaterialTheme.typography.titleMedium)
        OutlinedTextField(server, {server=it}, label={Text("服务器")}, modifier=Modifier.fillMaxWidth())
        OutlinedTextField(token, {token=it}, label={Text("Token（仅本机）")}, visualTransformation=PasswordVisualTransformation(), modifier=Modifier.fillMaxWidth())
        Text("last_sync:  todos=%d, messages=%d".format(lastTodo, lastMsg))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { scope.launch { saveServer(ctx, server); encryptedPrefs(ctx).edit().putString("token", token).apply(); onLog("已保存服务器/Token") } }){ Text("保存同步") }
            Button(onClick = {
                scope.launch {
                    val proxy = proxyFlow(ctx).first()
                    val tk = encryptedPrefs(ctx).getString("token","") ?: ""
                    val res = Repo(ctx).syncNow(tk, server, proxy, lastTodo, lastMsg, pinHost, pinSha256, trustCA)
                    onLog("立即同步: "+res); Noti.notify(ctx,"同步",res)
                }
            }){ Text("立即同步") }
        }
        Divider()
        Text("代理 / 网关 / 安全", style=MaterialTheme.typography.titleMedium)
        OutlinedTextField(gateway, {gateway=it}, label={Text("网关 BaseURL")}, modifier=Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(proxyHost, {proxyHost=it}, label={Text("代理主机")}); OutlinedTextField(proxyPort, {proxyPort=it}, label={Text("端口")}) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(proxyUser, {proxyUser=it}, label={Text("用户名")}); OutlinedTextField(proxyPass, {proxyPass=it}, label={Text("密码")}, visualTransformation=PasswordVisualTransformation()) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { proxyType = 0; onLog("代理=无") }){ Text("无") }
            Button(onClick = { proxyType = 1; onLog("代理=HTTP") }){ Text("HTTP") }
            Button(onClick = { proxyType = 2; onLog("代理=HTTPS") }){ Text("HTTPS") }
            Button(onClick = { proxyType = 3; onLog("代理=SOCKS") }){ Text("SOCKS") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Button(onClick = { scope.launch { saveGateway(ctx, gateway); saveProxy(ctx, proxyType, proxyHost, proxyPort.toIntOrNull()?:0, proxyUser, proxyPass); onLog("已保存代理/网关") } }){ Text("保存代理设置") } }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(pinHost, {pinHost=it}, label={Text("Pin 主机")}, modifier=Modifier.weight(1f))
            OutlinedTextField(pinSha256, {pinSha256=it}, label={Text("Pin SHA256（去掉sha256/）")}, modifier=Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { scope.launch { savePin(ctx, pinHost, pinSha256); onLog("已保存 Pin 配置") } }){ Text("保存 Pin") }
            FilterChip(selected=trustCA, onClick={ trustCA=!trustCA }, label={ Text(if (trustCA) "信任自签 CA：开" else "信任自签 CA：关") })
            Button(onClick = { scope.launch { saveTrustCA(ctx, trustCA); onLog("已保存 CA 信任设置") } }){ Text("保存 CA 设置") }
        }
        Divider()
        Text("自动同步（WorkManager 前台 + 开机自启）", style=MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { SyncWorker.start(ctx, 15); onLog("自动同步：已开启（15 分钟）") }){ Text("开启") }
            Button(onClick = { SyncWorker.stop(ctx); onLog("自动同步：已关闭") }){ Text("关闭") }
        }
    }
}
