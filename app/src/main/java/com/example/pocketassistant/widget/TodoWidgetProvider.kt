// SPDX-License-Identifier: MIT
package com.example.pocketassistant.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.pocketassistant.R

class TodoWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetIds: IntArray, appWidgetManager: AppWidgetManager) {
        val p = context.getSharedPreferences("pocket_prefs", Context.MODE_PRIVATE)
        val total = p.getInt("todo_total", 0)
        val important = p.getInt("todo_important", 0)
        val topN = 10 // 展示前10

        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_todo)
            views.setTextViewText(R.id.tv_total, "总数：$total")
            views.setTextViewText(R.id.tv_important, "重要：$important")
            views.setTextViewText(R.id.tv_top, "前十：$topN")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
