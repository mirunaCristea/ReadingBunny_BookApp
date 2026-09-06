package com.example.readingbunny.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.ReadingAchievement
import com.example.readingbunny.model.ReadingSession
import com.example.readingbunny.model.ReadingStatus
import com.example.readingbunny.ui.theme.ShelfWood
import com.example.readingbunny.util.calculateCurrentStreak
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


private data class DayReadingActivity(
    val date: LocalDate,
    val readingSeconds: Long
)


private enum class StatsPeriod {
    WEEK,
    MONTH,
    ALL_TIME
}


@Composable
fun StatsScreen(
    sessions: List<ReadingSession>,
    books: List<Book>
) {
    val today = LocalDate.now()

    var selectedPeriod by rememberSaveable {
        mutableStateOf(StatsPeriod.WEEK)
    }

    /*
     * ALL SESSION DATES
     *
     * These are used for the current streak.
     * The current streak should not depend on the selected
     * Week / Month / All Time filter.
     */
    val sessionDates =
        sessions
            .map { session ->
                Instant
                    .ofEpochMilli(session.startedAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            .toSet()

    val currentStreak =
        calculateCurrentStreak(
            sessionDates = sessionDates,
            today = today
        )

    /*
     * ACHIEVEMENTS ARE ALL-TIME
     *
     * Achievements should stay unlocked even when the user
     * changes the statistics period.
     */
    val achievements =
        buildAchievements(
            sessions = sessions,
            books = books,
            currentStreak = currentStreak
        )

    /*
     * WEEK RANGE
     */
    val startOfWeek =
        today.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.MONDAY
            )
        )

    val endOfWeek =
        startOfWeek.plusDays(6)

    /*
     * MONTH RANGE
     */
    val startOfMonth =
        today.withDayOfMonth(1)

    val endOfMonth =
        today.withDayOfMonth(
            today.lengthOfMonth()
        )

    /*
     * FILTER SESSIONS DEPENDING ON SELECTED PERIOD
     */
    val filteredSessions =
        sessions.filter { session ->

            val sessionDate =
                Instant
                    .ofEpochMilli(session.startedAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

            when (selectedPeriod) {

                StatsPeriod.WEEK ->
                    !sessionDate.isBefore(startOfWeek) &&
                            !sessionDate.isAfter(endOfWeek)

                StatsPeriod.MONTH ->
                    !sessionDate.isBefore(startOfMonth) &&
                            !sessionDate.isAfter(endOfMonth)

                StatsPeriod.ALL_TIME ->
                    true
            }
        }

    /*
     * WEEK ACTIVITY
     *
     * We keep this for the Week view.
     * Month will get the monthly calendar in the next step.
     */
    val weekDays =
        (0L..6L).map { dayOffset ->

            val date =
                startOfWeek.plusDays(dayOffset)

            val sessionsForDay =
                sessions.filter { session ->

                    val sessionDate =
                        Instant
                            .ofEpochMilli(session.startedAt)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                    sessionDate == date
                }

            val seconds =
                sessionsForDay.sumOf { session ->
                    session.durationSeconds
                }

            DayReadingActivity(
                date = date,
                readingSeconds = seconds
            )
        }

    /*
     * PERIOD STATISTICS
     */
    val totalSeconds =
        filteredSessions.sumOf { session ->
            session.durationSeconds
        }

    val pagesRead =
        filteredSessions.sumOf { session ->
            maxOf(
                0,
                session.endPage - session.startPage
            )
        }

    val readingDays =
        filteredSessions
            .map { session ->
                Instant
                    .ofEpochMilli(session.startedAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            .distinct()
            .size

    /*
     * RECENT ACTIVITY ALSO RESPECTS THE SELECTED PERIOD
     */
    val recentSessions =
        filteredSessions
            .sortedByDescending { session ->
                session.startedAt
            }
            .take(5)

    val periodSubtitle =
        when (selectedPeriod) {
            StatsPeriod.WEEK ->
                "This week"

            StatsPeriod.MONTH ->
                today.format(
                    DateTimeFormatter.ofPattern(
                        "MMMM yyyy"
                    )
                )

            StatsPeriod.ALL_TIME ->
                "All reading activity"
        }

    val recentActivityTitle =
        when (selectedPeriod) {
            StatsPeriod.WEEK ->
                "Recent activity this week"

            StatsPeriod.MONTH ->
                "Recent activity this month"

            StatsPeriod.ALL_TIME ->
                "Recent activity"
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(20.dp)
    ) {

        item {

            /*
             * HEADER
             */
            Text(
                text = "Reading journey",
                style =
                    MaterialTheme.typography.headlineMedium,
                color =
                    MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = periodSubtitle,
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            /*
             * PERIOD SELECTOR
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                StatsPeriodButton(
                    text = "Week",
                    selected =
                        selectedPeriod ==
                                StatsPeriod.WEEK,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedPeriod =
                            StatsPeriod.WEEK
                    }
                )

                StatsPeriodButton(
                    text = "Month",
                    selected =
                        selectedPeriod ==
                                StatsPeriod.MONTH,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedPeriod =
                            StatsPeriod.MONTH
                    }
                )

                StatsPeriodButton(
                    text = "All Time",
                    selected =
                        selectedPeriod ==
                                StatsPeriod.ALL_TIME,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedPeriod =
                            StatsPeriod.ALL_TIME
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            /*
             * CURRENT STREAK
             */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant,
                        shape =
                            RoundedCornerShape(18.dp)
                    )
                    .padding(18.dp)
            ) {

                Text(
                    text =
                        "🔥 $currentStreak day streak",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurface
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text =
                        if (currentStreak > 0) {
                            "Keep your reading streak going!"
                        } else {
                            "Read today to start a new streak."
                        },
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            /*
             * STATISTICS
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                StatCard(
                    modifier = Modifier.weight(1f),
                    value =
                        formatReadingDuration(
                            totalSeconds
                        ),
                    label = "Reading time"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    value = pagesRead.toString(),
                    label = "Pages"
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                StatCard(
                    modifier = Modifier.weight(1f),
                    value =
                        filteredSessions
                            .size
                            .toString(),
                    label = "Sessions"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    value =
                        readingDays.toString(),
                    label = "Reading days"
                )
            }

            /*
             * WEEK ACTIVITY
             *
             * Only shown for Week for now.
             */
            if (
                selectedPeriod ==
                StatsPeriod.WEEK
            ) {

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                Text(
                    text = "This week's activity",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onBackground
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                WeekActivityRow(
                    days = weekDays,
                    today = today
                )
            }

            /*
             * MILESTONES
             */
            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Text(
                text = "Milestones",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                color =
                    MaterialTheme
                        .colorScheme
                        .onBackground
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                achievements.forEach {
                        achievement ->

                    AchievementCard(
                        achievement = achievement
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            /*
             * RECENT ACTIVITY
             */
            Text(
                text = recentActivityTitle,
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                color =
                    MaterialTheme
                        .colorScheme
                        .onBackground
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        /*
         * RECENT SESSION LIST
         */
        if (recentSessions.isEmpty()) {

            item {

                Text(
                    text =
                        when (selectedPeriod) {

                            StatsPeriod.WEEK ->
                                "No reading sessions this week."

                            StatsPeriod.MONTH ->
                                "No reading sessions this month."

                            StatsPeriod.ALL_TIME ->
                                "No reading sessions yet."
                        },
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

        } else {

            items(recentSessions) { session ->

                val book =
                    books.firstOrNull { book ->
                        book.id == session.bookId
                    }

                ReadingSessionCard(
                    session = session,
                    bookTitle =
                        book?.title
                            ?: "Unknown book"
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
            }
        }
    }
}


@Composable
private fun StatsPeriodButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape =
            RoundedCornerShape(50.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    if (selected) {
                        ShelfWood
                    } else {
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                    },
                contentColor =
                    if (selected) {
                        MaterialTheme
                            .colorScheme
                            .onPrimary
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    }
            )
    ) {

        Text(
            text = text,
            fontSize = 12.sp
        )
    }
}


@Composable
private fun WeekActivityRow(
    days: List<DayReadingActivity>,
    today: LocalDate
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {

        days.forEach { day ->

            val hasRead =
                day.readingSeconds > 0

            val isToday =
                day.date == today

            val backgroundColor =
                if (hasRead) {
                    ShelfWood
                } else {
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant
                }

            val textColor =
                if (hasRead) {
                    MaterialTheme
                        .colorScheme
                        .onPrimary
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurface
                }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = backgroundColor,
                        shape =
                            RoundedCornerShape(
                                14.dp
                            )
                    )
                    .padding(
                        vertical = 10.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        day.date
                            .dayOfWeek
                            .name
                            .take(1),
                    fontSize = 12.sp,
                    color = textColor
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text =
                        if (hasRead) {
                            "📖"
                        } else {
                            "·"
                        },
                    fontSize = 17.sp,
                    color = textColor
                )

                if (isToday) {

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text = "Today",
                        fontSize = 9.sp,
                        color =
                            if (hasRead) {
                                MaterialTheme
                                    .colorScheme
                                    .onPrimary
                            } else {
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                            }
                    )
                }
            }
        }
    }
}


@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .background(
                color =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant,
                shape =
                    RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text = value,
            style =
                MaterialTheme
                    .typography
                    .titleLarge,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = label,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}


@Composable
private fun ReadingSessionCard(
    session: ReadingSession,
    bookTitle: String
) {
    val date =
        Instant
            .ofEpochMilli(
                session.startedAt
            )
            .atZone(
                ZoneId.systemDefault()
            )
            .toLocalDate()

    val formatter =
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy"
        )

    val pages =
        maxOf(
            0,
            session.endPage -
                    session.startPage
        )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant,
                shape =
                    RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text = bookTitle,
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text =
                date.format(formatter),
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "${
                    formatReadingDuration(
                        session.durationSeconds
                    )
                } · $pages pages",
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
        )
    }
}


private fun formatReadingDuration(
    seconds: Long
): String {
    val hours =
        seconds / 3600

    val minutes =
        (seconds % 3600) / 60

    return when {

        hours > 0 ->
            "${hours}h ${minutes}m"

        minutes > 0 ->
            "${minutes}m"

        else ->
            "${seconds}s"
    }
}


private fun buildAchievements(
    sessions: List<ReadingSession>,
    books: List<Book>,
    currentStreak: Int
): List<ReadingAchievement> {

    val totalPagesRead =
        sessions.sumOf { session ->
            maxOf(
                0,
                session.endPage -
                        session.startPage
            )
        }

    val totalReadingSeconds =
        sessions.sumOf { session ->
            session.durationSeconds
        }

    val hasFinishedBook =
        books.any { book ->
            book.status ==
                    ReadingStatus.FINISHED
        }

    return listOf(

        ReadingAchievement(
            title = "First Chapter",
            description =
                "Complete your first reading session",
            emoji = "🌱",
            isUnlocked =
                sessions.isNotEmpty()
        ),

        ReadingAchievement(
            title = "Page Turner",
            description =
                "Read 100 pages",
            emoji = "📖",
            isUnlocked =
                totalPagesRead >= 100
        ),

        ReadingAchievement(
            title = "Lost in a Book",
            description =
                "Read for 1 hour",
            emoji = "⏳",
            isUnlocked =
                totalReadingSeconds >= 3600
        ),

        ReadingAchievement(
            title = "On a Roll",
            description =
                "Reach a 3 day reading streak",
            emoji = "🔥",
            isUnlocked =
                currentStreak >= 3
        ),

        ReadingAchievement(
            title = "The End",
            description =
                "Finish your first book",
            emoji = "🏆",
            isUnlocked =
                hasFinishedBook
        )
    )
}


@Composable
private fun AchievementCard(
    achievement: ReadingAchievement
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant,
                shape =
                    RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = achievement.emoji,
            fontSize = 30.sp
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = achievement.title,
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurface
                        .copy(
                            alpha =
                                if (
                                    achievement
                                        .isUnlocked
                                ) {
                                    1f
                                } else {
                                    0.45f
                                }
                        )
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text =
                    achievement.description,
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }

        Text(
            text =
                if (
                    achievement.isUnlocked
                ) {
                    "✓"
                } else {
                    "🔒"
                },
            fontSize = 20.sp,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
        )
    }
}