/*
 * AI use:
 * - Q: How do I test ViewModels that connect to Firebase when they're created?
 *   A: Plain JUnit for the models; MockK to mock Firebase.firestore for the ViewModels.
 * - Some of the mock tests were written using Claude's output: the shared MockFirestore.kt helper
 *   and the ViewModel tests that use it.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.entrant.WonSelection.UserWonSelectionViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Locale

/**
 * Mock tests for [UserWonSelectionViewModel], which shows the entrant the lottery they won and
 * the campsites they can pick from.
 */
class UserWonSelectionViewModelTest {

    private val twoJack = Listing(name = "Two Jack Lakeside").apply { id = "listing-two-jack" }
    private val tunnelMountain = Listing(name = "Tunnel Mountain Village II").apply { id = "listing-tunnel-mtn" }
    private val lakeLouise = Listing(name = "Lake Louise Hard-Sided").apply { id = "listing-lake-louise" }

    private val julyLottery = Lottery(
        dates = listOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11)),
        start = day(2027, 7, 9),
        end = day(2027, 7, 11),
        campsiteIDs = listOf("listing-two-jack", "listing-tunnel-mtn")
    ).apply { id = "lottery-jul-2027-banff" }

    private val firestore = MockFirestore()
    private val originalLocale = Locale.getDefault()
    private lateinit var viewModel: UserWonSelectionViewModel

    @Before
    fun setUp() {
        Locale.setDefault(Locale.CANADA) // month names are formatted in the device's language
        firestore.install()
        viewModel = UserWonSelectionViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain, lakeLouise))
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
        Locale.setDefault(originalLocale)
    }

    @Test
    fun getLotteryDateAndListings_shouldReturnDatesAndOnlyThatLotterysCampsites() {
        val (dates, listings) = viewModel.getLotteryDateAndListings("lottery-jul-2027-banff")!!

        assertEquals("Jul 9-11, 2027", dates)
        assertEquals(listOf(twoJack, tunnelMountain), listings)
    }

    @Test
    fun getLotteryDateAndListings_unknownLottery_shouldReturnNull() {
        assertNull(viewModel.getLotteryDateAndListings("lottery-that-was-deleted"))
    }

    @Test
    fun lotteryDatesToString_acrossMonths_shouldShowBothMonths() {
        val lottery = Lottery(start = day(2027, 6, 28), end = day(2027, 7, 2))

        assertEquals("Jun 28 - Jul 2, 2027", viewModel.lotteryDatesToString(lottery))
    }

    @Test
    fun lotteryDatesToString_acrossYears_shouldShowBothYears() {
        val lottery = Lottery(start = day(2027, 12, 30), end = day(2028, 1, 2))

        assertEquals("Dec 30, 2027 - Jan 2, 2028", viewModel.lotteryDatesToString(lottery))
    }

    @Test
    fun lotteryDatesToString_singleDay_shouldShowOneDate() {
        val lottery = Lottery(start = day(2027, 7, 9), end = day(2027, 7, 9))

        assertEquals("Jul 9, 2027", viewModel.lotteryDatesToString(lottery))
    }
}
