// SPDX-License-Identifier: MIT
package com.example.todoai.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todoai.data.TodoRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditPage(id: Long, onDone: () -> Unit) {
    val ctx = LocalContext.current
    val origin = remember(id) { TodoRepo.getById(ctx, id) }
    var title by remember { mutableStateOf(origin?.title ?: "") }
    var content by remember { mutableStateOf(origin?.content ?: "") }
    var important by remember { mutableStateOf(origin?.important ?: false) }
    var processed by remember { mutableStateOf(origin?.processed ?: false) }

    Scaffold(topBar = { TopAppBar(title = { Text("编辑代办") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("内容") }, minLines = 4, modifier = Modifier.fillMaxWidth())
            Row {
                AssistChip(onClick = { important = !important }, label = { Text(if (important) "重要 ✓" else "设为重要") })
                Spacer(Modifier.width(12.dp))
                AssistChip(onClick = { processed = !processed }, label = { Text(if (processed) "已处理 ✓" else "未处理") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    if (id > 0) {
                        TodoRepo.updateById(ctx, id, title.trim(), content.trim(), important, processed)
                        Toast.makeText(ctx, "已保存", Toast.LENGTH_SHORT).show()
                        onDone()
                    } else {
                        Toast.makeText(ctx, "未找到条目", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("保存") }
                OutlinedButton(onClick = { onDone() }) { Text("取消") }
            }
        }
    }
}
