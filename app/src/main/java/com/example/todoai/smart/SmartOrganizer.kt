// SPDX-License-Identifier: MIT
package com.example.todoai.smart
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoai.settings.AppSettings
private const val CHANNEL_ID = "smart_org_channel"
object SmartOrganizer {
    fun save(context: Context, settings: AppSettings) {
        ensureChannel(context)
        val summary = "已保存到智能整理：\n" +
                "Endpoint: ${settings.endpoint}\n" +
                "Model: ${settings.model}\n" +
                "Proxy: ${settings.proxyType} ${settings.proxyHost}:${settings.proxyPort}"
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("智能整理")
            .setContentText("配置已保存（点开查看详情）")
            .setStyle(NotificationCompat.BigTextStyle().bigText(summary))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
        NotificationManagerCompat.from(context).notify(1001, notification)
    }
    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Smart Organizer", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "保存到智能整理的通知"
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }
}
