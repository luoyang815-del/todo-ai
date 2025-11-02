
package com.example.pocketassistant.ui
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pocketassistant.data.Entry
import com.example.pocketassistant.data.Event
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val vm: MainViewModel = viewModel(factory = MainViewModel.factory(this))
                MainScreen(vm)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel) {
    var text by remember { mutableStateOf("") }
    val entries by vm.entries.collectAsState(initial = emptyList())
    val events by vm.events.collectAsState(initial = emptyList())
    Scaffold(
        topBar = { TopAppBar(
            title = { Text("随身助手") },
            actions = {
                TextButton(onClick = { vm.goChat() }) { Text("聊天") }
                TextButton(onClick = { vm.goSettings() }) { Text("设置") }
            }
        ) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            OutlinedTextField(value = text, onValueChange = { text = it },
                label = { Text("一句话快速记录（如：周三14:00评审立体库标书）") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row {
                Button(onClick = { vm.addQuick(text); text = "" }) { Text("保存") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.addQuickAndParse(text); text = "" }) { Text("保存并智能整理") }
            }
            Spacer(Modifier.height(16.dp))
            Text("即将提醒/事件", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(Modifier.weight(1f)) {
                items(events) { e: Event ->
                    ListItem(
                        headlineContent = { Text(e.title) },
                        supportingContent = { Text((e.description ?: "") + when (e.priority) {2->"（紧急）";1->"（重要）";else->""}) }
                    )
                    Divider()
                }
            }
            if (entries.isEmpty()) Text("还没有事项，先随手记一条吧～", style = MaterialTheme.typography.labelMedium)
            Text("最近记录", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(entries) { it: Entry ->
                    ListItem(headlineContent = { Text(it.rawText) }, supportingContent = { Text("来源：" + it.source) })
                    Divider()
                }
            }
        }
    }
}
