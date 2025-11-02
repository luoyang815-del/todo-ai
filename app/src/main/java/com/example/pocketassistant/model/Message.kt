
package com.example.pocketassistant.model
data class Message(
    val role: String,        // "user" / "assistant"
    val content: String,
    val hasAiParsed: Boolean = false,
    val source: String? = null,
    val description: String? = null
)
