
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
  override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MainScreen() } }
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
}
@Composable fun ChatTab(onLog:(String)->Unit){ Text("聊天占位") }
@Composable fun SettingsTab(onLog:(String)->Unit){ Text("设置占位") }
