
package com.example.pocketassistant.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.example.pocketassistant.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAll(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        private fun colorForPriority(p: Int): Int = when {
            p >= 2 -> 0xFFFF5252.toInt() // 红
            p == 1 -> 0xFFFFC107.toInt() // 黄
            else -> 0xFF8BC34A.toInt()   // 绿
        }

        fun requestUpdate(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, TodoWidgetProvider::class.java))
            if (ids.isNotEmpty()) updateAll(context, mgr, ids)
        }

        private fun updateAll(context: Context, mgr: AppWidgetManager, ids: IntArray) {
            val prefs = context.getSharedPreferences("widget", Context.MODE_PRIVATE)
            val line1 = prefs.getString("line1", "· 暂无待办") ?: "· 暂无待办"
            val line2 = prefs.getString("line2", "") ?: ""
            val line3 = prefs.getString("line3", "") ?: ""
            val p1 = prefs.getInt("p1", 0)
            val p2 = prefs.getInt("p2", 0)
            val p3 = prefs.getInt("p3", 0)

            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            ids.forEach { id ->
                val rv = RemoteViews(context.packageName, R.layout.widget_todo).apply {
                    setTextViewText(R.id.date, dateStr)
                    setTextViewText(R.id.line1, line1)
                    setTextColor(R.id.line1, colorForPriority(p1))
                    setTextViewText(R.id.line2, line2)
                    setTextColor(R.id.line2, colorForPriority(p2))
                    setTextViewText(R.id.line3, line3)
                    setTextColor(R.id.line3, colorForPriority(p3))
                }
                mgr.updateAppWidget(id, rv)
            }
        }
    }
}
