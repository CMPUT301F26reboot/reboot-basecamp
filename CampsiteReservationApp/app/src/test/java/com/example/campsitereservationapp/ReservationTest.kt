/*
 * AI use
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * Tests for [Reservation], the model the Won Booking screen reads to block days that are
 * already booked at a campsite.
 */
class ReservationTest {

    private val entrantId = "entrant-device-0c9d55e1"
    private val campsiteId = "listing-two-jack"

    private lateinit var reservation: Reservation

    private fun date(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month - 1, day)
        return cal.time
    }

    @Before
    fun setUp() {
        reservation = Reservation(
            dates = listOf(date(2027, 7, 10)),
            entrantID = entrantId,
            campsiteID = campsiteId
        )
    }

    @Test
    fun constructor_shouldStoreBookingDetails() {
        assertEquals(listOf(date(2027, 7, 10)), reservation.dates)
        assertEquals(entrantId, reservation.entrantID)
        assertEquals(campsiteId, reservation.campsiteID)
    }

    @Test
    fun newReservation_shouldBeReservedAndUnrated() {
        assertEquals("reserved", reservation.status)
        assertFalse(reservation.rated)
    }

    @Test
    fun setStatus_shouldChangeStatus() {
        reservation.status = "cancelled"

        assertEquals("cancelled", reservation.status)
    }

    @Test
    fun setDates_shouldChangeDates() {
        val newDates = listOf(date(2027, 7, 9), date(2027, 7, 10))

        reservation.dates = newDates

        assertEquals(newDates, reservation.dates)
    }
}
