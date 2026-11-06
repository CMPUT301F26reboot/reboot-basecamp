/*
 * AI use (full log: "AI Use Logs - Jeel/AI-Use-Log_Jeel.md"):
  * -This mock tests were written using Claude's output.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.entrant.CurrentLotteries.UserCurrentLotteriesViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Mock tests for [UserCurrentLotteriesViewModel], which shows the lotteries an entrant has joined
 * and the ones they've won, and lets them leave a waitlist.
 */
class UserCurrentLotteriesViewModelTest {

    private val entrantId = "entrant-device-a81c4e07"

    private val twoJack = Listing(name = "Two Jack Lakeside", photoUrls = listOf("/9j/4AAQSkZJRgABAQ-twojack"))
        .apply { id = "listing-two-jack" }
    private val tunnelMountain = Listing(name = "Tunnel Mountain Village II", photoUrls = listOf("/9j/4AAQSkZJRgABAQ-tunnel"))
        .apply { id = "listing-tunnel-mtn" }

    /** The entrant is on this waitlist. */
    private val julyLottery = Lottery(campsiteIDs = listOf("listing-tunnel-mtn")).apply {
        id = "lottery-jul-2027-banff"
        waitlist = listOf("entrant-device-0c9d55e1", entrantId)
    }

    /** The entrant was selected in this one. */
    private val augustLottery = Lottery(campsiteIDs = listOf("listing-two-jack")).apply {
        id = "lottery-aug-2027-banff"
        selectedlist = listOf(entrantId)
    }

    /** The entrant isn't in this one at all. */
    private val septemberLottery = Lottery(campsiteIDs = listOf("listing-two-jack")).apply {
        id = "lottery-sep-2027-banff"
        waitlist = listOf("entrant-device-5be2f310")
    }

    private val firestore = MockFirestore()
    private lateinit var viewModel: UserCurrentLotteriesViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = UserCurrentLotteriesViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain))
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery, augustLottery, septemberLottery))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun start_shouldSplitLotteriesIntoOngoingAndWon() {
        viewModel.start(entrantId)

        assertEquals(listOf(julyLottery), viewModel.ongoingLotteries)
        assertEquals(listOf(augustLottery), viewModel.wonLotteries)
    }

    @Test
    fun start_calledAgain_shouldNotDuplicateLotteries() {
        viewModel.start(entrantId)

        viewModel.start(entrantId)

        assertEquals(1, viewModel.ongoingLotteries.size)
        assertEquals(1, viewModel.wonLotteries.size)
    }

    @Test
    fun getLotteryImage_shouldUseThePhotoOfACampsiteInThatLottery() {
        val image = viewModel.getLotteryImage(julyLottery)

        assertEquals("/9j/4AAQSkZJRgABAQ-tunnel", image) // Two Jack isn't in the July lottery
    }

    @Test
    fun getLotteryImage_noCampsiteHasPhotos_shouldReturnEmpty() {
        val noPhotos = Listing(name = "Two Jack Lakeside", photoUrls = emptyList()).apply { id = "listing-two-jack" }
        firestore.deliver("Listings", Listing::class.java, listOf(noPhotos))

        val image = viewModel.getLotteryImage(augustLottery)

        assertEquals("", image) // used to crash with NoSuchElementException
    }

    @Test
    fun leaveLottery_shouldRemoveEntrantFromWaitlist() {
        val lotteryDoc = mockk<DocumentReference>(relaxed = true)
        every { firestore.collection("Lotteries").document("lottery-jul-2027-banff") } returns lotteryDoc

        viewModel.leaveLottery(julyLottery, entrantId)

        verify { lotteryDoc.update("waitlist", any()) }
    }
}
