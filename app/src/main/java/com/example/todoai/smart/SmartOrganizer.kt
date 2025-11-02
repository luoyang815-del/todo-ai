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
        // 这里只做最小落地，避免网络/数据库导致的闪退
        // 你可以替换为真实保存逻辑（写入数据库/上传等），但须保留 try/catch。
        ensureChannel(context)
        val summary = buildString {
            appendLine("已保存到智能整理：")
            appendLine("Endpoint: ${settings.endpoint}")
            appendLine("Model: ${settings.model}")
            appendLine("Proxy: ${settings.proxyType} ${settings.proxyHost}:${settings.proxyPort}")
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("智能整理")
            .setContentText("配置已保存（点开查看详情）")
            .setStyle(NotificationCompat.BigTextStyle().bigText(summary))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notification)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Smart Organizer"
            val descriptionText = "保存到智能整理的通知"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
