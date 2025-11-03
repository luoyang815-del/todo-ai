// SPDX-License-Identifier: MIT
package com.example.todoai.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class TodoItem(
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
            out += TodoItem(
                title = o.optString("title"),
                content = o.optString("content"),
                important = o.optBoolean("important", False),
                processed = o.optBoolean("processed", False),
                ts = o.optLong("ts", 0L)
            )
        }
        return out.sortedByDescending { it.ts }
    }

    private fun save(ctx: Context, list: List<TodoItem>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject()
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
        val cur = all(ctx).toMutableList()
        cur.add(0, TodoItem(title = text.take(50), content = text, important = false, processed = false, ts = System.currentTimeMillis()))
        save(ctx, cur)
    }

    fun addBatch(ctx: Context, todos: List<String>) {
        val cur = all(ctx).toMutableList()
        val now = System.currentTimeMillis()
        todos.forEach {
            cur.add(0, TodoItem(title = it.take(50), content = it, important = false, processed = true, ts = now))
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

    fun toggleImportant(ctx: Context, indexInSorted: Int) {
        val list = all(ctx).toMutableList()
        if (indexInSorted in list.indices) {
            val it = list[indexInSorted]
            list[indexInSorted] = it.copy(important = !it.important)
            save(ctx, list)
        }
    }

    fun toggleProcessed(ctx: Context, indexInSorted: Int) {
        val list = all(ctx).toMutableList()
        if (indexInSorted in list.indices) {
            val it = list[indexInSorted]
            list[indexInSorted] = it.copy(processed = !it.processed)
            save(ctx, list)
        }
    }

    fun delete(ctx: Context, indexInSorted: Int) {
        val list = all(ctx).toMutableList()
        if (indexInSorted in list.indices) {
            list.removeAt(indexInSorted)
            save(ctx, list)
        }
    }
}
