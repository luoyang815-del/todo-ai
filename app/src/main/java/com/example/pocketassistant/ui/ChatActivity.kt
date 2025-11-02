
package com.example.pocketassistant.ui
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
data class Message(val role: String, val content: String)
class ChatActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { ChatScreen() } }
    }
}
@Composable
fun ChatScreen(vm: ChatViewModel = viewModel(factory = ChatViewModel.factory(application = null))) {
    val messages by vm.messages.collectAsState(initial = emptyList())
    var input by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text("智能对话 · 代办整理") }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(Modifier.weight(1f).padding(12.dp)) {
                items(messages) { m ->
                    Card(Modifier.padding(vertical = 6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(if (m.role == "user") "我" else "助手", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(m.content)
                        }
                    }
                }
            }
            Row(Modifier.padding(12.dp)) {
                OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), label = { Text("对 GPT 说点什么…") })
                Spacer(Modifier.width(8.dp))
                Button(onClick = { val t = input.trim(); if (t.isNotEmpty()) { vm.send(t); input = "" } }) { Text("发送") }
            }
        }
    }
}
