package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ReservationFilter
import com.example.campsitereservationapp.ui.owner.CampsiteDetails.OwnerReservationsViewModel
import com.example.campsitereservationapp.utils.toDayOnly
import java.util.Date
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/** Mock tests for [OwnerReservationsViewModel] functions such as: finding a campsite,
 * filtering its reservations, collecting its reserved nights, counting bookings,
 * and naming guests
 */

// mainly based off of UserReservationsViewModelTest
class OwnerReservationsViewModelTest {

    private val campsiteId = "listing-two-jack"
    private val entrantId = "entrant-device-a81c4e07"

    private val twoJack = Listing(name = "Two Jack Lakeside", rating = 4.7, numOfReviews = 23)
        .apply { id = "listing-two-jack" }

    private val guest = Profile(name = "Sam Rivers")
        .apply { id = "entrant-device-a81c4e07" }

    private val upcoming = Reservation(listOf(daysFromNow(30), daysFromNow(31)), entrantId, "listing-two-jack")
        .apply { id = "reservation-upcoming" }
    private val endsToday = Reservation(listOf(daysFromNow(0).toDayOnly()), entrantId, "listing-two-jack")
        .apply { id = "reservation-ends-today" }
    private val past = Reservation(listOf(daysFromNow(-60)), entrantId, "listing-two-jack")
        .apply { id = "reservation-past" }
    private val cancelled = Reservation(listOf(daysFromNow(10)), entrantId, "listing-two-jack", status = "cancelled")
        .apply { id = "reservation-cancelled" }
    private val otherCampsite = Reservation(listOf(daysFromNow(20)), entrantId, "listing-other")
        .apply { id = "reservation-other-campsite" }

    private val noDates = Reservation(emptyList(), entrantId, "listing-two-jack")
        .apply { id = "reservation-no-dates" }

    private val firestore = MockFirestore()
    private lateinit var viewModel: OwnerReservationsViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = OwnerReservationsViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack))
        firestore.deliver("Profiles", Profile::class.java, listOf(guest))
        firestore.deliver("Reservations", Reservation::class.java, listOf(upcoming, endsToday, past, cancelled, otherCampsite, noDates))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }
    // test getListing
    @Test
    fun getListing_shouldReturnTheMatchingCampsite() {
        assertEquals(twoJack, viewModel.getListing(campsiteId))
    }

    @Test
    fun getListing_unknownId_shouldReturnNull() {
        assertNull(viewModel.getListing("listing-missing"))
    }
    // test getFilteredReservations
    @Test
    fun getFilteredReservations_all_shouldShowEveryReservationForTheCampsite() {
        val result = viewModel.getFilteredReservations(campsiteId)

        assertEquals(listOf(upcoming, endsToday, past, cancelled, noDates), result) // other campsites' are hidden
    }

    @Test
    fun getFilteredReservations_reserved_shouldShowUpcomingEndingTodayAndUndatedStays() {
        viewModel.onFilterSelected(ReservationFilter.Reserved)

        assertEquals(listOf(upcoming, endsToday, noDates), viewModel.getFilteredReservations(campsiteId))
    }

    @Test
    fun getFilteredReservations_past_shouldShowOnlyFinishedStays() {
        viewModel.onFilterSelected(ReservationFilter.Past)

        assertEquals(listOf(past), viewModel.getFilteredReservations(campsiteId))
    }

    @Test
    fun getFilteredReservations_cancelled_shouldShowOnlyCancelledStays() {
        viewModel.onFilterSelected(ReservationFilter.Cancelled)

        assertEquals(listOf(cancelled), viewModel.getFilteredReservations(campsiteId))
    }
    // test getReservedDates
    @Test
    fun getReservedDates_shouldSkipCancelledAndOtherCampsites() {
        val expected = (upcoming.dates + endsToday.dates + past.dates).map { it.toDayOnly() }.toSet()

        assertEquals(expected, viewModel.getReservedDates(campsiteId))
    }

    @Test
    fun getReservedDates_sameDayAtDifferentTimes_shouldOnlyCountOnce() {
        val midnight = day(2026, 12, 1)
        val evening = Date(midnight.time + 18 * 60 * 60 * 1000L)
        val twice = Reservation(listOf(midnight, evening), entrantId, "listing-two-jack")
            .apply { id = "reservation-twice" }
        firestore.deliver("Reservations", Reservation::class.java, listOf(twice))

        assertEquals(setOf(midnight), viewModel.getReservedDates(campsiteId))
    }

    @Test
    fun getReservedDates_noReservations_shouldBeEmpty() {
        firestore.deliver("Reservations", Reservation::class.java, emptyList())

        assertEquals(emptySet<Date>(), viewModel.getReservedDates(campsiteId))
    }
    // test getBookedReservationCount
    @Test
    fun getBookedReservationCount_shouldNotCountCancelledStays() {
        assertEquals(4, viewModel.getBookedReservationCount(campsiteId)) // upcoming, ends today, past, no dates
    }

    @Test
    fun getBookedReservationCount_unknownCampsite_shouldBeZero() {
        assertEquals(0, viewModel.getBookedReservationCount("listing-missing"))
    }
    // test getGuestName
    @Test
    fun getGuestName_shouldReturnTheProfileName() {
        assertEquals("Sam Rivers", viewModel.getGuestName(entrantId))
    }

    @Test
    fun getGuestName_unknownProfile_shouldReturnTheId() {
        assertEquals("entrant-unknown", viewModel.getGuestName("entrant-unknown"))
    }

    @Test
    fun getGuestName_blankName_shouldReturnTheId() {
        val noName = Profile(name = "").apply { id = "entrant-no-name" }
        firestore.deliver("Profiles", Profile::class.java, listOf(noName))

        assertEquals("entrant-no-name", viewModel.getGuestName("entrant-no-name"))
    }
}