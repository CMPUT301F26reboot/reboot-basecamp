/*
 * AI use (full log: "AI Use Logs - Jeel/AI-Use-Log_Jeel.md"):
 * - Q: How do I test ViewModels that connect to Firebase when they're created?
 *   A: Plain JUnit for the models; MockK to mock Firebase.firestore for the ViewModels.
 * - Some of the mock tests were written using Claude's output: the shared MockFirestore.kt helper
 *   and the ViewModel tests that use it.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.entrant.CampsiteDetails.UserCampsiteDetailsViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Mock tests for [UserCampsiteDetailsViewModel], which shows a campsite's open lotteries and lets
 * the entrant join one. Its date helpers are tested in [UserCampsiteDetailsTest].
 */
class UserCampsiteDetailsViewModelTest {

    private val twoJack = Listing(name = "Two Jack Lakeside").apply { id = "listing-two-jack" }

    /** Open now, and offers stays 30-32 days from today. */
    private val openLottery = Lottery(
        dates = listOf(daysFromNow(30), daysFromNow(31), daysFromNow(32)),
        start = daysFromNow(-5),
        end = daysFromNow(20),
        campsiteIDs = listOf("listing-two-jack")
    ).apply {
        id = "lottery-open"
        title = "Summer Weekend - Banff"
        waitlist = listOf("entrant-device-0c9d55e1", "entrant-device-5be2f310")
        waitlistCapacity = 50
    }

    /** Already closed for entries. */
    private val closedLottery = Lottery(
        dates = listOf(daysFromNow(10)),
        start = daysFromNow(-40),
        end = daysFromNow(-10),
        campsiteIDs = listOf("listing-two-jack")
    ).apply { id = "lottery-closed" }

    /** Open, but for a different campsite. */
    private val otherCampsiteLottery = Lottery(
        dates = listOf(daysFromNow(30)),
        start = daysFromNow(-5),
        end = daysFromNow(20),
        campsiteIDs = listOf("listing-tunnel-mtn")
    ).apply { id = "lottery-other-campsite" }

    private val firestore = MockFirestore()
    private lateinit var viewModel: UserCampsiteDetailsViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = UserCampsiteDetailsViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack))
        firestore.deliver("Lotteries", Lottery::class.java, listOf(openLottery, closedLottery, otherCampsiteLottery))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun lotteryCards_shouldOnlyShowOpenLotteriesForThisCampsite() {
        viewModel.start("listing-two-jack", profileId = "entrant-device-a81c4e07", searchStart = null, searchEnd = null)

        val cards = viewModel.lotteryCards

        assertEquals(listOf("lottery-open"), cards.map { it.id })
        assertEquals(2, cards.first().waitlistCount)
    }

    @Test
    fun lotteryCards_searchRangeWithoutLotteryDays_shouldShowNothing() {
        viewModel.start("listing-two-jack", profileId = "entrant-device-a81c4e07", searchStart = daysFromNow(60), searchEnd = daysFromNow(65))

        assertTrue(viewModel.lotteryCards.isEmpty())
    }

    @Test
    fun hasCapacity_waitlistNotFull_shouldBeTrue() {
        viewModel.start("listing-two-jack", profileId = "entrant-device-a81c4e07", searchStart = null, searchEnd = null)

        viewModel.selectLottery("lottery-open")

        assertTrue(viewModel.hasCapacity) // 2 of 50 spots taken
    }

    @Test
    fun hasCapacity_waitlistFull_shouldBeFalse() {
        openLottery.waitlistCapacity = 2
        viewModel.start("listing-two-jack", profileId = "entrant-device-a81c4e07", searchStart = null, searchEnd = null)

        viewModel.selectLottery("lottery-open")

        assertFalse(viewModel.hasCapacity)
    }

    @Test
    fun start_differentCampsite_shouldClearSelectedLottery() {
        viewModel.start("listing-two-jack", profileId = "entrant-device-a81c4e07", searchStart = null, searchEnd = null)
        viewModel.selectLottery("lottery-open")

        viewModel.start("listing-tunnel-mtn", profileId = "entrant-device-a81c4e07", searchStart = null, searchEnd = null)

        assertEquals(null, viewModel.selectedLotteryId)
    }

    @Test
    fun joinLottery_shouldAddEntrantToWaitlist() {
        val lotteryDoc = mockk<DocumentReference>(relaxed = true)
        every { firestore.collection("Lotteries").document("lottery-open") } returns lotteryDoc

        viewModel.joinLottery("lottery-open", "entrant-device-a81c4e07", onResult = {})

        verify { lotteryDoc.update("waitlist", any()) }
    }
}
