package com.example.todoai.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.todoai.R

object Notifications {
    private const val CHANNEL_ID = "todoai_results"
    fun init(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(CHANNEL_ID, "AI 助手结果通知", NotificationManager.IMPORTANCE_DEFAULT)
            mgr.createNotificationChannel(channel)
        }
    }
    fun show(ctx: Context, title: String, content: String, id: Int = (System.currentTimeMillis()%1_000_000).toInt()) {
        val n = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
        (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(id, n)
    }
}
