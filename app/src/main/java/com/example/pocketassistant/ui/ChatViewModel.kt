
package com.example.pocketassistant.ui
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pocketassistant.App
import com.example.pocketassistant.data.Entry
import com.example.pocketassistant.data.Event
import com.example.pocketassistant.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val app: Application): AndroidViewModel(app) {
    private val db = (app as App).db
    private val _messages = MutableStateFlow(listOf<Message>())
    val messages = _messages.asStateFlow()

    fun send(text: String) {
        viewModelScope.launch {
            // 追加用户消息
            _messages.value = _messages.value + Message(role = "user", content = text)
            // 入库 Entry / 自动整理 Event（示例）
            val entry = Entry(rawText = text, hasAiParsed = true, source = "chat")
            db.entryDao().insert(entry)
            val event = Event(
                entryId = entry.id,
                title = text.take(30),
                description = "（演示）已整理为待办；配置 GPT 后将自动抽取详细信息",
                startTime = System.currentTimeMillis() + 60*60*1000,
                remindAt = System.currentTimeMillis() + 30*60*1000,
                priority = 1
            )
            db.eventDao().insert(event)
            // 追加助手消息
            _messages.value = _messages.value + Message(
                role = "assistant",
                content = "已把消息整理成待办并入库（演示）。",
                hasAiParsed = true,
                description = event.description
            )
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
