
package com.example.pocketassistant.model

/**
 * 与日志中使用方式兼容：Message("user", text) / Message("assistant", text)
 * 同时包含 hasAiParsed / source / description 字段，避免参数名缺失错误。
 */
data class Message(
    val role: String,                // "user" or "assistant"
    val content: String,             // text content
    val hasAiParsed: Boolean = false,
    val source: String? = null,
    val description: String? = null
)
