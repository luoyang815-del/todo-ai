// SPDX-License-Identifier: MIT
package com.example.todoai.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoai.net.OpenAIClient
import com.example.todoai.notify.Notifier
import com.example.todoai.settings.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatItem(val prompt: String, val reply: String)

class HomeViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<ChatItem>>(emptyList())
    val items: StateFlow<List<ChatItem>> = _items

    private val client = OpenAIClient()

    fun send(context: Context, settings: AppSettings, prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            try {
                val reply = client.chatOnce(settings, prompt)
                Notifier.showReply(context, "AI 回复", reply)
                _items.value = (listOf(ChatItem(prompt, reply)) + _items.value).take(10)
            } catch (e: Throwable) {
                Notifier.showReply(context, "调用失败", e.message ?: "未知错误")
            }
        }
    }
}
