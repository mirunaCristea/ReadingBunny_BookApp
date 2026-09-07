package com.example.readingbunny.ui.viewmodel

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.readingbunny.data.local.ReadingBunnyDatabase
import com.example.readingbunny.data.repository.ReadingSessionRepository
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.BookOwnership
import com.example.readingbunny.model.ReadingStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ReadingSessionViewModelTest {

    private lateinit var database:
            ReadingBunnyDatabase

    private lateinit var repository:
            ReadingSessionRepository

    private lateinit var viewModel:
            ReadingSessionViewModel

    @Before
    fun setup() {

        val context =
            ApplicationProvider
                .getApplicationContext<
                        Context
                        >()

        database =
            Room
                .inMemoryDatabaseBuilder<
                        ReadingBunnyDatabase
                        >(context)
                .build()

        repository =
            ReadingSessionRepository(
                database
                    .readingSessionDao()
            )

        viewModel =
            ReadingSessionViewModel(
                repository
            )
    }

    @After
    fun tearDown() {

        database.close()
    }

    @Test
    fun session_canStartPauseAndFinish() {

        val book =
            Book(
                id = 10,
                title = "Dune",
                author = "Frank Herbert",
                status =
                    ReadingStatus.READING,
                ownership =
                    BookOwnership.OWNED,
                currentPage = 40,
                totalPages = 400
            )

        InstrumentationRegistry
            .getInstrumentation()
            .runOnMainSync {

                viewModel.startSession(
                    book
                )
            }

        assertTrue(
            viewModel.isRunning.value
        )

        Thread.sleep(1100)

        assertTrue(
            viewModel
                .elapsedSeconds
                .value >= 1
        )

        InstrumentationRegistry
            .getInstrumentation()
            .runOnMainSync {

                viewModel.pauseSession()
            }

        val pausedTime =
            viewModel
                .elapsedSeconds
                .value

        Thread.sleep(1100)

        assertEquals(
            pausedTime,
            viewModel
                .elapsedSeconds
                .value
        )

        val savedLatch =
            CountDownLatch(1)

        InstrumentationRegistry
            .getInstrumentation()
            .runOnMainSync {

                viewModel.finishSession(
                    endPage = 55,
                    onSaved = {
                        savedLatch.countDown()
                    }
                )
            }

        assertTrue(
            savedLatch.await(
                2,
                TimeUnit.SECONDS
            )
        )

        val sessions =
            runBlocking {

                repository
                    .getAllSessions()
                    .first()
            }

        assertEquals(
            1,
            sessions.size
        )

        val session =
            sessions.first()

        assertEquals(
            10,
            session.bookId
        )

        assertEquals(
            40,
            session.startPage
        )

        assertEquals(
            55,
            session.endPage
        )

        assertEquals(
            pausedTime,
            session.durationSeconds
        )

        assertFalse(
            viewModel.isRunning.value
        )

        assertEquals(
            0L,
            viewModel
                .elapsedSeconds
                .value
        )
    }
}