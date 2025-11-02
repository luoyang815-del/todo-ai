
package com.example.pocketassistant.util
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
object CalendarUtil {
  fun tryInsert(ctx: Context, title: String, desc: String?, whenMillis: Long?) {
    if (whenMillis == null) return
    val ok = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
    if (!ok) return
    val values = ContentValues().apply {
      put(CalendarContract.Events.DTSTART, whenMillis)
      put(CalendarContract.Events.DTEND, whenMillis + 30*60*1000)
      put(CalendarContract.Events.TITLE, title)
      put(CalendarContract.Events.DESCRIPTION, desc ?: "")
      put(CalendarContract.Events.CALENDAR_ID, 1)
      put(CalendarContract.Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().id)
    }
    try { ctx.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values) } catch (_: Exception) {}
  }
}
