package com.example.campsitereservationapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.DateFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle as DateTextStyle
import java.util.Calendar
import java.util.Date

/**
 * A month grid that shows which days are available, selected, or unavailable.
 *
 * @param availableDays days that can be chosen (drawn green).
 * @param selectedDays days currently chosen (drawn dark; wins over available).
 * @param onDayClick called when an available or selected day is tapped. Unavailable days never call it.
 *        Pass an empty lambda for a view-only calendar.
 * @param title optional heading inside the card, e.g. "Available spots" or "Availability".
 * @param initialMonth month to open on. If it arrives late (data still loading), the calendar
 *        jumps to it, unless the user has already moved to another month.
 * @param firstDayOfWeek which weekday the columns start on.
 * @param showLegend shows the "Green: available · Grey: unavailable" caption.
 */
@Composable
fun AvailabilityCalendar(
    availableDays: Set<Date>,
    selectedDays: Set<Date>,
    onDayClick: (Date) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    initialMonth: YearMonth? = null,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    showLegend: Boolean = false
) {
    var shownMonth by rememberSaveable(stateSaver = YearMonthSaver) {
        mutableStateOf(initialMonth ?: YearMonth.now())
    }
    var userNavigated by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialMonth) {
        if (initialMonth != null && !userNavigated) shownMonth = initialMonth
    }

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
            }

            MonthHeader(
                month = shownMonth,
                onPrevious = {
                    userNavigated = true
                    shownMonth = shownMonth.minusMonths(1)
                },
                onNext = {
                    userNavigated = true
                    shownMonth = shownMonth.plusMonths(1)
                }
            )
            Spacer(Modifier.height(8.dp))
            WeekdayHeader(firstDayOfWeek)
            Spacer(Modifier.height(4.dp))
            MonthGrid(
                month = shownMonth,
                firstDayOfWeek = firstDayOfWeek,
                availableDays = availableDays,
                selectedDays = selectedDays,
                onDayClick = onDayClick
            )

            if (showLegend) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Green: available · Grey: unavailable",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Month/year title with previous/next month navigation arrows. */
@Composable
private fun MonthHeader(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${month.month.getDisplayName(DateTextStyle.FULL, locale)} ${month.year}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

/** Row of short weekday labels (Mon, Tue, ...) above the day grid. */
@Composable
private fun WeekdayHeader(firstDayOfWeek: DayOfWeek) {
    val locale = LocalConfiguration.current.locales[0]
    Row(modifier = Modifier.fillMaxWidth()) {
        for (i in 0..6) {
            val day = firstDayOfWeek.plus(i.toLong())
            Text(
                text = day.getDisplayName(DateTextStyle.SHORT, locale),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * The grid of day cells for one month.
 *
 * Built from plain Rows and Columns on purpose: a LazyVerticalGrid can't be nested inside a
 * scrolling screen, and a month never has more than 42 cells.
 */
@Composable
private fun MonthGrid(
    month: YearMonth,
    firstDayOfWeek: DayOfWeek,
    availableDays: Set<Date>,
    selectedDays: Set<Date>,
    onDayClick: (Date) -> Unit
) {
    val daysInMonth = month.lengthOfMonth()
    val leadingBlanks = firstWeekdayOffset(month, firstDayOfWeek)
    val rows = (leadingBlanks + daysInMonth + 6) / 7

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(7) { column ->
                    val dayOfMonth = row * 7 + column - leadingBlanks + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayOfMonth in 1..daysInMonth) {
                            val date = dateOf(month.year, month.monthValue, dayOfMonth)
                            val state = when {
                                date in selectedDays -> DayState.Selected
                                date in availableDays -> DayState.Available
                                else -> DayState.Unavailable
                            }
                            DayCell(
                                date = date,
                                dayOfMonth = dayOfMonth,
                                state = state,
                                onClick = { onDayClick(date) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** One circular day cell, colored by [state] and clickable unless unavailable. */
@Composable
private fun DayCell(
    date: Date,
    dayOfMonth: Int,
    state: DayState,
    onClick: () -> Unit
) {
    val (background, content) = when (state) {
        DayState.Selected ->
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        DayState.Available ->
            AvailableBackground to AvailableContent
        DayState.Unavailable ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    val spokenState = when (state) {
        DayState.Selected -> "selected"
        DayState.Available -> "available"
        DayState.Unavailable -> "unavailable"
    }
    val locale = LocalConfiguration.current.locales[0]
    val description =
        "${DateFormat.getDateInstance(DateFormat.LONG, locale).format(date)}, $spokenState"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(background)
            .clickable(
                enabled = state != DayState.Unavailable,
                role = Role.Button,
                onClick = onClick
            )
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (state == DayState.Selected) FontWeight.Bold else FontWeight.Medium,
            color = content
        )
    }
}

/** Visual state of a single day cell in [MonthGrid]. */
private enum class DayState { Selected, Available, Unavailable }

private val AvailableBackground = Color(0xFFCDEFD3)
private val AvailableContent = Color(0xFF1B5E20)

private val YearMonthSaver = Saver<YearMonth, String>(
    save = { it.toString() },
    restore = { YearMonth.parse(it) }
)

/** Weekday index (0 = [firstDayOfWeek]) of the 1st of [month], computed with [Calendar] instead of `LocalDate`. */
private fun firstWeekdayOffset(month: YearMonth, firstDayOfWeek: DayOfWeek): Int {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(month.year, month.monthValue - 1, 1)
    // Calendar.DAY_OF_WEEK is Sunday=1..Saturday=7; convert to ISO Monday=1..Sunday=7.
    val isoDayOfWeek = ((cal.get(Calendar.DAY_OF_WEEK) + 5) % 7) + 1
    return (isoDayOfWeek - firstDayOfWeek.value + 7) % 7
}

/** Builds a [Date] for [year]-[month]-[day] at midnight local time, with no time-of-day component. */
internal fun dateOf(year: Int, month: Int, day: Int): Date {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(year, month - 1, day, 0, 0, 0)
    return cal.time
}
