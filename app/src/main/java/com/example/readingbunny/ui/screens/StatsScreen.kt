package com.example.readingbunny.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readingbunny.R
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.ReadingAchievement
import com.example.readingbunny.model.ReadingSession
import com.example.readingbunny.model.ReadingStatus
import com.example.readingbunny.ui.theme.ShelfWood
import com.example.readingbunny.util.calculateCurrentStreak
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

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

    var displayedYear by rememberSaveable {
        mutableStateOf(today.year)
    }

    var displayedMonth by rememberSaveable {
        mutableStateOf(today.monthValue)
    }

    var selectedDayEpoch by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    val selectedDay =
        selectedDayEpoch?.let {
            LocalDate.ofEpochDay(it)
        }

    val displayedYearMonth =
        YearMonth.of(
            displayedYear,
            displayedMonth
        )

    val sessionDates =
        sessions
            .map { session ->
                sessionDate(session)
            }
            .toSet()

    val currentStreak =
        calculateCurrentStreak(
            sessionDates = sessionDates,
            today = today
        )

    val longestStreak =
        calculateLongestStreak(
            sessionDates
        )

    val achievements =
        buildAchievements(
            sessions = sessions,
            books = books,
            currentStreak = currentStreak
        )

    val startOfWeek =
        today.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.MONDAY
            )
        )

    val endOfWeek =
        startOfWeek.plusDays(6)

    val filteredSessions =
        sessions.filter { session ->
            val date =
                sessionDate(session)

            when (selectedPeriod) {
                StatsPeriod.WEEK ->
                    !date.isBefore(startOfWeek) &&
                            !date.isAfter(endOfWeek)

                StatsPeriod.MONTH ->
                    YearMonth.from(date) ==
                            displayedYearMonth

                StatsPeriod.ALL_TIME ->
                    true
            }
        }

    val weekDays =
        (0L..6L).map { offset ->
            val date =
                startOfWeek.plusDays(offset)

            val sessionsForDay =
                sessions.filter {
                    sessionDate(it) == date
                }

            DayReadingActivity(
                date = date,
                readingSeconds =
                    sessionsForDay.sumOf {
                        it.durationSeconds
                    }
            )
        }

    val monthActivities =
        (1..displayedYearMonth.lengthOfMonth())
            .map { day ->
                val date =
                    displayedYearMonth.atDay(day)

                val sessionsForDay =
                    sessions.filter {
                        sessionDate(it) == date
                    }

                DayReadingActivity(
                    date = date,
                    readingSeconds =
                        sessionsForDay.sumOf {
                            it.durationSeconds
                        }
                )
            }

    val totalSeconds =
        filteredSessions.sumOf {
            it.durationSeconds
        }

    val pagesRead =
        filteredSessions.sumOf { session ->
            maxOf(
                0,
                session.endPage -
                        session.startPage
            )
        }

    val readingDays =
        filteredSessions
            .map {
                sessionDate(it)
            }
            .distinct()
            .size

    val averageSessionSeconds =
        if (filteredSessions.isNotEmpty()) {
            totalSeconds /
                    filteredSessions.size
        } else {
            0L
        }

    val finishedBooksCount =
        books.count { book ->
            if (
                book.status !=
                ReadingStatus.FINISHED
            ) {
                return@count false
            }

            when (selectedPeriod) {
                StatsPeriod.WEEK -> {
                    val finishedDate =
                        book.finishedAt?.let {
                                timestamp ->
                            Instant
                                .ofEpochMilli(timestamp)
                                .atZone(
                                    ZoneId.systemDefault()
                                )
                                .toLocalDate()
                        } ?: return@count false

                    !finishedDate.isBefore(
                        startOfWeek
                    ) &&
                            !finishedDate.isAfter(
                                endOfWeek
                            )
                }

                StatsPeriod.MONTH -> {
                    val finishedDate =
                        book.finishedAt?.let {
                                timestamp ->
                            Instant
                                .ofEpochMilli(timestamp)
                                .atZone(
                                    ZoneId.systemDefault()
                                )
                                .toLocalDate()
                        } ?: return@count false

                    YearMonth.from(
                        finishedDate
                    ) == displayedYearMonth
                }

                StatsPeriod.ALL_TIME ->
                    true
            }
        }

    val recentSessions =
        filteredSessions
            .sortedByDescending {
                it.startedAt
            }
            .take(5)

    val periodSubtitle =
        when (selectedPeriod) {
            StatsPeriod.WEEK ->
                stringResource(
                    R.string.stats_this_week
                )

            StatsPeriod.MONTH ->
                displayedYearMonth.format(
                    DateTimeFormatter.ofPattern(
                        "MMMM yyyy"
                    )
                )

            StatsPeriod.ALL_TIME ->
                stringResource(
                    R.string.stats_all_reading_activity
                )
        }

    val recentActivityTitle =
        when (selectedPeriod) {
            StatsPeriod.WEEK ->
                stringResource(
                    R.string.recent_activity_week
                )

            StatsPeriod.MONTH ->
                stringResource(
                    R.string.recent_activity_month
                )

            StatsPeriod.ALL_TIME ->
                stringResource(
                    R.string.recent_activity
                )
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme
                    .colorScheme
                    .background
            )
            .padding(20.dp)
    ) {
        item {
            Text(
                text = stringResource(
                    R.string.reading_journey
                ),
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onBackground
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text = periodSubtitle,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                StatsPeriodButton(
                    text =
                        stringResource(
                            R.string.stats_week
                        ),
                    selected =
                        selectedPeriod ==
                                StatsPeriod.WEEK,
                    modifier =
                        Modifier.weight(1f),
                    onClick = {
                        selectedPeriod =
                            StatsPeriod.WEEK
                    }
                )

                StatsPeriodButton(
                    text =
                        stringResource(
                            R.string.stats_month
                        ),
                    selected =
                        selectedPeriod ==
                                StatsPeriod.MONTH,
                    modifier =
                        Modifier.weight(1f),
                    onClick = {
                        selectedPeriod =
                            StatsPeriod.MONTH

                        displayedYear =
                            today.year

                        displayedMonth =
                            today.monthValue
                    }
                )

                StatsPeriodButton(
                    text =
                        stringResource(
                            R.string.stats_all_time
                        ),
                    selected =
                        selectedPeriod ==
                                StatsPeriod.ALL_TIME,
                    modifier =
                        Modifier.weight(1f),
                    onClick = {
                        selectedPeriod =
                            StatsPeriod.ALL_TIME
                    }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
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
                            RoundedCornerShape(
                                18.dp
                            )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text =
                                pluralStringResource(
                                    R.plurals.stats_day_streak,
                                    currentStreak,
                                    currentStreak
                                ),
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
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                if (
                                    currentStreak > 0
                                ) {
                                    stringResource(
                                        R.string.keep_reading_streak
                                    )
                                } else {
                                    stringResource(
                                        R.string.start_new_streak
                                    )
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

                    Column(
                        horizontalAlignment =
                            Alignment.End
                    ) {
                        Text(
                            text =
                                longestStreak
                                    .toString(),
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                ShelfWood
                        )

                        Text(
                            text =
                                stringResource(
                                    R.string.best_streak
                                ),
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
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        formatReadingDuration(
                            totalSeconds
                        ),
                    label =
                        stringResource(
                            R.string.stats_reading_time
                        )
                )

                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        pagesRead.toString(),
                    label =
                        stringResource(
                            R.string.stats_pages
                        )
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        filteredSessions
                            .size
                            .toString(),
                    label =
                        stringResource(
                            R.string.stats_sessions
                        )
                )

                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        readingDays.toString(),
                    label =
                        stringResource(
                            R.string.stats_reading_days
                        )
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        formatReadingDuration(
                            averageSessionSeconds
                        ),
                    label =
                        stringResource(
                            R.string.stats_average_session
                        )
                )

                StatCard(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        finishedBooksCount
                            .toString(),
                    label =
                        when (selectedPeriod) {
                            StatsPeriod.WEEK ->
                                stringResource(
                                    R.string.finished_this_week
                                )

                            StatsPeriod.MONTH ->
                                stringResource(
                                    R.string.finished_this_month
                                )

                            StatsPeriod.ALL_TIME ->
                                stringResource(
                                    R.string.finished_books
                                )
                        }
                )
            }

            if (
                selectedPeriod ==
                StatsPeriod.WEEK
            ) {
                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )

                Text(
                    text =
                        stringResource(
                            R.string.this_week_activity
                        ),
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
                    modifier =
                        Modifier.height(12.dp)
                )

                WeekActivityRow(
                    days = weekDays,
                    today = today
                )
            }

            if (
                selectedPeriod ==
                StatsPeriod.MONTH
            ) {
                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )

                Text(
                    text =
                        stringResource(
                            R.string.monthly_activity
                        ),
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
                    modifier =
                        Modifier.height(12.dp)
                )

                MonthlyReadingCalendar(
                    yearMonth =
                        displayedYearMonth,
                    activities =
                        monthActivities,
                    today = today,

                    onPreviousMonth = {
                        val previous =
                            displayedYearMonth
                                .minusMonths(1)

                        displayedYear =
                            previous.year

                        displayedMonth =
                            previous.monthValue
                    },

                    onNextMonth = {
                        val next =
                            displayedYearMonth
                                .plusMonths(1)

                        displayedYear =
                            next.year

                        displayedMonth =
                            next.monthValue
                    },

                    onDayClick = { date ->
                        selectedDayEpoch =
                            date.toEpochDay()
                    }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )

            Text(
                text =
                    stringResource(
                        R.string.milestones
                    ),
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
                modifier =
                    Modifier.height(12.dp)
            )

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                achievements.forEach {
                        achievement ->

                    AchievementCard(
                        achievement =
                            achievement
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )

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
                modifier =
                    Modifier.height(12.dp)
            )
        }

        if (recentSessions.isEmpty()) {
            item {
                Text(
                    text =
                        when (selectedPeriod) {
                            StatsPeriod.WEEK ->
                                stringResource(
                                    R.string.no_sessions_week
                                )

                            StatsPeriod.MONTH ->
                                stringResource(
                                    R.string.no_sessions_month
                                )

                            StatsPeriod.ALL_TIME ->
                                stringResource(
                                    R.string.no_sessions_yet
                                )
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
            items(recentSessions) {
                    session ->

                val book =
                    books.firstOrNull {
                            book ->
                        book.id ==
                                session.bookId
                    }

                ReadingSessionCard(
                    session = session,
                    bookTitle =
                        book?.title
                            ?: stringResource(
                                R.string.unknown_book
                            )
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
            }
        }
    }

    selectedDay?.let { date ->
        val sessionsForDay =
            sessions
                .filter {
                    sessionDate(it) == date
                }
                .sortedBy {
                    it.startedAt
                }

        ReadingDayDetailsDialog(
            date = date,
            sessions =
                sessionsForDay,
            books = books,
            onDismiss = {
                selectedDayEpoch = null
            }
        )
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
                        Color.White
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
        modifier =
            Modifier.fillMaxWidth(),
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
                    Color.White
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurface
                }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color =
                            backgroundColor,
                        shape =
                            RoundedCornerShape(
                                14.dp
                            )
                    )
                    .padding(
                        vertical = 10.dp
                    ),
                horizontalAlignment =
                    Alignment
                        .CenterHorizontally
            ) {
                Text(
                    text =
                        day.date
                            .dayOfWeek
                            .getDisplayName(
                                TextStyle.NARROW,
                                Locale.getDefault()
                            ),
                    fontSize = 12.sp,
                    color = textColor
                )

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
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
                        text =
                            stringResource(
                                R.string.today_label
                            ),
                        fontSize = 9.sp,
                        color =
                            if (hasRead) {
                                Color.White
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
private fun MonthlyReadingCalendar(
    yearMonth: YearMonth,
    activities: List<DayReadingActivity>,
    today: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant,
                shape =
                    RoundedCornerShape(20.dp)
            )
            .padding(14.dp),
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            TextButton(
                onClick =
                    onPreviousMonth
            ) {
                Text(
                    text = "‹",
                    fontSize = 26.sp,
                    color = ShelfWood
                )
            }

            Text(
                text =
                    yearMonth.format(
                        DateTimeFormatter.ofPattern(
                            "MMMM yyyy"
                        )
                    ),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurface
            )

            TextButton(
                onClick =
                    onNextMonth
            ) {
                Text(
                    text = "›",
                    fontSize = 26.sp,
                    color = ShelfWood
                )
            }
        }

        Row(
            modifier =
                Modifier.fillMaxWidth()
        ) {
            DayOfWeek.entries
                .forEach { dayOfWeek ->
                    Text(
                        text =
                            dayOfWeek
                                .getDisplayName(
                                    TextStyle.NARROW,
                                    Locale.getDefault()
                                ),
                        modifier =
                            Modifier.weight(1f),
                        textAlign =
                            TextAlign.Center,
                        fontSize = 11.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
        }

        val leadingEmptyCells =
            yearMonth
                .atDay(1)
                .dayOfWeek
                .value - 1

        val calendarCells =
            buildList<DayReadingActivity?> {
                repeat(
                    leadingEmptyCells
                ) {
                    add(null)
                }

                addAll(activities)

                while (size % 7 != 0) {
                    add(null)
                }
            }

        calendarCells
            .chunked(7)
            .forEach { week ->
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            4.dp
                        )
                ) {
                    week.forEach {
                            activity ->

                        if (activity == null) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        } else {
                            CalendarDayCell(
                                activity =
                                    activity,
                                isToday =
                                    activity.date ==
                                            today,
                                modifier =
                                    Modifier.weight(1f),
                                onClick = {
                                    onDayClick(
                                        activity.date
                                    )
                                }
                            )
                        }
                    }
                }
            }

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = "●",
                color = ShelfWood,
                fontSize = 11.sp
            )

            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )

            Text(
                text =
                    stringResource(
                        R.string.reading_activity
                    ),
                fontSize = 11.sp,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    activity: DayReadingActivity,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val hasRead =
        activity.readingSeconds > 0

    val background =
        if (hasRead) {
            ShelfWood
        } else {
            MaterialTheme
                .colorScheme
                .background
        }

    val textColor =
        if (hasRead) {
            Color.White
        } else {
            MaterialTheme
                .colorScheme
                .onSurface
        }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (isToday) {
                    Modifier.border(
                        width = 2.dp,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        shape =
                            RoundedCornerShape(
                                12.dp
                            )
                    )
                } else {
                    Modifier
                }
            )
            .background(
                color = background,
                shape =
                    RoundedCornerShape(
                        12.dp
                    )
            )
            .clickable(
                onClick = onClick
            )
            .padding(4.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text =
                activity.date
                    .dayOfMonth
                    .toString(),
            fontSize = 12.sp,
            fontWeight =
                if (isToday) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
            color = textColor
        )

        if (hasRead) {
            Text(
                text = "•",
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ReadingDayDetailsDialog(
    date: LocalDate,
    sessions: List<ReadingSession>,
    books: List<Book>,
    onDismiss: () -> Unit
) {
    val totalSeconds =
        sessions.sumOf {
            it.durationSeconds
        }

    val totalPages =
        sessions.sumOf {
            maxOf(
                0,
                it.endPage -
                        it.startPage
            )
        }

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "EEEE, dd MMMM yyyy"
        )

    AlertDialog(
        onDismissRequest =
            onDismiss,

        title = {
            Text(
                text =
                    date.format(
                        dateFormatter
                    ),
                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        max = 430.dp
                    )
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {
                if (sessions.isEmpty()) {
                    Text(
                        text =
                            stringResource(
                                R.string.no_reading_activity_day
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                } else {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                8.dp
                            )
                    ) {
                        StatCard(
                            modifier =
                                Modifier.weight(1f),
                            value =
                                formatReadingDuration(
                                    totalSeconds
                                ),
                            label =
                                stringResource(
                                    R.string.stats_reading_time
                                )
                        )

                        StatCard(
                            modifier =
                                Modifier.weight(1f),
                            value =
                                totalPages
                                    .toString(),
                            label =
                                stringResource(
                                    R.string.stats_pages
                                )
                        )
                    }

                    Text(
                        text =
                            pluralStringResource(
                                R.plurals.reading_sessions_count,
                                sessions.size,
                                sessions.size
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    sessions.forEach {
                            session ->

                        val book =
                            books.firstOrNull {
                                it.id ==
                                        session.bookId
                            }

                        ReadingSessionCard(
                            session = session,
                            bookTitle =
                                book?.title
                                    ?: stringResource(
                                        R.string.unknown_book
                                    )
                        )
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text =
                        stringResource(
                            R.string.close_action
                        )
                )
            }
        }
    )
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
                    RoundedCornerShape(
                        18.dp
                    )
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
            modifier =
                Modifier.height(4.dp)
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
        sessionDate(session)

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy"
        )

    val time =
        Instant
            .ofEpochMilli(
                session.startedAt
            )
            .atZone(
                ZoneId.systemDefault()
            )
            .toLocalTime()
            .format(
                DateTimeFormatter.ofPattern(
                    "HH:mm"
                )
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
                    RoundedCornerShape(
                        16.dp
                    )
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
            modifier =
                Modifier.height(5.dp)
        )

        Text(
            text =
                stringResource(
                    R.string.session_date_time,
                    date.format(
                        dateFormatter
                    ),
                    time
                ),
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
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                stringResource(
                    R.string.session_duration_pages,
                    formatReadingDuration(
                        session.durationSeconds
                    ),
                    pages
                ),
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

private fun sessionDate(
    session: ReadingSession
): LocalDate {
    return Instant
        .ofEpochMilli(
            session.startedAt
        )
        .atZone(
            ZoneId.systemDefault()
        )
        .toLocalDate()
}

private fun calculateLongestStreak(
    sessionDates: Set<LocalDate>
): Int {
    if (sessionDates.isEmpty()) {
        return 0
    }

    val sortedDates =
        sessionDates.sorted()

    var longestStreak = 1
    var currentStreak = 1

    for (
    index in 1 until
            sortedDates.size
    ) {
        val previousDate =
            sortedDates[index - 1]

        val currentDate =
            sortedDates[index]

        if (
            currentDate ==
            previousDate.plusDays(1)
        ) {
            currentStreak++

            if (
                currentStreak >
                longestStreak
            ) {
                longestStreak =
                    currentStreak
            }
        } else {
            currentStreak = 1
        }
    }

    return longestStreak
}

@Composable
private fun formatReadingDuration(
    seconds: Long
): String {
    val hours =
        seconds / 3600

    val minutes =
        (seconds % 3600) / 60

    return when {
        hours > 0 ->
            stringResource(
                R.string.duration_hours_minutes,
                hours,
                minutes
            )

        minutes > 0 ->
            stringResource(
                R.string.duration_minutes,
                minutes
            )

        else ->
            stringResource(
                R.string.duration_seconds,
                seconds
            )
    }
}

@Composable
private fun buildAchievements(
    sessions: List<ReadingSession>,
    books: List<Book>,
    currentStreak: Int
): List<ReadingAchievement> {
    val totalPagesRead =
        sessions.sumOf {
                session ->

            maxOf(
                0,
                session.endPage -
                        session.startPage
            )
        }

    val totalReadingSeconds =
        sessions.sumOf {
            it.durationSeconds
        }

    val hasFinishedBook =
        books.any {
            it.status ==
                    ReadingStatus.FINISHED
        }

    return listOf(
        ReadingAchievement(
            title =
                stringResource(
                    R.string.achievement_first_chapter
                ),
            description =
                stringResource(
                    R.string.achievement_first_chapter_description
                ),
            emoji = "🌱",
            isUnlocked =
                sessions.isNotEmpty()
        ),

        ReadingAchievement(
            title =
                stringResource(
                    R.string.achievement_page_turner
                ),
            description =
                stringResource(
                    R.string.achievement_page_turner_description
                ),
            emoji = "📖",
            isUnlocked =
                totalPagesRead >= 100
        ),

        ReadingAchievement(
            title =
                stringResource(
                    R.string.achievement_lost_in_book
                ),
            description =
                stringResource(
                    R.string.achievement_lost_in_book_description
                ),
            emoji = "⏳",
            isUnlocked =
                totalReadingSeconds >=
                        3600
        ),

        ReadingAchievement(
            title =
                stringResource(
                    R.string.achievement_on_a_roll
                ),
            description =
                stringResource(
                    R.string.achievement_on_a_roll_description
                ),
            emoji = "🔥",
            isUnlocked =
                currentStreak >= 3
        ),

        ReadingAchievement(
            title =
                stringResource(
                    R.string.achievement_the_end
                ),
            description =
                stringResource(
                    R.string.achievement_the_end_description
                ),
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
                    RoundedCornerShape(
                        16.dp
                    )
            )
            .padding(16.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text =
                achievement.emoji,
            fontSize = 30.sp
        )

        Spacer(
            modifier =
                Modifier.width(14.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {
            Text(
                text =
                    achievement.title,
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
                modifier =
                    Modifier.height(3.dp)
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