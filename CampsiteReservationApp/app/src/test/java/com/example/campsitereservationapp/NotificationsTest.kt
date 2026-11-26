/* File to test the Notifications document */

package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * Tests for [Notifications], the model the Notifications screen reads to show an entrant their
 * lottery results, reservation updates, and owner messages.
 */
class NotificationsTest {

    private val ownerId = "owner-device"
    private val entrantId1 = "entrant-device1"
    private val entrantId2 = "entrant-device2"

    private lateinit var notification: Notifications

    private fun date(year: Int, month: Int, day: Int): Date {
        val cal = appCalendar()
        cal.clear()
        cal.set(year, month - 1, day)
        return cal.time
    }

    @Before
    fun setUp() {
        notification = Notifications(
            senderId = ownerId,
            recipientIds = listOf(entrantId1, entrantId2),
            type = "ownerNotif",
            subject = "Fire ban update",
            message = "Campfires are not allowed at Two Jack Lakeside until further notice.",
            lotteryId = "lottery-summer-banff",
            timestamp = date(2027, 6, 15)
        )
    }

    @Test
    fun constructor_shouldStoreNotificationDetails() {
        assertEquals(ownerId, notification.senderId)
        assertEquals(listOf(entrantId1, entrantId2), notification.recipientIds)
        assertEquals("ownerNotif", notification.type)
        assertEquals("Fire ban update", notification.subject)
        assertEquals("lottery-summer-banff", notification.lotteryId)
        assertEquals(date(2027, 6, 15), notification.timestamp)
    }

    @Test
    fun newNotification_shouldNotBeViewedByAnyone() {
        assertTrue(notification.viewedBy.isEmpty())
    }

    @Test
    fun newNotification_shouldHaveNoReservation() {
        assertEquals("", notification.reservationId)
    }

    @Test
    fun setViewedBy_shouldOnlyMarkThatEntrantAsViewed() {
        notification.viewedBy = listOf(entrantId1)

        assertTrue(entrantId1 in notification.viewedBy)
        assertTrue(entrantId2 !in notification.viewedBy)
    }

    @Test
    fun setType_shouldChangeType() {
        notification.type = "reserveCancel"

        assertEquals("reserveCancel", notification.type)
    }
}