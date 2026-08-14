package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readingbunny.data.repository.ReadingSessionRepository
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.ReadingSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ReadingSessionViewModel(
    private val repository: ReadingSessionRepository
) : ViewModel() {

    private val _elapsedSeconds =
        MutableStateFlow(0L)

    val elapsedSeconds: StateFlow<Long> =
        _elapsedSeconds.asStateFlow()


    private val _isRunning =
        MutableStateFlow(false)

    val isRunning: StateFlow<Boolean> =
        _isRunning.asStateFlow()

    val sessions: Flow<List<ReadingSession>> =
        repository.getAllSessions()
    private var timerJob: Job? = null

    private var activeBookId: Int? = null
    private var startPage: Int? = null
    private var startedAt: Long? = null


    fun startSession(book: Book) {

        if (startedAt != null) {
            return
        }

        activeBookId = book.id
        startPage = book.currentPage
        startedAt = System.currentTimeMillis()

        _elapsedSeconds.value = 0L

        resumeSession()
    }


    fun pauseSession() {

        timerJob?.cancel()
        timerJob = null

        _isRunning.value = false
    }


    fun resumeSession() {

        if (
            _isRunning.value ||
            startedAt == null
        ) {
            return
        }

        _isRunning.value = true

        timerJob = viewModelScope.launch {

            while (isActive) {

                delay(1000)

                _elapsedSeconds.update {
                    it + 1
                }
            }
        }
    }


    fun finishSession(
        endPage: Int,
        onSaved: () -> Unit
    ) {

        val bookId =
            activeBookId ?: return

        val sessionStartPage =
            startPage ?: return

        val sessionStartedAt =
            startedAt ?: return

        pauseSession()

        val session = ReadingSession(
            bookId = bookId,
            startedAt = sessionStartedAt,
            endedAt = System.currentTimeMillis(),
            startPage = sessionStartPage,
            endPage = endPage,
            durationSeconds = _elapsedSeconds.value
        )

        viewModelScope.launch {

            repository.addSession(session)

            resetSession()

            onSaved()
        }
    }


    private fun resetSession() {

        timerJob?.cancel()
        timerJob = null

        activeBookId = null
        startPage = null
        startedAt = null

        _elapsedSeconds.value = 0L
        _isRunning.value = false
    }

    fun cancelSession() {
        resetSession()
    }
}