
package com.aihelper.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.aihelper.app.R
import com.aihelper.app.core.Storage
import kotlinx.coroutines.runBlocking

class TodoWidget: AppWidgetProvider() {
  override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    for (id in appWidgetIds) {
      val count = runBlocking { Storage.loadTodos(context).size }
      val views = RemoteViews(context.packageName, R.layout.todo_widget)
      views.setTextViewText(R.id.txtCount, "待办：" + count)
      appWidgetManager.updateAppWidget(id, views)
    }
  }
}
