// SPDX-License-Identifier: MIT
package com.example.todoai.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.todoai.data.TodoItem
import com.example.todoai.data.TodoRepo
import com.example.todoai.widget.TodoWidgetProvider

enum class SortMode { TIME_DESC, TIME_ASC, IMPORTANT_FIRST, UNPROCESSED_FIRST }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListPage(onEdit: (Long) -> Unit) {
    val ctx = LocalContext.current
    var list by remember { mutableStateOf(TodoRepo.all(ctx)) }
    var filterText by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(SortMode.TIME_DESC) }
    var selectMode by remember { mutableStateOf(false) }
    val selected = remember { mutableStateListOf<Long>() }

    fun refresh() { list = TodoRepo.all(ctx) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("代办列表", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = filterText,
            onValueChange = { filterText = it },
            label = { Text("搜索（标题/内容包含）") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("排序：")
            SortButton(text = "时间新→旧", active = sort == SortMode.TIME_DESC) { sort = SortMode.TIME_DESC }
            SortButton(text = "时间旧→新", active = sort == SortMode.TIME_ASC) { sort = SortMode.TIME_ASC }
            SortButton(text = "重要优先", active = sort == SortMode.IMPORTANT_FIRST) { sort = SortMode.IMPORTANT_FIRST }
            SortButton(text = "未处理优先", active = sort == SortMode.UNPROCESSED_FIRST) { sort = SortMode.UNPROCESSED_FIRST }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AssistChip(onClick = { selectMode = !selectMode; selected.clear() }, label = { Text(if (selectMode) "退出批量" else "进入批量") })
            if (selectMode) {
                val allShown = filteredSorted(list, filterText, sort).map { it.id }
                AssistChip(onClick = {
                    if (selected.size == allShown.size && selected.isNotEmpty()) selected.clear()
                    else { selected.clear(); selected.addAll(allShown) }
                }, label = { Text(if (selected.size == allShown.size && selected.isNotEmpty()) "全不选" else "全选") })
                AssistChip(onClick = {
                    TodoRepo.bulkDelete(ctx, selected.toList())
                    selected.clear(); refresh(); TodoWidgetProvider.refresh(ctx)
                }, label = { Text("删除选中") })
                AssistChip(onClick = {
                    TodoRepo.bulkMarkImportant(ctx, selected.toList(), true)
                    refresh(); TodoWidgetProvider.refresh(ctx)
                }, label = { Text("设为重要") })
                AssistChip(onClick = {
                    TodoRepo.bulkMarkImportant(ctx, selected.toList(), false)
                    refresh(); TodoWidgetProvider.refresh(ctx)
                }, label = { Text("取消重要") })
                AssistChip(onClick = {
                    TodoRepo.bulkMarkProcessed(ctx, selected.toList(), true)
                    refresh(); TodoWidgetProvider.refresh(ctx)
                }, label = { Text("标记已处理") })
                AssistChip(onClick = {
                    TodoRepo.bulkMarkProcessed(ctx, selected.toList(), false)
                    refresh(); TodoWidgetProvider.refresh(ctx)
                }, label = { Text("标记未处理") })
            }
            OutlinedButton(onClick = { refresh(); TodoWidgetProvider.refresh(ctx) }) { Text("刷新") }
        }

        val shown = filteredSorted(list, filterText, sort)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shown, key = { it.id }) { item ->
                TodoCard(
                    item = item,
                    selected = selected.contains(item.id),
                    selectMode = selectMode,
                    onToggleSelect = {
                        if (selected.contains(item.id)) selected.remove(item.id) else selected.add(item.id)
                    },
                    onToggleImportant = {
                        TodoRepo.toggleImportantById(ctx, item.id); refresh(); TodoWidgetProvider.refresh(ctx)
                    },
                    onToggleProcessed = {
                        TodoRepo.toggleProcessedById(ctx, item.id); refresh(); TodoWidgetProvider.refresh(ctx)
                    },
                    onDelete = {
                        TodoRepo.deleteById(ctx, item.id); refresh(); TodoWidgetProvider.refresh(ctx)
                    },
                    onEdit = { onEdit(item.id) }
                )
            }
        }
    }
}

private fun filteredSorted(src: List<TodoItem>, q: String, sort: SortMode): List<TodoItem> {
    val f = src.filter { it.title.contains(q, true) || it.content.contains(q, true) }
    return when (sort) {
        SortMode.TIME_DESC -> f.sortedByDescending { it.ts }
        SortMode.TIME_ASC -> f.sortedBy { it.ts }
        SortMode.IMPORTANT_FIRST -> f.sortedWith(compareByDescending<TodoItem> { it.important }.thenByDescending { it.ts })
        SortMode.UNPROCESSED_FIRST -> f.sortedWith(compareByDescending<TodoItem> { !it.processed }.thenByDescending { it.ts })
    }
}

@Composable
private fun SortButton(text: String, active: Boolean, onClick: () -> Unit) {
    if (active) {
        Button(onClick = onClick) { Text(text) }
    } else {
        OutlinedButton(onClick = onClick) { Text(text) }
    }
}

@Composable
private fun TodoCard(
    item: TodoItem,
    selected: Boolean,
    selectMode: Boolean,
    onToggleSelect: () -> Unit,
    onToggleImportant: () -> Unit,
    onToggleProcessed: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().then(if (selectMode) Modifier.clickable { onToggleSelect() } else Modifier)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectMode) {
                    val label = if (selected) "已选" else "未选"
                    AssistChip(onClick = onToggleSelect, label = { Text(label) })
                }
                Text(item.title.ifBlank { "（无标题）" }, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                IconButton(onClick = onToggleImportant) {
                    Icon(imageVector = if (item.important) Icons.Filled.Star else Icons.Outlined.StarOutline, contentDescription = null)
                }
                IconButton(onClick = onDelete) { Icon(imageVector = Icons.Filled.Delete, contentDescription = null) }
            }
            Text(item.content, maxLines = 5, overflow = TextOverflow.Ellipsis, modifier = Modifier.clickable { onEdit() })
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val processedText = if (item.processed) "标记未处理" else "标记已处理"
                OutlinedButton(onClick = onToggleProcessed) { Text(processedText) }
                OutlinedButton(onClick = onEdit) { Text("编辑") }
            }
        }
    }
}
