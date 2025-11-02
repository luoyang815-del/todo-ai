
package com.example.pocketassistant.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object ReminderScheduler {
    fun scheduleExact(context: Context, triggerAtMillis: Long, title: String, text: String, id: Int) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("text", text)
            putExtra("id", id)
        }
        val pi = PendingIntent.getBroadcast(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
    }
}
