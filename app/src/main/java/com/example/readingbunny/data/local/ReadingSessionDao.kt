package com.example.readingbunny.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.readingbunny.model.ReadingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {

    @Insert
    suspend fun insertSession(
        session: ReadingSession
    )

    @Query(
        "SELECT * FROM reading_sessions ORDER BY startedAt DESC"
    )
    fun getAllSessions(): Flow<List<ReadingSession>>

    @Query(
        "SELECT * FROM reading_sessions WHERE bookId = :bookId ORDER BY startedAt DESC"
    )
    fun getSessionsForBook(
        bookId: Int
    ): Flow<List<ReadingSession>>
}