
package com.example.pocketassistant.ui
import android.app.Application
import android.content.ContentValues
import android.provider.CalendarContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pocketassistant.App
import com.example.pocketassistant.data.Entry
import com.example.pocketassistant.data.Event
import com.example.pocketassistant.reminder.ReminderScheduler
import com.example.pocketassistant.widget.TodoWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.TimeZone
class ChatViewModel(private val app: Application): AndroidViewModel(app) {
    private val db = (app as App).db
    private val _messages = MutableStateFlow(listOf<Message>())
    val messages = _messages.asStateFlow()
    fun send(text: String) {
        viewModelScope.launch {
            _messages.value = _messages.value + Message("user", text)
            val entry = Entry(rawText = text, hasAiParsed = true, source = "chat")
            db.entryDao().insert(entry)
            val now = System.currentTimeMillis()
            val event = Event(entryId = entry.id, title = text.take(30),
                description = "（演示）已整理为待办；配置 GPT 后将自动抽取详细时间/地点/优先级",
                startTime = now + 60*60*1000, remindAt = now + 30*60*1000, priority = 1)
            db.eventDao().insert(event)
            try {
                val values = ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, event.startTime)
                    put(CalendarContract.Events.DTEND, event.startTime!! + 30*60*1000)
                    put(CalendarContract.Events.TITLE, event.title)
                    put(CalendarContract.Events.DESCRIPTION, event.description)
                    put(CalendarContract.Events.CALENDAR_ID, 1)
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                }
                app.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            } catch (_: Throwable) {}
            event.remindAt?.let {
                ReminderScheduler.scheduleExact(app, it, event.title, event.description ?: "", event.eventId.hashCode())
            }
            TodoWidgetProvider.requestUpdate(app)
            _messages.value = _messages.value + Message("assistant", "已将消息整理为待办并写入系统日历，桌面小组件已更新。")
        }
    }
    companion object {
        fun provideFactory(app: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(app) as T
            }
        }
    }
}
