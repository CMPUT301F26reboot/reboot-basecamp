/*
 * AI use:
 * Claude Opus 5.5, 2026/11/2026
 *
 * Q. "Given the OwnerNotifLogViewModel, and the example file, without writing anything, please tell me
 * what tests I should do. Hints are welcome. Please also write the KDocs"
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import com.example.campsitereservationapp.ui.owner.Notifications.OwnerNotifLogViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Mock tests for [OwnerNotifLogViewModel], the controller behind the owner's
 * Notification History screen (US 02.10.02).
 */
class OwnerNotifLogViewModelTest {

    private val ownerId1 = "owner1"
    private val ownerId2 = "owner12" // contains "owner1" so a substring check would match it

    private val firestore = MockFirestore()
    private lateinit var viewModel: OwnerNotifLogViewModel

    private val fireBan = Notifications(
        senderId = ownerId1,
        recipientIds = listOf("device1", "device2"),
        type = "ownerNotif",
        subject = "Fire ban in effect",
        message = "Campfires are not allowed until further notice.",
        lotteryId = "lottery-july",
        timestamp = day(2026, 11, 20)
    ).apply { id = "notif-fire-ban" }

    private val checkIn = Notifications(
        senderId = ownerId1,
        recipientIds = listOf("device1"),
        type = "ownerNotif",
        subject = "Check-in time changed",
        message = "Check-in now starts at 3 PM.",
        lotteryId = "lottery-july",
        timestamp = day(2026, 11, 24)
    ).apply { id = "notif-check-in" }

    private val otherOwners = Notifications(
        senderId = ownerId2,
        recipientIds = listOf("device3"),
        type = "ownerNotif",
        subject = "Road closure",
        message = "The access road is closed for repairs.",
        lotteryId = "lottery-august",
        timestamp = day(2026, 11, 22)
    ).apply { id = "notif-other-owner" }

    private val julyLottery = Lottery().apply {
        id = "lottery-july"
        title = "July Long Weekend - Banff"
    }

    @Before
    fun setUp() {
        firestore.install()
        viewModel = OwnerNotifLogViewModel()
        firestore.deliver("Notifications", Notifications::class.java, listOf(fireBan, checkIn, otherOwners))
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun doSearch_emptyKeywordShouldShowThisOwnersNotificationsNewestFirst() {
        viewModel.doSearch("", ownerId1)

        assertEquals(listOf("notif-check-in", "notif-fire-ban"), viewModel._searchResults.map { it.id })
    }

    @Test
    fun doSearch_shouldNotShowAnotherOwnerWhoseIdContainsThisOne() {
        viewModel.doSearch("", ownerId1)

        assertTrue(viewModel._searchResults.none { it.id == "notif-other-owner" })
    }

    @Test
    fun doSearch_keywordInSubjectShouldShowOnlyMatches() {
        viewModel.doSearch("fire ban", ownerId1)

        assertEquals(listOf("notif-fire-ban"), viewModel._searchResults.map { it.id })
    }

    @Test
    fun doSearch_keywordInMessageShouldIgnoreCase() {
        viewModel.doSearch("3 pm", ownerId1)

        assertEquals(listOf("notif-check-in"), viewModel._searchResults.map { it.id })
    }

    @Test
    fun doSearch_noMatchesShouldBeEmpty() {
        viewModel.doSearch("refund", ownerId1)

        assertTrue(viewModel._searchResults.isEmpty())
    }

    @Test
    fun getLotName_shouldReturnTitleOrFallback() {
        assertEquals("July Long Weekend - Banff", viewModel.getLotName("lottery-july"))
        assertEquals("No lottery found", viewModel.getLotName("lottery-deleted"))
    }
}