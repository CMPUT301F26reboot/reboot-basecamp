package com.example.campsitereservationapp.utils

import com.example.campsitereservationapp.ui.components.dateOf
import java.time.MonthDay
import java.time.Year
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class DateFormatter {
    fun formatDate(date: Date): Date{
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("MST"))
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 17)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}