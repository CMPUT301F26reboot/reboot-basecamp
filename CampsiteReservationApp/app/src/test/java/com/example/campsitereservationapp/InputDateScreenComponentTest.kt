/**
 * Test for the control functions in InputDateScreenComponent:
 * nextDateRange, datesBetween, and formatDateRange
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.ui.components.datesBetween
import com.example.campsitereservationapp.ui.components.formatDateRange
import com.example.campsitereservationapp.ui.components.nextDateRange
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date
import java.util.Locale

class InputDateScreenComponentTest {

    private lateinit var originalLocale: Locale

    @Before
    fun setUp() {
        // formatDateRange uses the default locale, so fix it to get "May 1, 2027"
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    /** [date] plus [hours], to check that times are dropped. */
    private fun atHour(date: Date, hours: Int) = Date(date.time + hours * 60 * 60 * 1000L)

    // ---------- nextDateRange ----------

    @Test
    fun nextDateRange_noDatesChosen_startsNewRangeAtMidnight() {
        val result = nextDateRange(emptyList(), atHour(day(2027, 5, 1), 15))

        assertEquals(listOf(day(2027, 5, 1)), result)
    }

    @Test
    fun nextDateRange_oneDateChosen_tapLater_fillsRange() {
        val result = nextDateRange(listOf(day(2027, 5, 1)), day(2027, 5, 4))

        assertEquals(
            listOf(day(2027, 5, 1), day(2027, 5, 2), day(2027, 5, 3), day(2027, 5, 4)),
            result
        )
    }

    @Test
    fun nextDateRange_oneDateChosen_tapEarlier_fillsRangeInOrder() {
        val result = nextDateRange(listOf(day(2027, 5, 4)), day(2027, 5, 2))

        assertEquals(listOf(day(2027, 5, 2), day(2027, 5, 3), day(2027, 5, 4)), result)
    }

    @Test
    fun nextDateRange_oneDateChosen_tapSameDay_returnsOneDay() {
        val result = nextDateRange(listOf(day(2027, 5, 1)), day(2027, 5, 1))

        assertEquals(listOf(day(2027, 5, 1)), result)
    }

    @Test
    fun nextDateRange_rangeAlreadyChosen_startsNewRange() {
        val range = listOf(day(2027, 5, 1), day(2027, 5, 2), day(2027, 5, 3))

        val result = nextDateRange(range, day(2027, 6, 10))

        assertEquals(listOf(day(2027, 6, 10)), result)
    }

    // ---------- datesBetween ----------

    @Test
    fun datesBetween_includesBothEndsAcrossMonthEnd() {
        val result = datesBetween(day(2027, 4, 29), day(2027, 5, 2))

        assertEquals(
            listOf(day(2027, 4, 29), day(2027, 4, 30), day(2027, 5, 1), day(2027, 5, 2)),
            result
        )
    }

    @Test
    fun datesBetween_sameDay_returnsOneDay() {
        val result = datesBetween(atHour(day(2027, 5, 1), 9), atHour(day(2027, 5, 1), 20))

        assertEquals(listOf(day(2027, 5, 1)), result)
    }

    @Test
    fun datesBetween_startAfterEnd_returnsEmpty() {
        assertTrue(datesBetween(day(2027, 5, 5), day(2027, 5, 1)).isEmpty())
    }

    // ---------- formatDateRange ----------

    @Test
    fun formatDateRange_noDates_returnsEmptyText() {
        assertEquals("", formatDateRange(emptyList()))
    }

    @Test
    fun formatDateRange_oneDay_returnsOneDate() {
        assertEquals("May 1, 2027", formatDateRange(listOf(day(2027, 5, 1))))
    }

    @Test
    fun formatDateRange_range_returnsFirstToLastInAnyOrder() {
        val dates = listOf(day(2027, 5, 14), day(2027, 5, 1), day(2027, 5, 7))

        assertEquals("May 1, 2027 – May 14, 2027", formatDateRange(dates))
    }
}