
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
  OutlinedTextField(value=model, onValueChange={model=it}, label={Text("模型")}, modifier=Modifier.fillMaxWidth())
  OutlinedTextField(value=text, onValueChange={text=it}, label={Text("输入")}, modifier=Modifier.fillMaxWidth())
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    Button(onClick = { scope.launch { Repo(ctx).addMessage("user", text); onLog("已保存聊天（本地）") } }){ Text("保存") }
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
  LaunchedEffect(Unit){ serverFlow(ctx).collect{ server = it } }
  LaunchedEffect(Unit){ gatewayFlow(ctx).collect{ gateway = it } }
  LaunchedEffect(Unit){ lastSyncTodoFlow(ctx).collect{ lastTodo = it } }
  LaunchedEffect(Unit){ lastSyncMsgFlow(ctx).collect{ lastMsg = it } }
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text("同步设置", style=MaterialTheme.typography.titleMedium)
    OutlinedTextField(server, {server=it}, label={Text("服务器")}, modifier=Modifier.fillMaxWidth())
    OutlinedTextField(token, {token=it}, label={Text("Token（仅本机）")}, visualTransformation=PasswordVisualTransformation(), modifier=Modifier.fillMaxWidth())
    Text("last_sync:  todos=%d, messages=%d".format(lastTodo, lastMsg))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = { scope.launch { saveServer(ctx, server); encryptedPrefs(ctx).edit().putString("token", token).apply(); onLog("已保存服务器/Token") } }){ Text("保存同步") }
      Button(onClick = {
        scope.launch {
          val proxy = proxyFlow(ctx).first()
          val tk = encryptedPrefs(ctx).getString("token","") ?: ""
          val res = Repo(ctx).syncNow(tk, server, proxy, lastTodo, lastMsg)
          onLog("立即同步: "+res); Noti.notify(ctx,"同步",res)
        }
      }){ Text("立即同步") }
    }
  }
}
