/*
 * This file tests the user notifications view model
 *
 * AI use:
 * Claude Opus 5.5, 2026/11/26
 *
 * Q. "I have attached an example of the viewModel tests we have, as well as the schema for Notifications document.
 * Please create some mock notifications in a similar way and a setup function for me to create some tests for UserNotificationsViewModelTests"
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import com.example.campsitereservationapp.ui.entrant.Notifications.UserNotificationsViewModel
import io.mockk.every
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Mock tests for [UserNotificationsViewModel], which shows an entrant their notifications and
 * lets them search and open them (US 01.03.01, US 01.03.02, US 01.05.02, US 01.07.02).
 */
class UserNotificationsViewModelTests {

    private val entrantId1 = "entrant-device1"
    private val entrantId2 = "entrant-device2"

    private val wonLottery = Notifications(
        senderId = "",
        recipientIds = listOf(entrantId1),
        type = "wonLottery",
        subject = "You won the lottery!",
        message = "You won the Mid-May Lakeside Week lottery. Tap to choose your dates.",
        lotteryId = "lottery-mid-may",
        timestamp = day(2026, 11, 20)
    ).apply { id = "notif-won" }

    private val reservationCancelled = Notifications(
        senderId = "",
        recipientIds = listOf(entrantId1),
        type = "reserveCancel",
        subject = "Reservation Cancelled",
        message = "Your reservation at Lakehouse was cancelled.",
        reservationId = "reservation-lakehouse-may",
        timestamp = day(2026, 11, 22)
    ).apply { id = "notif-cancelled" }

    // Owner broadcast sent to both entrants
    private val ownerBroadcast = Notifications(
        senderId = "owner-device-7f3a91c2",
        recipientIds = listOf(entrantId1, entrantId2),
        type = "ownerNotif",
        subject = "Fire ban update",
        message = "Campfires are not allowed at Lakehouse until further notice.",
        timestamp = day(2026, 11, 24)
    ).apply { id = "notif-broadcast" }

    // Sent only to someone else, so it should never show up for entrantId
    private val someoneElses = Notifications(
        senderId = "",
        recipientIds = listOf(entrantId2),
        type = "lostLottery",
        subject = "Lottery results",
        message = "You were not selected for the Early May Getaway lottery.",
        timestamp = day(2026, 11, 21)
    ).apply { id = "notif-other" }

    private val firestore = MockFirestore()
    private lateinit var viewModel: UserNotificationsViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = UserNotificationsViewModel()
        firestore.deliver(
            "Notifications",
            Notifications::class.java,
            listOf(wonLottery, reservationCancelled, ownerBroadcast, someoneElses)
        )
    }

    // The following is where the Claude output referenced above stops

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun doSearch_emptyKeywordShouldShowAllOfThisEntrantsNotifications() {
        viewModel.doSearch("", entrantId1)

        val shown = viewModel._searchResults.map { it.id }.toSet()
        assertEquals(setOf("notif-won", "notif-cancelled", "notif-broadcast"), shown)
    }

    @Test
    fun doSearch_shouldNeverShowNotificationsSentToSomeoneElse() {
        viewModel.doSearch("", entrantId1)

        assertTrue(viewModel._searchResults.none { it.id == "notif-other" })
    }

    @Test
    fun doSearch_keywordMatchesSubjectShouldShowOnlyMatchingNotifications() {
        viewModel.doSearch("Reservation", entrantId1)

        assertEquals(listOf("notif-cancelled"), viewModel._searchResults.map { it.id })
    }

    @Test
    fun doSearch_keywordIgnoresCaseShouldStillMatch() {
        viewModel.doSearch("fire ban", entrantId1)

        assertEquals(listOf("notif-broadcast"), viewModel._searchResults.map { it.id })
    }

    @Test
    fun markViewed_shouldAddEntrantToTheNotificationsViewedList() {
        every {
            firestore.collection("Notifications").document("notif-broadcast").update(any<String>(), any())
        } returns MockTask<Void?>().task

        viewModel.markViewed("notif-broadcast", entrantId1)

        verify {
            firestore.collection("Notifications").document("notif-broadcast").update("viewedBy", any())
        }
    }
}