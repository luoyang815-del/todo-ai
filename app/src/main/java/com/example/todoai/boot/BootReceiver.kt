package com.example.todoai.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todoai.core.Notifications

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // 可在此恢复自动同步计划（WorkManager）
        Notifications.show(context, "todo-ai", "设备重启，服务已就绪")
    }
}
