
package com.aihelper.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey val id: String,
    val title: String = "",
    val content: String = "",
    val priority: Int = 0,
    val due_ts: Long = 0,
    val deleted: Int = 0,
    val updated_at: Long = 0L,
    val source: String = "client"
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val role: String = "user",
    val content: String = "",
    val deleted: Int = 0,
    val updated_at: Long = 0L,
    val source: String = "client"
)
