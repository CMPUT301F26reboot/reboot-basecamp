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
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserCampsiteSearchViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Mock tests for [UserCampsiteSearchViewModel], which filters campsites for an entrant's search. */
class UserCampsiteSearchViewModelTest {

    private val twoJack = Listing(
        name = "Two Jack Lakeside",
        address = "Lake Minnewanka Scenic Dr, Banff, AB T1L 1K2",
        maxOccupancy = 6
    ).apply { id = "listing-two-jack" }

    
    private val tunnelMountain = Listing(
        name = "Tunnel Mountain Village II",
        address = "Tunnel Mountain Rd, Banff, AB T1L 1B3",
        maxOccupancy = 4
    ).apply { id = "listing-tunnel-mtn" }

    private val julyLottery = Lottery(
        dates = listOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11)),
        campsiteIDs = listOf("listing-two-jack")
    ).apply { id = "lottery-jul-2027-banff" }

    private val firestore = MockFirestore()
    private lateinit var viewModel: UserCampsiteSearchViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = UserCampsiteSearchViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain))
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun doSearch_noCriteria_shouldReturnEveryCampsite() {
        viewModel.doSearch(keyword = "", location = "", capacity = 0)

        assertEquals(listOf(twoJack, tunnelMountain), viewModel._searchResults)
    }

    @Test
    fun doSearch_keyword_shouldMatchStartOfNameIgnoringCase() {
        viewModel.doSearch(keyword = "two j", location = "", capacity = 0)

        assertEquals(listOf(twoJack), viewModel._searchResults)
    }

    @Test
    fun doSearch_location_shouldMatchAddressIgnoringCase() {
        viewModel.doSearch(keyword = "", location = "tunnel mountain rd, banff, ab t1l 1b3", capacity = 0)

        assertEquals(listOf(tunnelMountain), viewModel._searchResults)
    }

    @Test
    fun doSearch_capacity_shouldHideCampsitesThatAreTooSmall() {
        viewModel.doSearch(keyword = "", location = "", capacity = 5)

        assertEquals(listOf(twoJack), viewModel._searchResults) // Tunnel Mountain only fits 4
    }

    @Test
    fun doSearch_selectedDates_shouldOnlyShowCampsitesOfferingAllOfThem() {
        viewModel.toggleDate(day(2027, 7, 9))
        viewModel.toggleDate(day(2027, 7, 10))

        viewModel.doSearch(keyword = "", location = "", capacity = 0)

        assertEquals(listOf(twoJack), viewModel._searchResults)
    }

    @Test
    fun toggleDate_twice_shouldUnselectTheDate() {
        viewModel.toggleDate(day(2027, 8, 1))
        viewModel.toggleDate(day(2027, 8, 1))

        viewModel.doSearch(keyword = "", location = "", capacity = 0)

        assertEquals(listOf(twoJack, tunnelMountain), viewModel._searchResults) // no date filter left
    }
}
