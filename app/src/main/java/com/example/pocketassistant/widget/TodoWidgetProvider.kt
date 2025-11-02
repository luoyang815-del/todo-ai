
package com.example.pocketassistant.widget
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.example.pocketassistant.R
import com.example.pocketassistant.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodoWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAll(context, appWidgetManager, appWidgetIds)
    }
    companion object {
        private fun colorForPriority(p: Int): Int = when {
            p >= 2 -> 0xFFFF5252.toInt()
            p == 1 -> 0xFFFFC107.toInt()
            else -> 0xFF8BC34A.toInt()
        }
        fun requestUpdate(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, TodoWidgetProvider::class.java))
            if (ids.isNotEmpty()) updateAll(context, mgr, ids)
        }
        private fun updateAll(context: Context, mgr: AppWidgetManager, ids: IntArray) {
            val db = AppDatabase.get(context)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            CoroutineScope(Dispatchers.IO).launch {
                val c = db.openHelper.readableDatabase.query("""
                    SELECT title, priority FROM events
                    WHERE status='open'
                    ORDER BY CASE WHEN priority>=2 THEN 0 WHEN priority=1 THEN 1 ELSE 2 END,
                             COALESCE(remindAt,startTime,createdAt) ASC
                    LIMIT 10
                """)
                val lines = mutableListOf<Pair<String,Int>>()
                while (c.moveToNext()) lines += c.getString(0) to c.getInt(1)
                c.close()
                fun scalar(sql: String): Int {
                    val cc = db.openHelper.readableDatabase.query(sql); cc.moveToFirst()
                    val v = cc.getInt(0); cc.close(); return v
                }
                val total = scalar("SELECT COUNT(*) FROM events WHERE status='open'")
                val important = scalar("SELECT COUNT(*) FROM events WHERE status='open' AND priority>=1")
                val urgent = scalar("SELECT COUNT(*) FROM events WHERE status='open' AND priority>=2")
                ids.forEach { id ->
                    val rv = RemoteViews(context.packageName, R.layout.widget_todo).apply {
                        setTextViewText(R.id.date, sdf.format(Date()))
                        setTextViewText(R.id.summary, "全部:%d  重要:%d  紧急:%d".format(total, important, urgent))
                        val idsArr = intArrayOf(
                            R.id.line1,R.id.line2,R.id.line3,R.id.line4,R.id.line5,
                            R.id.line6,R.id.line7,R.id.line8,R.id.line9,R.id.line10
                        )
                        for (i in idsArr.indices) {
                            if (i < lines.size) {
                                val (t,p) = lines[i]
                                setTextViewText(idsArr[i], "· " + t)
                                setTextColor(idsArr[i], colorForPriority(p))
                            } else setTextViewText(idsArr[i], "")
                        }
                    }
                    mgr.updateAppWidget(id, rv)
                }
            }
        }
    }
}
