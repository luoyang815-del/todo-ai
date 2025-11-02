
package com.example.pocketassistant.ui
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pocketassistant.model.Message
class ChatActivity: ComponentActivity(){
  override fun onCreate(savedInstanceState: Bundle?){ super.onCreate(savedInstanceState); setContent{ MaterialTheme{ val vm: ChatViewModel = viewModel(factory=ChatViewModel.provideFactory(application)); ChatScreen(vm) } } }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(vm: ChatViewModel){
  val messages by vm.messages.collectAsState(initial= emptyList<Message>()); var input by remember{ mutableStateOf("") }
  Scaffold(topBar={ TopAppBar(title={ Text("智能对话 · 代办整理") }) }){ padding->
    Column(Modifier.padding(padding).fillMaxSize()){
      LazyColumn(Modifier.weight(1f).padding(12.dp)){ items(messages){ m:Message ->
        Card(Modifier.padding(vertical=6.dp)){ Column(Modifier.padding(12.dp)){ Text(if(m.role=="user") "我" else "助手", style=MaterialTheme.typography.labelMedium); Spacer(Modifier.height(4.dp)); Text(m.content + (m.description?.let{"\n"+it} ?: "")) } }
      } }
      Row(Modifier.padding(12.dp)){ OutlinedTextField(value=input, onValueChange={input=it}, modifier=Modifier.weight(1f), label={ Text("对 GPT 说点什么…") }); Spacer(Modifier.width(8.dp)); Button(onClick={ val t=input.trim(); if(t.isNotEmpty()){ vm.send(t); input="" } }){ Text("发送") } }
    }
  }
}
