package com.example.campsitereservationapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.campsitereservationapp.ui.entrant.WonBooking.toDayOnly
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * A reusable input composable for getting a range of dates from the user.
 * The caller owns the dates: this composable reads them and reports each tapped day.
 *
 * @param title OPTIONAL
 * @param subheader OPTIONAL
 * @param infoDescription OPTIONAL - description underneath input
 * @param dates the chosen days, shown in the input and on the calendar
 * @param onSelection called with each day the user taps. [nextDateRange] turns taps into a range.
 * @param removedDates OPTIONAL - list of dates that aren't available for selection (default: dates before
 * current aren't available)
 */

@Composable
fun InputDateScreenComponent(
    title: String? = null,
    subheader: String? = null,
    infoDescription: String? = null,
    dates: List<Date>,
    removedDates: List<Date>? = null,
    onSelection: (Date) -> Unit,
    modifier: Modifier,

    ){
    var showCalendar by rememberSaveable { mutableStateOf(false) }

    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (subheader != null) {
                Text(
                    text = subheader,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (title != null || subheader != null) {
                Spacer(Modifier.height(12.dp))
            }

            Surface(
                onClick = { showCalendar = true },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(52.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = if (dates.isEmpty()) "Select dates" else formatDateRange(dates),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (dates.isEmpty()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Choose dates",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (infoDescription != null) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = infoDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showCalendar) {
        DateRangeCalendarDialog(
            dates = dates,
            onSelection = onSelection,
            onDismiss = { showCalendar = false },
            title = title,
            removedDates = removedDates
        )
    }
}


//Preview of where each variable string goes
@Preview(showBackground = true)
@Composable
fun InputDateComponentPreview(){
    var dates by remember { mutableStateOf(emptyList<Date>()) }
    InputDateScreenComponent(
        title = "Title",
        subheader = "Subheader",
        infoDescription = "Info Description",
        dates = dates,
        onSelection = { day -> dates = nextDateRange(dates, day) },
        modifier = Modifier.fillMaxWidth()
    )

}


//example of no use of optional input
@Preview(showBackground = true)
@Composable
fun InputDataPreview_NoText(){
    var dates by remember { mutableStateOf(emptyList<Date>()) }
    InputDateScreenComponent(
        dates = dates,
        onSelection = { day -> dates = nextDateRange(dates, day) },
        modifier = Modifier.fillMaxWidth()
    )
}




//============================================================================================================
//========================Extension Functions for AvailabilityCalendar.kt Compatibility ======================
//============================================================================================================

/** How far ahead a date can be chosen. */
private const val MAX_DAYS_AHEAD = 730

/**
 * Shows [AvailabilityCalendar] in a dialog for choosing a date range.
 *
 * Holds no selection state. Each tapped day goes to [onSelection], and the caller passes the new
 * range back through [dates].
 */
@Composable
internal fun DateRangeCalendarDialog(
    dates: List<Date>,
    onSelection: (Date) -> Unit,
    onDismiss: () -> Unit,
    title: String? = null,
    removedDates: List<Date>? = null,
    ) {
    val availableDays = remember(removedDates) { selectableDays(removedDates) }

    Dialog(onDismissRequest = onDismiss) {
        Column {
            AvailabilityCalendar(
                availableDays = availableDays,
                selectedDays = dates.map { it.toDayOnly() }.toSet(),
                onDayClick = onSelection,
                title = title,
                initialMonth = (dates.minOrNull() ?: Date()).toYearMonth(),
                showLegend = true
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Turns a tapped day into the next date range. With one day already chosen, the tapped day ends
 * the range. Otherwise, the tapped day starts a new range.
 */
internal fun nextDateRange(dates: List<Date>, tapped: Date): List<Date> {
    if (dates.size != 1) return listOf(tapped.toDayOnly())
    val first = dates.first()
    return datesBetween(minOf(first, tapped), maxOf(first, tapped))
}

/** Every calendar day from [start] to [end], including both, at midnight. */
internal fun datesBetween(start: Date, end: Date): List<Date> {
    val days = mutableListOf<Date>()
    val calendar = Calendar.getInstance()
    calendar.time = start.toDayOnly()
    val lastDay = end.toDayOnly()
    while (!calendar.time.after(lastDay)) {
        days += calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}

/** Formats dates like "May 1, 2027 – May 14, 2027", or one date if they are all the same day. */
internal fun formatDateRange(dates: List<Date>): String {
    val first = dates.minOrNull() ?: return ""
    val last = dates.maxOrNull() ?: return ""
    if (first.toDayOnly() == last.toDayOnly()) return formatDate(first)
    return "${formatDate(first)} – ${formatDate(last)}"
}

/** Every day from today to [MAX_DAYS_AHEAD] days ahead, minus [removedDates]. */
private fun selectableDays(removedDates: List<Date>? = null): Set<Date> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, MAX_DAYS_AHEAD)
    val allDays = datesBetween(Date(), calendar.time)
    val unavailable = removedDates?.map { it.toDayOnly() }?.toSet().orEmpty()
    return allDays.filterNot { it in unavailable }.toSet()
}

/** Formats a date like "May 1, 2027". */
private fun formatDate(date: Date): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)

private fun Date.toYearMonth(): YearMonth {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
}