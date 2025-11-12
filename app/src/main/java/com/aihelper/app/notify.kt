
package com.aihelper.app
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
object Noti {
  const val CID_SYNC = "aihelper.sync"
  fun ensureChannel(ctx: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManagerCompat.from(ctx).createNotificationChannel(NotificationChannel(CID_SYNC, "同步", NotificationManager.IMPORTANCE_DEFAULT))
    }
  }
  fun notify(ctx: Context, title:String, text:String, id:Int=1){
    ensureChannel(ctx)
    val n = NotificationCompat.Builder(ctx, CID_SYNC).setSmallIcon(android.R.drawable.ic_popup_sync).setContentTitle(title).setContentText(text).setAutoCancel(true).build()
    NotificationManagerCompat.from(ctx).notify(id, n)
  }
}
