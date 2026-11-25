/*
 * DateUtils.kt
 *
 * The one definition of "a day" used across the app. Firestore stores every Date as a UTC
 * Timestamp (an exact moment), so two Dates only match if they are the same moment. Every day the
 * app saves, shows on a calendar, or compares is therefore snapped to midnight on the phone's
 * local clock with [toDayOnly], and range ends are checked against [endOfDay].
 */
package com.example.campsitereservationapp.utils

import java.util.Calendar
import java.util.Date

/**
 * Snaps this date to midnight local time, so two Dates on the same calendar day compare equal.
 * Use on every day before saving it, putting it on a calendar, or comparing it, including days
 * read back from Firestore.
 */
fun Date.toDayOnly(): Date {
    val cal = Calendar.getInstance()
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
    val cal = Calendar.getInstance()
    cal.time = toDayOnly()
    cal.add(Calendar.DAY_OF_MONTH, 1)
    cal.add(Calendar.MILLISECOND, -1)
    return cal.time
}
