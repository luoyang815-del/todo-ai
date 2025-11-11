
package com.aihelper.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.aihelper.app.*
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = try {
        val ctx = applicationContext
        val proxy = proxyFlow(ctx).first()
        val tk = encryptedPrefs(ctx).getString("token","") ?: ""
        val server = serverFlow(ctx).first()
        val since = lastSyncFlow(ctx).first()
        Repo(ctx).syncNow(tk, server, proxy, since)
        Result.success()
    } catch (e: Exception) { Result.retry() }

    companion object {
        private const val UNIQUE = "auto_sync_v21"
        fun start(ctx: Context, minutes: Long = 15){
            val req = PeriodicWorkRequestBuilder<SyncWorker>(minutes, TimeUnit.MINUTES).build()
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(UNIQUE, ExistingPeriodicWorkPolicy.UPDATE, req)
        }
        fun stop(ctx: Context){
            WorkManager.getInstance(ctx).cancelUniqueWork(UNIQUE)
        }
    }
}
