package com.example.campsitereservationapp

import com.example.campsitereservationapp.ui.entrant.CampsiteDetails.formatDateRange
import com.example.campsitereservationapp.ui.entrant.CampsiteDetails.parseRouteDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.Date

class UserCampsiteDetailsTest {

    // Makes a Date for the given day
    private fun date(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month - 1, day)
        return cal.time
    }

    @Test
    fun formatDateRange_twoDates_showsRange() {
        val result = formatDateRange(listOf(date(2027, 5, 1), date(2027, 5, 7)))

        assertEquals("May 1, 2027 - May 7, 2027", result)
    }

    @Test
    fun formatDateRange_noDates_showsNoDatesFound() {
        assertEquals("No dates found", formatDateRange(emptyList()))
    }

    @Test
    fun parseRouteDate_validDate_returnsCorrectDate() {
        assertEquals(date(2027, 5, 1), parseRouteDate("2027-05-01"))
    }

    @Test
    fun parseRouteDate_null_returnsNull() {
        assertNull(parseRouteDate(null))
    }
}