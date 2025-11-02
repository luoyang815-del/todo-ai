
package com.example.pocketassistant.notify
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pocketassistant.data.AppDatabase
class ReminderWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
  override suspend fun doWork(): Result {
    val id = inputData.getString("eventId") ?: return Result.success()
    val db = AppDatabase.get(applicationContext)
    val c = db.openHelper.readableDatabase.query("SELECT title, description FROM events WHERE eventId=?", arrayOf(id))
    if (c.moveToFirst()) {
      val title = c.getString(0)
      val desc = c.getString(1) ?: ""
      Notifier.notify(applicationContext, id.hashCode(), title, desc)
    }
    c.close()
    return Result.success()
  }
}
