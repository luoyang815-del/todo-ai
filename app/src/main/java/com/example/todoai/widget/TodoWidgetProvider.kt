// SPDX-License-Identifier: MIT
package com.example.todoai.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.todoai.R
import com.example.todoai.data.TodoRepo

class TodoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAll(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.example.todoai.ACTION_REFRESH_WIDGET") {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(android.content.ComponentName(context, TodoWidgetProvider::class.java))
            updateAll(context, mgr, ids)
        }
    }

    private fun updateAll(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val (total, important) = TodoRepo.counts(context)
        val topN = 10
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_todo)
            views.setTextViewText(R.id.tv_header, context.getString(R.string.widget_header))
            views.setTextViewText(R.id.tv_total, context.getString(R.string.widget_total_label) + "：" + total)
            views.setTextViewText(R.id.tv_important, context.getString(R.string.widget_important_label) + "：" + important)
            views.setTextViewText(R.id.tv_top, context.getString(R.string.widget_top_label) + "：" + topN)
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    companion object {
        fun refresh(context: Context) {
            val i = Intent("com.example.todoai.ACTION_REFRESH_WIDGET")
            i.setPackage(context.packageName)
            context.sendBroadcast(i)
        }
    }
}
