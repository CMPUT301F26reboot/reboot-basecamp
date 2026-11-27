/**
 * This file tests AdminBrowseCampsiteViewModel
 *
 * Tests:
 * - A search shows every campsite for an empty keyword, and only campsites whose name starts
 *   with the keyword otherwise, ignoring case.
 * - Refreshing the search runs the last keyword again.
 * - The listing count is the number of campsites loaded from Firestore.
 * - A successful delete removes the right campsite from Firestore and the search results,
 *   and calls onSuccess.
 * - A failed delete keeps the campsite in the search results and calls onFailure.
 * - A campsite with no ID is never sent to Firestore, and neither callback runs.
 * - Deleting a campsite removes it from lotteries that have other campsites, leaves other
 *   lotteries alone, and tells the owner the listing was deleted.
 * - Deleting a lottery's only campsite deletes the lottery and tells the owner.
 * - Deleting a campsite cancels only reservations that start after today, and tells those
 *   entrants. Stays happening now and finished stays are left alone.
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-26_AdminBrowseAndDelete.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.ui.Admin.BrowseCampsites.AdminBrowseCampsiteViewModel
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserCampsiteSearchViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Mock tests for [AdminBrowseCampsiteViewModel], which lets an admin search and delete campsites. */
class AdminBrowseCampsiteViewModelTest {

    private val twoJack = Listing(name = "Two Jack Lakeside", ownerID = "owner-alex").apply { id = "listing-two-jack" }
    private val tunnelMountain = Listing(name = "Tunnel Mountain Village II").apply { id = "listing-tunnel-mtn" }

    private val firestore = MockFirestore()
    private val listingDoc = mockk<DocumentReference>()
    private val deletion = MockTask<Void>()
    private val callbacks = mutableListOf<String>() // "success" or "failure", in the order they ran
    private lateinit var viewModel: AdminBrowseCampsiteViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Listings").document(any()) } returns listingDoc
        every { listingDoc.delete() } returns deletion.task

        // MockFirestore keeps only the newest listener per collection, so the listings go to the
        // search ViewModel first (for searching), then to the admin ViewModel (for counting and
        // looking up the owner).
        val search = UserCampsiteSearchViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain))
        viewModel = AdminBrowseCampsiteViewModel(search)
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain))
        viewModel.campsiteSearch("")
    }

    @After
    fun tearDown() = firestore.uninstall()

    /** Deletes [listing], recording which callback runs in [callbacks]. */
    private fun delete(listing: Listing) =
        viewModel.campsiteDelete(listing, onSuccess = { callbacks += "success" }, onFailure = { callbacks += "failure" })

    /** A mocked document with this [id] in [collection]. */
    private fun doc(collection: String, id: String) = mockk<DocumentReference>(relaxed = true).also {
        every { firestore.collection(collection).document(id) } returns it
    }

    /**
     * True when [data] is a notification with this [subject], sent only to [recipient]. This is
     * kept outside verify { }, where MockK's own get operator would replace the map's.
     */
    private fun isNotification(data: Any, subject: String, recipient: String): Boolean {
        val notification = data as? Map<*, *> ?: return false
        return notification["subject"] == subject && notification["recipientIds"] == listOf(recipient)
    }

    /** Checks that exactly one notification was sent, with this [subject], to [recipient]. */
    private fun verifyOnlyNotification(subject: String, recipient: String) {
        val notifications = firestore.collection("Notifications")
        verify(exactly = 1) { notifications.add(any()) }
        verify { notifications.add(match { isNotification(it, subject, recipient) }) }
    }

    @Test
    fun campsiteSearch_shouldMatchStartOfNameIgnoringCase() {
        assertEquals(listOf(twoJack, tunnelMountain), viewModel._searchResults) // empty keyword
        viewModel.campsiteSearch("tunnel")
        assertEquals(listOf(tunnelMountain), viewModel._searchResults)
    }

    @Test
    fun refreshSearch_shouldRunLastKeywordAgain() {
        viewModel.campsiteSearch("tunnel")
        viewModel._searchResults.clear()
        viewModel.refreshSearch()
        assertEquals(listOf(tunnelMountain), viewModel._searchResults)
    }

    @Test
    fun listingCount_shouldCountLoadedCampsites() = assertEquals(2, viewModel.listingCount)

    @Test
    fun campsiteDelete_success_shouldRemoveCampsiteAndCallOnSuccess() {
        delete(twoJack)
        deletion.succeed(null)
        verify { firestore.collection("Listings").document("listing-two-jack") }
        assertEquals(listOf(tunnelMountain), viewModel._searchResults)
        assertEquals(listOf("success"), callbacks)
    }

    @Test
    fun campsiteDelete_failure_shouldKeepCampsiteAndCallOnFailure() {
        delete(twoJack)
        deletion.fail()
        assertEquals(listOf(twoJack, tunnelMountain), viewModel._searchResults)
        assertEquals(listOf("failure"), callbacks)
    }

    @Test
    fun campsiteDelete_blankId_shouldNotTouchFirestore() {
        delete(Listing(name = "Unsaved Campsite")) // id is "" until Firestore assigns one
        verify(exactly = 0) { listingDoc.delete() }
        assertEquals(listOf(twoJack, tunnelMountain), viewModel._searchResults)
        assertEquals(emptyList<String>(), callbacks)
    }

    @Test
    fun campsiteDelete_sharedLottery_shouldRemoveCampsiteAndTellOwner() {
        val shared = doc("Lotteries", "lottery-banff")
        firestore.deliver("Lotteries", Lottery::class.java, listOf(
            Lottery(campsiteIDs = listOf("listing-two-jack", "listing-tunnel-mtn")).apply { id = "lottery-banff" },
            Lottery(campsiteIDs = listOf("listing-tunnel-mtn")).apply { id = "lottery-other" },
        ))
        delete(twoJack)
        deletion.succeed(null)
        verify { shared.update("campsiteIDs", any()) }
        verify(exactly = 0) { shared.delete() } // the lottery still has Tunnel Mountain
        verify(exactly = 0) { firestore.collection("Lotteries").document("lottery-other") }
        verifyOnlyNotification("Listing Deleted", "owner-alex")
    }

    @Test
    fun campsiteDelete_onlyCampsiteOfLottery_shouldDeleteLotteryAndTellOwner() {
        val lottery = doc("Lotteries", "lottery-two-jack")
        firestore.deliver("Lotteries", Lottery::class.java, listOf(
            Lottery(campsiteIDs = listOf("listing-two-jack")).apply { id = "lottery-two-jack" },
        ))
        delete(twoJack)
        deletion.succeed(null)
        verify { lottery.delete() }
        verify(exactly = 0) { lottery.update(any<String>(), any()) }
        verifyOnlyNotification("Lottery Deleted", "owner-alex")
    }

    @Test
    fun campsiteDelete_shouldCancelOnlyFutureReservationsAndTellThoseEntrants() {
        val future = doc("Reservations", "res-future")
        val ongoing = doc("Reservations", "res-ongoing")
        val past = doc("Reservations", "res-past")
        firestore.deliver("Reservations", Reservation::class.java, listOf(
            Reservation(listOf(daysFromNow(10), daysFromNow(11)), "entrant-a", "listing-two-jack").apply { id = "res-future" },
            Reservation(listOf(daysFromNow(-1), daysFromNow(1)), "entrant-b", "listing-two-jack").apply { id = "res-ongoing" },
            Reservation(listOf(daysFromNow(-30)), "entrant-c", "listing-two-jack").apply { id = "res-past" },
        ))
        delete(twoJack)
        deletion.succeed(null)
        verify { future.update("status", "cancelled") }
        verify(exactly = 0) { ongoing.update(any<String>(), any()) } // still camping: keeps the stay
        verify(exactly = 0) { past.update(any<String>(), any()) }
        verifyOnlyNotification("Reservation Cancelled", "entrant-a")
    }
}