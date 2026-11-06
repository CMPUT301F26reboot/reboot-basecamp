/*
 * AI use:
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * Tests for [Lottery], the model that holds the dates and campsites an entrant can win a
 * booking for on the Won Booking screen.
 */
class LotteryTest {

    private val campsiteIds = listOf("listing-two-jack", "listing-tunnel-mtn")

    private lateinit var lottery: Lottery

    private fun date(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month - 1, day)
        return cal.time
    }

    @Before
    fun setUp() {
        lottery = Lottery(
            dates = listOf(date(2027, 7, 9), date(2027, 7, 10), date(2027, 7, 11)),
            start = date(2027, 5, 1),
            end = date(2027, 5, 31),
            campsiteIDs = campsiteIds
        )
    }

    @Test
    fun constructor_shouldStoreDatesAndCampsites() {
        assertEquals(listOf(date(2027, 7, 9), date(2027, 7, 10), date(2027, 7, 11)), lottery.dates)
        assertEquals(date(2027, 5, 1), lottery.start)
        assertEquals(date(2027, 5, 31), lottery.end)
        assertEquals(campsiteIds, lottery.campsiteIDs)
    }

    @Test
    fun newLottery_shouldHaveEmptyWaitlistAndNoLimits() {
        assertTrue(lottery.waitlist.isEmpty())
        assertNull(lottery.waitlistCapacity)
        assertNull(lottery.lotteryWinners)
    }

    @Test
    fun addEntrant_shouldAddToWaitlist() {
        lottery.addEntrant("entrant-device-a81c4e07")

        assertEquals(listOf("entrant-device-a81c4e07"), lottery.waitlist)
    }

    @Test
    fun addEntrant_shouldKeepJoinOrder() {
        lottery.addEntrant("entrant-device-a81c4e07")
        lottery.addEntrant("entrant-device-0c9d55e1")
        lottery.addEntrant("entrant-device-5be2f310")

        assertEquals(
            listOf("entrant-device-a81c4e07", "entrant-device-0c9d55e1", "entrant-device-5be2f310"),
            lottery.waitlist
        )
    }

    @Test
    fun setCampsiteIds_shouldChangeCampsites() {
        lottery.campsiteIDs = listOf("listing-lake-louise")

        assertEquals(listOf("listing-lake-louise"), lottery.campsiteIDs)
    }
}
