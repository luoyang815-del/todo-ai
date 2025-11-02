package com.example.pocketassistant.model

/**
 * 最小补丁：补齐对话消息数据结构，兼容你现有代码的用法：
 * - ChatViewModel/ChatActivity 里使用的 `Message("user", text)` / `Message("assistant", text)`
 * - 以及日志中提示缺少的 hasAiParsed / source / description 字段
 */
data class Message(
    val role: String,                // "user" / "assistant"
    val content: String,             // 文本内容
    val hasAiParsed: Boolean = false,
    val source: String? = null,
    val description: String? = null
)
