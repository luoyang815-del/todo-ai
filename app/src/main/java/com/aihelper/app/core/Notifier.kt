
package com.aihelper.app.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object Notifier {
  private const val CHANNEL_ID = "aihelper_channel"
  fun init(ctx: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(CHANNEL_ID, "AIHelper", NotificationManager.IMPORTANCE_DEFAULT)
      (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }
  }
  fun notify(ctx: Context, title: String, content: String) {
    val n = NotificationCompat.Builder(ctx, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.stat_notify_more)
      .setContentTitle(title)
      .setContentText(content)
      .setAutoCancel(true)
      .build()
    NotificationManagerCompat.from(ctx).notify(System.currentTimeMillis().toInt(), n)
  }
}
