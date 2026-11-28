/*
 * AI use:
 * Claude Opus 5.5, 2026/11/2026
 *
 * Q. "Okay now given the OwnerSendNotificationViewModel, and the example file, without writing anything, please tell me
 * what tests I should do. Hints are welcome. Please also write the KDocs"
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.owner.Notifications.OwnerSendNotificationViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Mock tests for [OwnerSendNotificationViewModel], the controller behind the owner's
 * Send Notification screen (US 02.10.01).
 */
class OwnerSendNotificationViewModelTest {

    private val ownerId = "owner1"

    private val firestore = MockFirestore()
    private val addNotification = MockTask<DocumentReference>()
    private lateinit var viewModel: OwnerSendNotificationViewModel

    private val julyLottery = Lottery().apply {
        id = "lottery-july"
        title = "July Long Weekend - Banff"
        waitlist = listOf("device1", "device2", "device3")
    }

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Notifications").add(any()) } returns addNotification.task
        viewModel = OwnerSendNotificationViewModel()
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    private fun sendFireBan(lotteryId: String = "lottery-july", onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
        viewModel.onSend(
            subject = "Fire ban in effect",
            message = "Campfires are not allowed until further notice.",
            ownerId = ownerId,
            lotteryId = lotteryId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    @Test
    fun getLottery_shouldFindLoadedLotteryOrReturnNull() {
        assertEquals("July Long Weekend - Banff", viewModel.getLottery("lottery-july")?.title)
        assertNull(viewModel.getLottery("lottery-deleted"))
    }

    @Test
    fun onSend_shouldSendNotificationToWholeWaitlist() {
        val sent = slot<Map<String, Any?>>()

        sendFireBan()

        verify { firestore.collection("Notifications").add(capture(sent)) }
        assertEquals(julyLottery.waitlist, sent.captured["recipientIds"])
        assertEquals(ownerId, sent.captured["senderId"])
        assertEquals("ownerNotif", sent.captured["type"])
        assertEquals("Fire ban in effect", sent.captured["subject"])
        assertEquals("lottery-july", sent.captured["lotteryId"])
    }

    @Test
    fun onSend_writeSucceedsShouldCallOnSuccess() {
        var succeeded = false
        var failed = false

        sendFireBan(onSuccess = { succeeded = true }, onFailure = { failed = true })
        addNotification.succeed(mockk_doc())

        assertTrue(succeeded)
        assertFalse(failed)
    }

    @Test
    fun onSend_writeFailsShouldCallOnFailure() {
        var succeeded = false
        var failed = false

        sendFireBan(onSuccess = { succeeded = true }, onFailure = { failed = true })
        addNotification.fail()

        assertTrue(failed)
        assertFalse(succeeded)
    }

    @Test
    fun onSend_lotteryNotLoadedShouldFailWithoutSaving() {
        var failed = false

        sendFireBan(lotteryId = "lottery-deleted", onFailure = { failed = true })

        assertTrue(failed)
        verify(exactly = 0) { firestore.collection("Notifications").add(any()) }
    }

    private fun mockk_doc(): DocumentReference = io.mockk.mockk(relaxed = true)
}