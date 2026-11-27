/**
 * This file tests AdminBrowseCampsiteViewModel
 *
 * Tests:
 * - An empty search shows every campsite.
 * - A keyword search shows only campsites whose name starts with the keyword, ignoring case.
 * - A successful delete removes the right campsite from Firestore and the search results,
 *   and calls onSuccess.
 * - A failed delete keeps the campsite in the search results and calls onFailure.
 * - A campsite with no ID is never sent to Firestore, and neither callback runs.
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-26_AdminBrowseAndDelete.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.Admin.BrowseCampsites.AdminBrowseCampsiteViewModel
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserCampsiteSearchViewModel
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

/** Mock tests for [AdminBrowseCampsiteViewModel], which lets an admin search and delete campsites. */
class AdminBrowseCampsiteViewModelTest {

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

    private val firestore = MockFirestore()
    private val listingDoc = mockk<DocumentReference>()
    private val deletion = MockTask<Void>()
    private lateinit var viewModel: AdminBrowseCampsiteViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Listings").document(any()) } returns listingDoc
        every { listingDoc.delete() } returns deletion.task

        // Deliver listings before creating the admin ViewModel: MockFirestore keeps only the
        // newest listener per collection, and the search ViewModel is the one that needs them.
        val search = UserCampsiteSearchViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain))
        viewModel = AdminBrowseCampsiteViewModel(search)
        viewModel.campsiteSearch("")
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun campsiteSearch_emptyKeyword_shouldShowEveryCampsite() {
        assertEquals(listOf(twoJack, tunnelMountain), viewModel._searchResults)
    }

    @Test
    fun campsiteSearch_keyword_shouldMatchStartOfNameIgnoringCase() {
        viewModel.campsiteSearch("tunnel")

        assertEquals(listOf(tunnelMountain), viewModel._searchResults)
    }

    @Test
    fun campsiteDelete_success_shouldRemoveCampsiteAndCallOnSuccess() {
        var succeeded = false
        var failed = false

        viewModel.campsiteDelete(twoJack, onSuccess = { succeeded = true }, onFailure = { failed = true })
        deletion.succeed(null)

        verify { firestore.collection("Listings").document("listing-two-jack") }
        assertEquals(listOf(tunnelMountain), viewModel._searchResults)
        assertTrue(succeeded)
        assertFalse(failed)
    }

    @Test
    fun campsiteDelete_failure_shouldKeepCampsiteAndCallOnFailure() {
        var succeeded = false
        var failed = false

        viewModel.campsiteDelete(twoJack, onSuccess = { succeeded = true }, onFailure = { failed = true })
        deletion.fail()

        assertEquals(listOf(twoJack, tunnelMountain), viewModel._searchResults)
        assertFalse(succeeded)
        assertTrue(failed)
    }

    @Test
    fun campsiteDelete_blankId_shouldNotTouchFirestore() {
        var called = false
        val unsaved = Listing(name = "Unsaved Campsite") // id is "" until Firestore assigns one

        viewModel.campsiteDelete(unsaved, onSuccess = { called = true }, onFailure = { called = true })

        verify(exactly = 0) { listingDoc.delete() }
        assertEquals(listOf(twoJack, tunnelMountain), viewModel._searchResults)
        assertFalse(called)
    }
}