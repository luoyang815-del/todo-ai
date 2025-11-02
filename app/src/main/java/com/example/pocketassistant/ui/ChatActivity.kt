package com.example.pocketassistant.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pocketassistant.model.Message

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val vm: ChatViewModel = viewModel(factory = ChatViewModel.provideFactory(application))
                ChatScreen(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(vm: ChatViewModel) {
    val messages by vm.messages
    var input by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text("Chat · Todo") }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(Modifier.weight(1f).padding(12.dp)) {
                items(messages) { m: Message ->
                    Card(Modifier.padding(vertical = 6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(if (m.role == "user") "Me" else "Assistant", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.width(0.dp))
                            Text(m.content + (m.description?.let { "\n" + it } ?: ""))
                        }
                    }
                }
            }
            Row(Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Say something to GPT...") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val t = input.trim()
                    if (t.isNotEmpty()) {
                        vm.send(t)
                        input = ""
                    }
                }) { Text("Send") }
            }
        }
    }
}
