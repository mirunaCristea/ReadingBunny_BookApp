package com.example.readingbunny.util

import java.time.LocalDate

fun calculateCurrentStreak(
    sessionDates: Set<LocalDate>,
    today: LocalDate
): Int {
    if (sessionDates.isEmpty()) {
        return 0
    }

    var currentDate =
        if (today in sessionDates) {
            today
        } else {
            today.minusDays(1)
        }

    var streak = 0

    while (currentDate in sessionDates) {
        streak++

        currentDate =
            currentDate.minusDays(1)
    }

    return streak
}