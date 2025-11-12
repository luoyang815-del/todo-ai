
package com.aihelper.app.work
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.aihelper.app.*
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
  override suspend fun getForegroundInfo(): ForegroundInfo {
    Noti.ensureChannel(applicationContext)
    val n = NotificationCompat.Builder(applicationContext, Noti.CID_SYNC).setSmallIcon(android.R.drawable.ic_popup_sync).setContentTitle("AI 助手").setContentText("正在同步…").build()
    return ForegroundInfo(42, n)
  }
  override suspend fun doWork(): Result = try {
    val ctx = applicationContext
    val proxy = proxyFlow(ctx).first()
    val tk = encryptedPrefs(ctx).getString("token","") ?: ""
    val server = serverFlow(ctx).first()
    val lastTodo = lastSyncTodoFlow(ctx).first()
    val lastMsg  = lastSyncMsgFlow(ctx).first()
    Repo(ctx).syncNow(tk, server, proxy, lastTodo, lastMsg)
    Result.success()
  } catch (e: Exception) { Result.retry() }
  companion object {
    private const val UNIQUE = "auto_sync_rv"
    fun start(ctx: Context, minutes: Long = 15){ val req = PeriodicWorkRequestBuilder<SyncWorker>(minutes, TimeUnit.MINUTES).build(); WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(UNIQUE, ExistingPeriodicWorkPolicy.UPDATE, req) }
    fun stop(ctx: Context){ WorkManager.getInstance(ctx).cancelUniqueWork(UNIQUE) }
  }
}
