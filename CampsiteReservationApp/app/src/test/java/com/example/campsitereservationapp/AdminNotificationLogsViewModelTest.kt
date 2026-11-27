/*
 * AI use:
 * Claude Opus 5.5, 2026/11/2026
 *
 * Q. "Given the AdminNotificationLogsViewModel, and the example file, without writing anything, please tell me
 * what tests I should do. Hints are welcome, as well as the structure for the Notifications to test with (can be monotonous to fill it out myself).
 * Please also write the KDocs"
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.ui.Admin.Notifications.AdminNotificationLogsViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Mock tests for [AdminNotificationLogsViewModel], the controller behind the admin's Notification Log Screen
 */
class AdminNotificationLogsViewModelTest {

    private val ownerId = "owner-device1"

    private val firestore = MockFirestore()
    private lateinit var viewModel: AdminNotificationLogsViewModel

    private val fireBan = Notifications(
        senderId = ownerId,
        recipientIds = listOf("device1", "device2"),
        type = "ownerNotif",
        subject = "Fire ban in effect",
        message = "Campfires are not allowed until further notice.",
        lotteryId = "lottery-july",
        timestamp = day(2026, 11, 20)
    ).apply { id = "notif-fire-ban" }

    private val cancelled = Notifications(
        senderId = "system",
        recipientIds = listOf("device1"),
        type = "reserveCancel",
        subject = "Reservation cancelled",
        message = "Your reservation at Two Jack Lakeside has been cancelled.",
        reservationId = "reservation-1",
        timestamp = day(2026, 11, 24)
    ).apply { id = "notif-cancelled" }

    private val won = Notifications(
        senderId = "system",
        recipientIds = listOf("device2"),
        type = "wonLottery",
        subject = "You won the lottery!",
        message = "Pick your dates for the July Long Weekend.",
        lotteryId = "lottery-july",
        timestamp = day(2026, 11, 22)
    ).apply { id = "notif-won" }

    private val owner = Profile(name = "Joseph Owner", deviceId = ownerId).apply { id = ownerId }

    @Before
    fun setUp() {
        firestore.install()
        viewModel = AdminNotificationLogsViewModel()
        firestore.deliver("Notifications", Notifications::class.java, listOf(fireBan, cancelled, won))
        firestore.deliver("Profiles", Profile::class.java, listOf(owner))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun doSearch_emptyKeyword_shouldShowEveryNotificationNewestFirst() {
        viewModel.doSearch("")

        assertEquals(
            listOf("notif-cancelled", "notif-won", "notif-fire-ban"),
            viewModel._searchResults.map { it.id }
        )
    }

    @Test
    fun doSearch_keywordInSubject_shouldShowOnlyMatches() {
        viewModel.doSearch("fire ban")

        assertEquals(listOf("notif-fire-ban"), viewModel._searchResults.map { it.id })
    }

    @Test
    fun doSearch_keywordInMessage_shouldIgnoreCase() {
        viewModel.doSearch("TWO JACK")

        assertEquals(listOf("notif-cancelled"), viewModel._searchResults.map { it.id })
    }

    @Test
    fun doSearch_noMatches_shouldBeEmpty() {
        viewModel.doSearch("refund")

        assertTrue(viewModel._searchResults.isEmpty())
    }

    @Test
    fun getSenderName_shouldReturnProfileNameOrSystem() {
        assertEquals("Joseph Owner", viewModel.getSenderName(ownerId))
        assertEquals("System", viewModel.getSenderName("system"))
    }

    @Test
    fun getSenderName_shouldAlsoMatchByDeviceId() {
        val entrant = Profile(name = "Alex", deviceId = "device1").apply { id = "profile-doc-1" }
        firestore.deliver("Profiles", Profile::class.java, listOf(owner, entrant))

        assertEquals("Alex", viewModel.getSenderName("device1"))
    }

    @Test
    fun deleteNotification_shouldDeleteThatDocument() {
        val doc = mockk<DocumentReference>(relaxed = true)
        every { firestore.collection("Notifications").document("notif-fire-ban") } returns doc

        viewModel.deleteNotification("notif-fire-ban")

        verify { doc.delete() }
    }
}