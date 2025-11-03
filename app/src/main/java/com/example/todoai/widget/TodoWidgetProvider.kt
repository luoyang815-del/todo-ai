// SPDX-License-Identifier: MIT
package com.example.todoai.widget
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.todoai.R
class TodoWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetIds: IntArray, appWidgetManager: AppWidgetManager) {
        val p = context.getSharedPreferences("todoai_prefs", Context.MODE_PRIVATE)
        val total = p.getInt("todo_total", 0)
        val important = p.getInt("todo_important", 0)
        val topN = 10
        appWidgetIds.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_todo)
            views.setTextViewText(R.id.tv_header, context.getString(R.string.widget_header))
            views.setTextViewText(R.id.tv_total, context.getString(R.string.widget_total_label) + "：$total")
            views.setTextViewText(R.id.tv_important, context.getString(R.string.widget_important_label) + "：$important")
            views.setTextViewText(R.id.tv_top, context.getString(R.string.widget_top_label) + "：$topN")
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
