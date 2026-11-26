/**
 * Test for ListingViewModel,
 * Creates Listings of dates, add to a lottery, get listing dates and compare if correct
 *
 * AI Refrence: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ListingViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ListingViewModelTest {

    private val twoJack = Listing(name = "Two Jack Lakeside").apply { id = "listing-two-jack" }
    private val tunnelMountain = Listing(name = "Tunnel Mountain Village II").apply { id = "listing-tunnel-mtn" }

    // July lottery: both campsites
    private val julyLottery = Lottery(
        dates = listOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11)),
        campsiteIDs = listOf("listing-two-jack", "listing-tunnel-mtn")
    ).apply { id = "lottery-july" }

    // August lottery: Two Jack only, shares 0 dates with July
    private val augustLottery = Lottery(
        dates = listOf(day(2027, 8, 1), day(2027, 8, 2)),
        campsiteIDs = listOf("listing-two-jack")
    ).apply { id = "lottery-august" }

    // Overlap lottery: Two Jack only, repeats July 10 to check duplicates are removed
    private val overlapLottery = Lottery(
        dates = listOf(day(2027, 7, 10)),
        campsiteIDs = listOf("listing-two-jack")
    ).apply { id = "lottery-overlap" }

    private val firestore = MockFirestore()
    private lateinit var viewModel: ListingViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = ListingViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun getAvailability_returnsDatesOfEveryLotteryWithTheListing() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery, augustLottery))

        val expected = setOf(
            day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11),
            day(2027, 8, 1), day(2027, 8, 2)
        )
        assertEquals(expected, viewModel.getAvailability(twoJack))
    }

    @Test
    fun getAvailability_skipsLotteriesWithoutTheListing() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery, augustLottery))

        // Tunnel Mountain is only in the July lottery
        val expected = setOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11))
        assertEquals(expected, viewModel.getAvailability(tunnelMountain))
    }

    @Test
    fun getAvailability_removesDuplicateDates() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery, overlapLottery))

        // July 10 is in both lotteries but appears once
        val expected = setOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11))
        assertEquals(expected, viewModel.getAvailability(twoJack))
    }

    @Test
    fun getAvailability_noLotteries_returnsEmpty() {
        firestore.deliver("Lotteries", Lottery::class.java, emptyList())

        assertTrue(viewModel.getAvailability(twoJack).isEmpty())
    }

    @Test
    fun listings_returnsDeliveredListings() {
        assertEquals(listOf(twoJack, tunnelMountain), viewModel.listings)
    }
}