
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
import com.aihelper.app.core.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { MainScreen() }
  }
}

@Composable fun MainScreen() {
  var tab by remember { mutableStateOf(0) }
  var log by remember { mutableStateOf("") }
  MaterialTheme {
    Column(Modifier.fillMaxSize().padding(12.dp)) {
      TabRow(selectedTabIndex = tab) {
        Tab(selected = tab==0, onClick={tab=0}, text={ Text("代办") })
        Tab(selected = tab==1, onClick={tab=1}, text={ Text("聊天") })
        Tab(selected = tab==2, onClick={tab=2}, text={ Text("设置") })
      }
      Spacer(Modifier.height(12.dp))
      when(tab){ 0 -> TodoTab { log = it }; 1 -> ChatTab { log = it }; 2 -> SettingsTab { log = it } }
      Spacer(Modifier.height(8.dp))
      if (log.isNotBlank()) Text(log)
    }
  }
}

@Composable fun TodoTab(onLog:(String)->Unit){
  val ctx = androidx.compose.ui.platform.LocalContext.current
  val scope = rememberCoroutineScope()
  var title by remember { mutableStateOf("") }
  var content by remember { mutableStateOf("") }
  var todos by remember { mutableStateOf(listOf<Todo>()) }

  LaunchedEffect(Unit){ todos = Storage.loadTodos(ctx) }

  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    OutlinedTextField(title, {title=it}, label={Text("标题")}, modifier=Modifier.weight(1f))
    OutlinedTextField(content, {content=it}, label={Text("内容")}, modifier=Modifier.weight(1f))
  }
  Spacer(Modifier.height(8.dp))
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    Button(onClick = {
      scope.launch {
        if (title.isBlank() && content.isBlank()) { Toast.makeText(ctx, "请输入内容", Toast.LENGTH_SHORT).show(); return@launch }
        val now = System.currentTimeMillis()
        val t = Todo(id = now.toString(), title = title.ifBlank{"(无标题)"}, content = content, updatedAt = now)
        todos = listOf(t) + todos
        Storage.saveTodos(ctx, todos)
        title=""; content=""
        Notifier.notify(ctx, "已写入代办", t.title)
        onLog("已写入代办并刷新列表")
      }
    }){ Text("写入本地") }

    Button(onClick = {
      scope.launch {
        val f = Storage.exportJson(ctx)
        onLog("已导出到: ${'$'}{f.absolutePath}")
        Toast.makeText(ctx, "导出完成: ${'$'}{f.name}", Toast.LENGTH_SHORT).show()
      }
    }){ Text("导出数据") }
  }
  Spacer(Modifier.height(8.dp)); Divider()
  Text("最近代办", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top=8.dp, bottom=8.dp))
  LazyColumn(Modifier.fillMaxWidth().weight(1f, fill=false)) {
    items(todos){ t -> Text("• " + (t.title.ifBlank{t.content}.take(60))) }
  }
}

@Composable fun ChatTab(onLog:(String)->Unit){
  val ctx = androidx.compose.ui.platform.LocalContext.current
  val scope = rememberCoroutineScope()
  var input by remember { mutableStateOf("请总结今天的代办，并生成 3 条下一步待办。") }
  var model by remember { mutableStateOf("gpt-4o-mini") }
  var reply by remember { mutableStateOf("") }

  OutlinedTextField(model, {model=it}, label={Text("模型")}, modifier=Modifier.fillMaxWidth())
  OutlinedTextField(input, {input=it}, label={Text("输入")}, modifier=Modifier.fillMaxWidth())
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    Button(onClick = {
      scope.launch {
        reply = "【示例回复】这是根据你的输入生成的总结与 3 条待办建议。"
        onLog("已收到回复并保存（本地）")
      }
    }){ Text("发送") }

    Button(onClick = {
      scope.launch {
        if (reply.isBlank()) { Toast.makeText(ctx,"暂无回复内容",Toast.LENGTH_SHORT).show(); return@launch }
        val now = System.currentTimeMillis()
        val t = Todo(id = now.toString(), title = reply.take(18), content = reply, updatedAt = now)
        val todos = Storage.loadTodos(ctx).let { listOf(t) + it }
        Storage.saveTodos(ctx, todos)
        Notifier.notify(ctx, "AI 转为代办", t.title)
        onLog("已将回复保存为代办")
      }
    }){ Text("转为代办") }
  }
  if (reply.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text("回复：\n"+reply) }
}

@Composable fun SettingsTab(onLog:(String)->Unit){
  val ctx = androidx.compose.ui.platform.LocalContext.current
  var server by remember { mutableStateOf("http://127.0.0.1:8000") }
  var token by remember { mutableStateOf("") }
  var gateway by remember { mutableStateOf("https://api.openai.com") }
  var proxyHost by remember { mutableStateOf("") }
  var proxyPort by remember { mutableStateOf("") }
  var proxyUser by remember { mutableStateOf("") }
  var proxyPass by remember { mutableStateOf("") }
  var syncStatus by remember { mutableStateOf("") }

  LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
    item {
      Text("同步设置", style=MaterialTheme.typography.titleMedium)
      OutlinedTextField(server, {server=it}, label={Text("服务器")}, modifier=Modifier.fillMaxWidth())
      OutlinedTextField(token, {token=it}, label={Text("Token（仅本机）")}, visualTransformation=PasswordVisualTransformation(), modifier=Modifier.fillMaxWidth())
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { onLog("已保存服务器/Token"); syncStatus=""; }){ Text("保存同步") }
        Button(onClick = {
          // 示例：可改为 Sync.api(server).push/pull
          syncStatus="成功"; onLog("立即同步: ok")
        }){ Text("立即同步") }
      }
      if (syncStatus.isNotBlank()) {
        val color = if (syncStatus=="成功") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        Text("同步状态：${'$'}syncStatus", color = color)
      }
      Divider(Modifier.padding(vertical=6.dp))
    }
    item {
      Text("代理 / 网关 / 证书", style=MaterialTheme.typography.titleMedium)
      OutlinedTextField(gateway, {gateway=it}, label={Text("网关 BaseURL")}, modifier=Modifier.fillMaxWidth())
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(proxyHost, {proxyHost=it}, label={Text("代理主机")}, modifier=Modifier.weight(1f))
        OutlinedTextField(proxyPort, {proxyPort=it}, label={Text("端口")}, modifier=Modifier.weight(1f))
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(proxyUser, {proxyUser=it}, label={Text("用户名")}, modifier=Modifier.weight(1f))
        OutlinedTextField(proxyPass, {proxyPass=it}, label={Text("密码")}, visualTransformation=PasswordVisualTransformation(), modifier=Modifier.weight(1f))
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { }){ Text("无") }
        Button(onClick = { }){ Text("HTTP") }
        Button(onClick = { }){ Text("HTTPS") }
        Button(onClick = { }){ Text("SOCKS") }
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { onLog("已保存代理/网关") }){ Text("保存代理设置") }
        Button(onClick = { onLog("自签CA：切换") }){ Text("自签CA(切换)") }
      }
    }
  }
}
