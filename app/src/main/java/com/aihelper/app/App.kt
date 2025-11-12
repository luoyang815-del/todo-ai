
package com.aihelper.app

import android.app.Application
import com.aihelper.app.core.Notifier

class App: Application() {
  override fun onCreate() { super.onCreate(); Notifier.init(this) }
}
