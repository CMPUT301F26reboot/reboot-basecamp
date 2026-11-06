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
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.ui.entrant.ReservationDetails.ReservationFilter
import com.example.campsitereservationapp.ui.entrant.Reservations.UserReservationsViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Mock tests for [UserReservationsViewModel] and the [com.example.campsitereservationapp.ui.entrant.ReservationDetails.ReservationsViewModel]
 * it extends: listing an entrant's reservations (US 01.06.01), cancelling (US 01.07.01) and rating a stay.
 */
class UserReservationsViewModelTest {

    private val entrantId = "entrant-device-a81c4e07"

    private val twoJack = Listing(name = "Two Jack Lakeside", rating = 4.7, numOfReviews = 23)
        .apply { id = "listing-two-jack" }

    private val upcoming = Reservation(listOf(daysFromNow(30), daysFromNow(31)), entrantId, "listing-two-jack")
        .apply { id = "reservation-upcoming" }
    private val past = Reservation(listOf(daysFromNow(-60)), entrantId, "listing-two-jack")
        .apply { id = "reservation-past" }
    private val cancelled = Reservation(listOf(daysFromNow(10)), entrantId, "listing-two-jack", status = "cancelled")
        .apply { id = "reservation-cancelled" }
    private val someoneElses = Reservation(listOf(daysFromNow(30)), "entrant-device-0c9d55e1", "listing-two-jack")
        .apply { id = "reservation-other-entrant" }

    private val firestore = MockFirestore()
    private lateinit var viewModel: UserReservationsViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = UserReservationsViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack))
        firestore.deliver("Reservations", Reservation::class.java, listOf(upcoming, past, cancelled, someoneElses))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    /** Mocks the reservation document [id] so the test can check what is written to it. */
    private fun reservationDoc(id: String): DocumentReference {
        val doc = mockk<DocumentReference>(relaxed = true)
        every { firestore.collection("Reservations").document(id) } returns doc
        return doc
    }

    @Test
    fun filteredReservations_all_shouldShowOnlyThisEntrantsActiveReservations() {
        val result = viewModel.filteredReservations(entrantId)

        assertEquals(listOf(upcoming, past), result) // cancelled and other entrants' are hidden
    }

    @Test
    fun filteredReservations_reserved_shouldShowOnlyUpcomingStays() {
        viewModel.onFilterSelected(ReservationFilter.Reserved)

        assertEquals(listOf(upcoming), viewModel.filteredReservations(entrantId))
    }

    @Test
    fun filteredReservations_past_shouldShowOnlyFinishedStays() {
        viewModel.onFilterSelected(ReservationFilter.Past)

        assertEquals(listOf(past), viewModel.filteredReservations(entrantId))
    }

    @Test
    fun listingFor_shouldReturnTheReservedCampsite() {
        assertEquals(twoJack, viewModel.listingFor(upcoming))
    }

    @Test
    fun confirmCancel_shouldMarkReservationCancelledAndCloseDialog() {
        val doc = reservationDoc("reservation-upcoming")
        viewModel.onCancelClicked("reservation-upcoming")

        viewModel.confirmCancel()

        verify { doc.update("status", "cancelled") }
        assertNull(viewModel.pendingCancelId)
    }

    @Test
    fun dismissCancelDialog_shouldNotCancelAnything() {
        val doc = reservationDoc("reservation-upcoming")
        viewModel.onCancelClicked("reservation-upcoming")

        viewModel.dismissCancelDialog()
        viewModel.confirmCancel()

        verify(exactly = 0) { doc.update(any<String>(), any()) }
    }

    @Test
    fun cancelReservation_doubleTap_shouldOnlyCancelOnce() {
        val doc = reservationDoc("reservation-upcoming")

        viewModel.cancelReservation("reservation-upcoming")
        viewModel.cancelReservation("reservation-upcoming")

        verify(exactly = 1) { doc.update("status", "cancelled") }
    }

    @Test
    fun submitReview_shouldMarkRatedThenUpdateCampsiteAverage() {
        val markRated = MockTask<Void>()
        val reservation = reservationDoc("reservation-past")
        every { reservation.update("rated", true) } returns markRated.task
        val listingDoc = mockk<DocumentReference>(relaxed = true)
        every { firestore.collection("Listings").document("listing-two-jack") } returns listingDoc
        viewModel.onLeaveReviewClicked("reservation-past")

        viewModel.submitReview(5)
        markRated.succeed(null)

        // (4.7 * 23 + 5) / 24 = 4.7125, rounded to one decimal place = 4.7
        verify { listingDoc.update("rating", 4.7, "numOfReviews", 24) }
    }
}
