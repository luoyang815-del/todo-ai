package com.aihelper.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.aihelper.app.R
import com.aihelper.app.Repo
import kotlinx.coroutines.runBlocking

class TodosWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // 取 top3 代办（阻塞一下就行，耗时很短）
        val lines: List<String> = runBlocking {
            try {
                Repo(context).top3().map { "• " + (it.title.ifBlank { it.content }.take(24)) }
            } catch (_: Exception) {
                emptyList()
            }
        }
        val views = buildViews(context, lines)
        appWidgetIds.forEach { id -> appWidgetManager.updateAppWidget(id, views) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        // 自定义刷新广播（可选）：当你新增/同步完成后发一个广播来刷新小组件
        if (intent.action == ACTION_REFRESH) {
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, TodosWidgetProvider::class.java)
            onUpdate(context, mgr, mgr.getAppWidgetIds(cn))
        }
    }

    private fun buildViews(ctx: Context, lines: List<String>): RemoteViews {
        val rv = RemoteViews(ctx.packageName, R.layout.widget_todos)
        // 统一先隐藏
        rv.setViewVisibility(R.id.line1, android.view.View.GONE)
        rv.setViewVisibility(R.id.line2, android.view.View.GONE)
        rv.setViewVisibility(R.id.line3, android.view.View.GONE)
        rv.setViewVisibility(R.id.empty, android.view.View.GONE)

        if (lines.isEmpty()) {
            rv.setViewVisibility(R.id.empty, android.view.View.VISIBLE)
        } else {
            if (lines.size >= 1) {
                rv.setTextViewText(R.id.line1, lines[0])
                rv.setViewVisibility(R.id.line1, android.view.View.VISIBLE)
            }
            if (lines.size >= 2) {
                rv.setTextViewText(R.id.line2, lines[1])
                rv.setViewVisibility(R.id.line2, android.view.View.VISIBLE)
            }
            if (lines.size >= 3) {
                rv.setTextViewText(R.id.line3, lines[2])
                rv.setViewVisibility(R.id.line3, android.view.View.VISIBLE)
            }
        }
        return rv
    }

    companion object {
        const val ACTION_REFRESH = "com.aihelper.app.widget.REFRESH"
    }
}
