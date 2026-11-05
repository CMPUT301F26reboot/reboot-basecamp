package com.example.campsitereservationapp

import com.example.campsitereservationapp.ui.entrant.computeBookableDays
import com.example.campsitereservationapp.ui.entrant.toDayOnly
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date

class UserWonBookingTest {

    // Makes a Date for the given day at midnight
    private fun date(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month - 1, day)
        return cal.time
    }

    // Makes a Date for the given day and time
    private fun dateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month - 1, day, hour, minute)
        return cal.time
    }

    @Test
    fun toDayOnly_stripsTimeOfDay() {
        val withTime = dateTime(2027, 5, 1, 14, 30)

        val result = withTime.toDayOnly()

        assertEquals(date(2027, 5, 1), result)
    }

    @Test
    fun toDayOnly_sameDayDifferentTimes_areEqual() {
        val morning = dateTime(2027, 5, 1, 7, 0)
        val night = dateTime(2027, 5, 1, 23, 45)

        assertEquals(morning.toDayOnly(), night.toDayOnly())
    }

    @Test
    fun computeBookableDays_removesReservedDays() {
        val lotteryDays = setOf(date(2027, 5, 1), date(2027, 5, 2), date(2027, 5, 3))
        val reservedDays = setOf(date(2027, 5, 2))

        val result = computeBookableDays(lotteryDays, reservedDays)

        assertEquals(setOf(date(2027, 5, 1), date(2027, 5, 3)), result)
    }

    @Test
    fun computeBookableDays_noReservations_returnsAllLotteryDays() {
        val lotteryDays = setOf(date(2027, 5, 1), date(2027, 5, 2))

        val result = computeBookableDays(lotteryDays, emptySet())

        assertEquals(lotteryDays, result)
    }

    @Test
    fun computeBookableDays_everyDayReserved_returnsEmpty() {
        val lotteryDays = setOf(date(2027, 5, 1))

        val result = computeBookableDays(lotteryDays, lotteryDays)

        assertTrue(result.isEmpty())
    }
}
