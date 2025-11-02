
package com.example.pocketassistant.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val rawText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val source: String = "quick_input",
    val hasAiParsed: Boolean = false
)

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val eventId: String = UUID.randomUUID().toString(),
    val entryId: String,
    val title: String,
    val description: String? = null,
    val startTime: Long? = null,
    val priority: Int = 0,          // 0普通 1重要 2紧急
    val remindAt: Long? = null,
    val status: String = "open",
    val createdAt: Long = System.currentTimeMillis()
)
