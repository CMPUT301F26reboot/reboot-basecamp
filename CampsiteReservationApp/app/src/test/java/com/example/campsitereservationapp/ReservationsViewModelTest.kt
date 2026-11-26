/**
 * Test for ReservationsViewModel, the shared ViewModel the entrant and owner reservation screens
 * extend: the filter, the list of reservations, finding a reservation's campsite, and cancelling.
 *
 * AI use:
 * - Mock tests written using Claude's output, following the shared MockFirestore.kt helper.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ReservationFilter
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ReservationsViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReservationsViewModelTest {

    private val twoJack = Listing(name = "Two Jack Lakeside").apply { id = "listing-two-jack" }

    private val julyStay = Reservation(
        dates = listOf(day(2027, 7, 9), day(2027, 7, 10)),
        entrantID = "device-7f3a91c2",
        campsiteID = "listing-two-jack"
    ).apply { id = "reservation-july" }

    // points at a campsite that has since been deleted
    private val orphanStay = Reservation(
        dates = listOf(day(2027, 8, 1)),
        entrantID = "device-7f3a91c2",
        campsiteID = "listing-deleted"
    ).apply { id = "reservation-orphan" }

    private val firestore = MockFirestore()
    private val reservationDoc = mockk<DocumentReference>(relaxed = true)
    private val cancelWrite = MockTask<Void>()
    private lateinit var viewModel: ReservationsViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Reservations").document("reservation-july") } returns reservationDoc
        every { reservationDoc.update("status", "cancelled") } returns cancelWrite.task
        viewModel = ReservationsViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack))
        firestore.deliver("Reservations", Reservation::class.java, listOf(julyStay, orphanStay))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    // ---------- filter ----------

    @Test
    fun filter_shouldStartOnAll() {
        assertEquals(ReservationFilter.All, viewModel.filter)
    }

    @Test
    fun onFilterSelected_shouldChangeFilter() {
        viewModel.onFilterSelected(ReservationFilter.Cancelled)

        assertEquals(ReservationFilter.Cancelled, viewModel.filter)
    }

    // ---------- allReservations ----------

    @Test
    fun allReservations_shouldReturnEveryReservationFromFirestore() {
        assertEquals(listOf(julyStay, orphanStay), viewModel.allReservations)
    }

    @Test
    fun allReservations_shouldUpdateWhenFirestoreChanges() {
        firestore.deliver("Reservations", Reservation::class.java, listOf(julyStay))

        assertEquals(listOf(julyStay), viewModel.allReservations)
    }

    // ---------- listingFor ----------

    @Test
    fun listingFor_shouldReturnTheReservedCampsite() {
        assertEquals(twoJack, viewModel.listingFor(julyStay))
    }

    @Test
    fun listingFor_deletedCampsite_shouldReturnNull() {
        assertNull(viewModel.listingFor(orphanStay))
    }

    // ---------- cancelReservation ----------

    @Test
    fun cancelReservation_shouldMarkReservationCancelled() {
        viewModel.cancelReservation("reservation-july")

        verify { reservationDoc.update("status", "cancelled") }
    }

    @Test
    fun cancelReservation_whileSaving_shouldBeCancelling() {
        viewModel.cancelReservation("reservation-july")

        assertTrue(viewModel.isCancelling)
    }

    @Test
    fun cancelReservation_doubleTap_shouldOnlyCancelOnce() {
        viewModel.cancelReservation("reservation-july")
        viewModel.cancelReservation("reservation-july")

        verify(exactly = 1) { reservationDoc.update("status", "cancelled") }
    }

    @Test
    fun cancelReservation_saveSucceeds_shouldStopCancelling() {
        viewModel.cancelReservation("reservation-july")

        cancelWrite.succeed(null)

        assertFalse(viewModel.isCancelling)
    }

    @Test
    fun cancelReservation_saveFails_shouldStopCancellingSoUserCanRetry() {
        viewModel.cancelReservation("reservation-july")

        cancelWrite.fail()

        assertFalse(viewModel.isCancelling)
        viewModel.cancelReservation("reservation-july")
        verify(exactly = 2) { reservationDoc.update("status", "cancelled") }
    }
}
