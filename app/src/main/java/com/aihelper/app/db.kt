
package com.aihelper.app

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE deleted=0 ORDER BY updated_at DESC")
    fun observe(): Flow<List<Todo>>
    @Query("SELECT * FROM todos WHERE deleted=0 ORDER BY updated_at DESC LIMIT 3")
    suspend fun top3(): List<Todo>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(todo: Todo)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<Todo>)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE deleted=0 ORDER BY updated_at DESC")
    fun observe(): Flow<List<Message>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(m: Message)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<Message>)
}

@Database(entities = [Todo::class, Message::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun messageDao(): MessageDao
}
