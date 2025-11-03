// SPDX-License-Identifier: MIT
package com.example.todoai.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todoai.data.TodoRepo
import com.example.todoai.widget.TodoWidgetProvider

@Composable
fun TodoListPage() {
    val ctx = LocalContext.current
    var list by remember { mutableStateOf(TodoRepo.all(ctx)) }
    var filterImportant by remember { mutableStateOf(false) }
    var filterUnprocessed by remember { mutableStateOf(false) }

    fun refresh() { list = TodoRepo.all(ctx) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("代办列表", style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(checked = filterImportant, onCheckedChange = { filterImportant = it }) { Text("只看重要") }
            FilterChip(checked = filterUnprocessed, onCheckedChange = { filterUnprocessed = it }) { Text("只看未处理") }
            OutlinedButton(onClick = { refresh(); TodoWidgetProvider.refresh(ctx) }) { Text("刷新") }
        }

        val shown = list.filter { (!filterImportant || it.important) && (!filterUnprocessed || !it.processed) }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(shown) { idx, item ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(item.title.ifBlank { "（无标题）" }, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            IconButton(onClick = {
                                val indexInAll = list.indexOf(item)
                                TodoRepo.toggleImportant(ctx, indexInAll)
                                refresh(); TodoWidgetProvider.refresh(ctx)
                            }) {
                                if (item.important) Icon(Icons.Filled.Star, contentDescription = "取消重要") else Icon(Icons.Outlined.StarOutline, contentDescription = "设为重要")
                            }
                            IconButton(onClick = {
                                val indexInAll = list.indexOf(item)
                                TodoRepo.delete(ctx, indexInAll)
                                refresh(); TodoWidgetProvider.refresh(ctx)
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "删除")
                            }
                        }
                        Text(item.content)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            val processedText = if (item.processed) "标记未处理" else "标记已处理"
                            OutlinedButton(onClick = {
                                val indexInAll = list.indexOf(item)
                                TodoRepo.toggleProcessed(ctx, indexInAll)
                                refresh(); TodoWidgetProvider.refresh(ctx)
                            }) { Text(processedText) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(checked: Boolean, onCheckedChange: (Boolean) -> Unit, content: @Composable () -> Unit) {
    AssistChip(
        onClick = { onCheckedChange(!checked) },
        label = { content() },
        leadingIcon = if (checked) { { Icon(Icons.Filled.Star, contentDescription = null) } } else null
    )
}
