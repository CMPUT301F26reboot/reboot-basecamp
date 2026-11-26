/*
 * DateUtils.kt
 *
 * The one definition of "a day" used across the app. Firestore stores every Date as a UTC
 * Timestamp (an exact moment), so two Dates only match if they are the same moment. Every day the
 * app saves, shows on a calendar, or compares is therefore snapped to midnight in [APP_TIME_ZONE]
 * with [toDayOnly], and range ends are checked against [endOfDay].
 *
 * All campsites are in Alberta, so days, deadlines and displayed dates all use Alberta time
 * (America/Edmonton, which handles daylight saving), whatever time zone the phone is set to.

 */
package com.example.campsitereservationapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** The time zone the whole app works in: Alberta time */
val APP_TIME_ZONE: TimeZone = TimeZone.getTimeZone("America/Edmonton")

/** A Calendar set to [APP_TIME_ZONE] and the current time. Use instead of Calendar.getInstance(). */
fun appCalendar(): Calendar = Calendar.getInstance(APP_TIME_ZONE)

/**
 * A date formatter that shows and reads dates in [APP_TIME_ZONE], e.g. appDateFormat("MMM d, yyyy").
 * Use instead of SimpleDateFormat so a day shows the same on every phone.
 */
fun appDateFormat(pattern: String): SimpleDateFormat =
    SimpleDateFormat(pattern, Locale.getDefault()).apply { timeZone = APP_TIME_ZONE }

/**
 * Snaps this date to midnight local time, so two Dates on the same calendar day compare equal.
 * Use on every day before saving it, putting it on a calendar, or comparing it, including days
 * read back from Firestore.
 */
fun Date.toDayOnly(): Date {
    val cal = appCalendar()
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

/**
 * Use when checking whether a range that ends on this day is over,
 * so the range stays open for the whole of its last day.
 */
fun Date.endOfDay(): Date {
    val cal = appCalendar()
    cal.time = toDayOnly()
    cal.add(Calendar.DAY_OF_MONTH, 1)
    cal.add(Calendar.MILLISECOND, -1)
    return cal.time
}
