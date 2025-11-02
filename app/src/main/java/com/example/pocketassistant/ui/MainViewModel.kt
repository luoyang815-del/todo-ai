
package com.example.pocketassistant.ui
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pocketassistant.App
import com.example.pocketassistant.data.Entry
import com.example.pocketassistant.data.Event
import com.example.pocketassistant.reminder.ReminderScheduler
import com.example.pocketassistant.widget.TodoWidgetProvider
import kotlinx.coroutines.launch
class MainViewModel(private val app: Application): AndroidViewModel(app) {
    private val db = (app as App).db
    val entries = db.entryDao().latest()
    val events = db.eventDao().upcoming()
    fun addQuick(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch { db.entryDao().insert(Entry(rawText = text)) }
    }
    fun addQuickAndParse(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val entry = Entry(rawText = text, hasAiParsed = true)
            db.entryDao().insert(entry)
            val now = System.currentTimeMillis()
            val event = Event(entryId = entry.id, title = text.take(20),
                description = "（演示）智能整理结果：配置 GPT 后将自动解析时间/地点/优先级",
                startTime = now + 60*60*1000, remindAt = now + 10*60*1000, priority = 1)
            db.eventDao().insert(event)
            ReminderScheduler.scheduleExact(app, event.remindAt!!, event.title, event.description ?: "", event.eventId.hashCode())
            TodoWidgetProvider.requestUpdate(app)
        }
    }
    fun goSettings() { app.startActivity(Intent(app, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    fun goChat() { app.startActivity(Intent(app, ChatActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    companion object {
        val factory = { app: Application -> object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T { @Suppress("UNCHECKED_CAST") return MainViewModel(app) as T }
        } }
    }
}
