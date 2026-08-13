package com.example.readingbunny.data.repository

import com.example.readingbunny.data.local.ReadingSessionDao
import com.example.readingbunny.model.ReadingSession
import kotlinx.coroutines.flow.Flow

class ReadingSessionRepository(
    private val readingSessionDao: ReadingSessionDao
) {

    suspend fun addSession(
        session: ReadingSession
    ) =
        readingSessionDao.insertSession(session)

    fun getAllSessions(): Flow<List<ReadingSession>> =
        readingSessionDao.getAllSessions()

    fun getSessionsForBook(
        bookId: Int
    ): Flow<List<ReadingSession>> =
        readingSessionDao.getSessionsForBook(bookId)
}