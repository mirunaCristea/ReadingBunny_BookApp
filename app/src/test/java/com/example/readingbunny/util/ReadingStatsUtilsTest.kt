package com.example.readingbunny.util

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDate

class ReadingStatsUtilsTest {

    @Test
    fun currentStreak_returnsZero_whenThereAreNoReadingSessions() {
        val today = LocalDate.of(2026, 8, 23)

        val result = calculateCurrentStreak(
            sessionDates = emptySet(),
            today = today
        )

        assertEquals(0, result)
    }

    @Test
    fun currentStreak_returnsOne_whenUserReadTodayOnly() {
        val today = LocalDate.of(2026, 8, 23)

        val result = calculateCurrentStreak(
            sessionDates = setOf(today),
            today = today
        )

        assertEquals(1, result)
    }

    @Test
    fun currentStreak_countsConsecutiveDaysEndingToday() {
        val today = LocalDate.of(2026, 8, 23)

        val result = calculateCurrentStreak(
            sessionDates = setOf(
                today,
                today.minusDays(1),
                today.minusDays(2)
            ),
            today = today
        )

        assertEquals(3, result)
    }

    @Test
    fun currentStreak_startsFromYesterday_whenUserDidNotReadToday() {
        val today = LocalDate.of(2026, 8, 23)

        val result = calculateCurrentStreak(
            sessionDates = setOf(
                today.minusDays(1),
                today.minusDays(2)
            ),
            today = today
        )

        assertEquals(2, result)
    }

    @Test
    fun currentStreak_stopsAtFirstMissingDay() {
        val today = LocalDate.of(2026, 8, 23)

        val result = calculateCurrentStreak(
            sessionDates = setOf(
                today,
                today.minusDays(1),
                today.minusDays(3)
            ),
            today = today
        )

        assertEquals(2, result)
    }
}