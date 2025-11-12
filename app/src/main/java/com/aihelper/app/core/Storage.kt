
package com.aihelper.app.core

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object Storage {
  private fun file(ctx: Context) = File(ctx.filesDir, "todos.json")

  suspend fun loadTodos(ctx: Context) = withContext(Dispatchers.IO) {
    val f = file(ctx)
    if (!f.exists()) return@withContext emptyList<Todo>()
    val arr = JSONArray(f.readText())
    buildList {
      for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        add(Todo(o.getString("id"), o.getString("title"), o.getString("content"), o.getLong("updatedAt")))
      }
    }
  }

  suspend fun saveTodos(ctx: Context, todos: List<Todo>) = withContext(Dispatchers.IO) {
    val arr = JSONArray()
    todos.forEach {
      arr.put(JSONObject()
        .put("id", it.id)
        .put("title", it.title)
        .put("content", it.content)
        .put("updatedAt", it.updatedAt))
    }
    file(ctx).writeText(arr.toString())
  }

  suspend fun exportJson(ctx: Context): File = withContext(Dispatchers.IO) {
    val out = File(ctx.getExternalFilesDir(null), "AIHelperExport-todos.json")
    val src = file(ctx)
    if (src.exists()) out.writeText(src.readText()) else out.writeText("[]")
    out
  }
}
