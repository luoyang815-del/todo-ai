
package com.example.pocketassistant.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert suspend fun insert(entry: Entry)
    @Query("SELECT * FROM entries ORDER BY createdAt DESC LIMIT :limit")
    fun latest(limit: Int = 50): Flow<List<Entry>>
}

@Dao
interface EventDao {
    @Insert suspend fun insert(event: Event)

    @Query("""
        SELECT * FROM events
        WHERE status='open'
        ORDER BY
          CASE WHEN priority>=2 THEN 0 WHEN priority=1 THEN 1 ELSE 2 END,
          COALESCE(remindAt, startTime, createdAt) ASC
        LIMIT 10
    """)
    fun upcoming(): Flow<List<Event>>

    @Query("SELECT COUNT(*) FROM events WHERE status='open'")
    fun totalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM events WHERE status='open' AND priority>=1")
    fun importantCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM events WHERE status='open' AND priority>=2")
    fun urgentCount(): Flow<Int>

    @Query("UPDATE events SET status='done' WHERE eventId=:id")
    suspend fun markDone(id: String)
}
