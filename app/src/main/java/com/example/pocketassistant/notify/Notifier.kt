
package com.example.pocketassistant.notify
import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pocketassistant.R
object Notifier {
  const val CHANNEL_ID = "todo_channel"
  fun ensureChannel(ctx: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
        val ch = NotificationChannel(CHANNEL_ID,
          ctx.getString(R.string.notify_channel_name),
          NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = ctx.getString(R.string.notify_channel_desc)
          }
        mgr.createNotificationChannel(ch)
      }
    }
  }
  fun notify(ctx: Context, id: Int, title: String, text: String) {
    val n = NotificationCompat.Builder(ctx, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_popup_reminder)
      .setContentTitle(title)
      .setContentText(text)
      .setAutoCancel(true)
      .build()
    (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(id, n)
  }
}
