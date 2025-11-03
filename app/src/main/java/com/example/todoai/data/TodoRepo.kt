// SPDX-License-Identifier: MIT
package com.example.todoai.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class TodoItem(
    val id: Long,
    val title: String,
    val content: String,
    val important: Boolean,
    val processed: Boolean,
    val ts: Long
)

object TodoRepo {
    private const val PREF = "todo_repo"
    private const val KEY = "items"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun all(ctx: Context): List<TodoItem> {
        val raw = sp(ctx).getString(KEY, "[]") ?: "[]"
        val arr = JSONArray(raw)
        val out = mutableListOf<TodoItem>()
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val ts = o.optLong("ts", System.currentTimeMillis())
            out += TodoItem(
                id = o.optLong("id", ts),
                title = o.optString("title"),
                content = o.optString("content"),
                important = o.optBoolean("important", false),
                processed = o.optBoolean("processed", false),
                ts = ts
            )
        }
        return out.sortedByDescending { it.ts }
    }

    private fun save(ctx: Context, list: List<TodoItem>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject()
                .put("id", it.id)
                .put("title", it.title)
                .put("content", it.content)
                .put("important", it.important)
                .put("processed", it.processed)
                .put("ts", it.ts)
            )
        }
        sp(ctx).edit().putString(KEY, arr.toString()).apply()
    }

    fun addLocal(ctx: Context, text: String) {
        val now = System.currentTimeMillis()
        val cur = all(ctx).toMutableList()
        cur.add(0, TodoItem(id = now, title = text.take(50), content = text, important = false, processed = false, ts = now))
        save(ctx, cur)
    }

    fun addBatch(ctx: Context, todos: List<String>) {
        val now = System.currentTimeMillis()
        val cur = all(ctx).toMutableList()
        todos.forEach {
            val t = it.trim()
            if (t.isNotEmpty()) {
                cur.add(0, TodoItem(id = System.nanoTime(), title = t.take(50), content = t, important = false, processed = true, ts = now))
            }
        }
        save(ctx, cur)
    }

    fun counts(ctx: Context): Pair<Int, Int> {
        val list = all(ctx)
        val total = list.size
        val important = list.count { it.important }
        return total to important
    }

    fun unprocessed(ctx: Context): List<TodoItem> = all(ctx).filter { !it.processed }

    fun getById(ctx: Context, id: Long): TodoItem? = all(ctx).firstOrNull { it.id == id }

    fun updateById(ctx: Context, id: Long, title: String, content: String, important: Boolean, processed: Boolean) {
        val list = all(ctx).toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val old = list[idx]
            list[idx] = old.copy(title = title, content = content, important = important, processed = processed)
            save(ctx, list)
        }
    }

    fun toggleImportantById(ctx: Context, id: Long) {
        val list = all(ctx).toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val it = list[idx]
            list[idx] = it.copy(important = !it.important)
            save(ctx, list)
        }
    }

    fun toggleProcessedById(ctx: Context, id: Long) {
        val list = all(ctx).toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val it = list[idx]
            list[idx] = it.copy(processed = !it.processed)
            save(ctx, list)
        }
    }

    fun deleteById(ctx: Context, id: Long) {
        val list = all(ctx).toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            list.removeAt(idx)
            save(ctx, list)
        }
    }

    fun bulkDelete(ctx: Context, ids: Collection<Long>) {
        val list = all(ctx).filterNot { ids.contains(it.id) }
        save(ctx, list)
    }

    fun bulkMarkImportant(ctx: Context, ids: Collection<Long>, value: Boolean) {
        val list = all(ctx).map {
            if (ids.contains(it.id)) it.copy(important = value) else it
        }
        save(ctx, list)
    }

    fun bulkMarkProcessed(ctx: Context, ids: Collection<Long>, value: Boolean) {
        val list = all(ctx).map {
            if (ids.contains(it.id)) it.copy(processed = value) else it
        }
        save(ctx, list)
    }
}
