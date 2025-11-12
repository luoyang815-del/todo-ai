
package com.aihelper.app
import android.content.Context
import android.os.Environment
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import kotlinx.coroutines.Dispatchers
import androidx.room.Room
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
class Repo(private val ctx: Context) {
  private val db by lazy { Room.databaseBuilder(ctx, AppDb::class.java, "app.db").build() }
  private val todoDao get() = db.todoDao()
  private val msgDao get() = db.messageDao()
  private val clientId by lazy { UUID.randomUUID().toString() }
  suspend fun addTodo(title:String, content:String){
    val now = System.currentTimeMillis()/1000
    val t = Todo(id=UUID.randomUUID().toString(), title=title, content=content, updated_at=now)
    withContext(Dispatchers.IO){ todoDao.upsert(t) }
    refreshWidget()
  }
  suspend fun addMessage(role:String, content:String){
    val now = System.currentTimeMillis()/1000
    val m = Message(id=UUID.randomUUID().toString(), role=role, content=content, updated_at=now)
    withContext(Dispatchers.IO){ msgDao.upsert(m) }
  }
  suspend fun syncNow(token:String, server:String, proxy:List<Any>, sinceTodo:Long, sinceMsg:Long,
                      pinHost:String="", pinSha256:String="", trustCA:Boolean=true): String = withContext(Dispatchers.IO){
    val http = okHttpSecure(ctx, (proxy.getOrNull(0) as? Int) ?: 0, (proxy.getOrNull(1) as? String) ?: "", (proxy.getOrNull(2) as? Int) ?: 0, (proxy.getOrNull(3) as? String) ?: "", (proxy.getOrNull(4) as? String) ?: "", pinHost, pinSha256, trustCA)
    val api = retrofitFor(server, http).create(SyncApi::class.java)
    val todos = todoDao.observe().first(); val msgs  = msgDao.observe().first()
    api.push("Bearer $token", SyncBatch(clientId, sinceTodo, sinceMsg, todos, msgs))
    val pull = api.pull("Bearer $token", sinceTodo, sinceMsg)
    todoDao.upsertAll(pull.todos); msgDao.upsertAll(pull.messages); refreshWidget()
    saveLastSyncTodo(ctx, pull.ts_todo); saveLastSyncMsg(ctx, pull.ts_msg)
    "ok todo_ts=${pull.ts_todo} msg_ts=${pull.ts_msg}"
  }
  suspend fun exportJson(): File = withContext(Dispatchers.IO){
    val todos = todoDao.observe().first(); val msgs  = msgDao.observe().first()
    val data = mapOf("todos" to todos, "messages" to msgs)
    val json = Json { prettyPrint = true }.encodeToString(data)
    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS); dir.mkdirs()
    val f = File(dir, "AIHelperExport-${System.currentTimeMillis()}.json"); f.writeText(json, Charsets.UTF_8); f
  }
  private suspend fun refreshWidget(){
    val mgr = AppWidgetManager.getInstance(ctx)
    val cn = ComponentName(ctx, com.aihelper.app.widget.TodosWidgetProvider::class.java)
    val ids = mgr.getAppWidgetIds(cn)
    if (ids.isNotEmpty()) com.aihelper.app.widget.TodosWidgetProvider().onUpdate(ctx, mgr, ids)
  }
  suspend fun top3(): List<Todo> = withContext(Dispatchers.IO) { db.todoDao().top3() }
}
