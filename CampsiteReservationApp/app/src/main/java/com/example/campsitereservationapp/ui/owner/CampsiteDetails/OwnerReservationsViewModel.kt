/*
 * OwnerReservationsViewModel.kt
 *
 * Role: ViewModel (MVVM) behind OwnerCampsiteDetailsScreen. It extends the shared
 * ReservationsViewModel (which holds the Firestore-backed lists and the selected
 * ReservationFilter) and adds the owner-specific queries the details screen needs:
 * look up one campsite, list/filter its reservations, count its bookings, collect its
 * booked nights, and resolve a guest's display name.
 *
 * Outstanding issues:
 *
 */


package com.example.campsitereservationapp.ui.owner.CampsiteDetails

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ReservationFilter
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ReservationsViewModel
import com.example.campsitereservationapp.utils.endOfDay
import com.example.campsitereservationapp.utils.toDayOnly
import java.util.Date

/**
 * View model for the owner's "Campsite details" screen.
 *
 * Every query is scoped to one campsite (listing) id and reads data already loaded by
 * [ReservationsViewModel] and its Firestore listeners.
 * The selected [ReservationFilter] is inherited from the parent and is
 * changed with 'onFilterSelected', only [getFilteredReservations] depends on it.
 */

class OwnerReservationsViewModel : ReservationsViewModel() {

    /**
     * Finds a campsite by id.
     *
     * @param listingId id of the [Listing] to look up.
     * @return the matching listing, or null if no loaded listing has that id.
     */
    fun getListing(listingId: String): Listing? {
        return _listings.firstOrNull { it.id == listingId }
    }

    /**
     * Returns the reservations of one campsite, narrowed by the currently selected
     * [ReservationFilter].
     *
     * - 'Reserved' (shown as "Active"): status "reserved" and last date after now, or
     *   with no dates at all.
     * - 'Past': status "reserved" and last date before now.
     * - 'Cancelled': status "cancelled", regardless of dates.
     * - any other value (e.g. 'All'): every reservation for the campsite.
     *
     * @param listingId id of the campsite whose reservations are wanted.
     * @return the filtered reservations or empty if there are none.
     */
    fun getFilteredReservations(listingId: String): List<Reservation> {
        val today = Date()
        val all = allReservations.filter { it.campsiteID == listingId }

        return when (filter) {
            ReservationFilter.Reserved -> all.filter {
                val lastDate = it.dates.maxOrNull()
                it.status == "reserved" && (lastDate == null || lastDate.endOfDay().after(today))
            }

            ReservationFilter.Past -> all.filter {
                val lastDate = it.dates.maxOrNull()
                it.status == "reserved" && lastDate != null && lastDate.endOfDay().before(today)
            }

            ReservationFilter.Cancelled -> all.filter {
                it.status == "cancelled"
            }

            else -> all
        }
    }

    /**
     * Collects every night that is booked at a campsite, for drawing on the calendar
     * Cancelled reservations are ignored
     *
     * @param listingId id of the campsite.
     * @return the distinct booked dates or empty if nothing is reserved.
     */

    fun getReservedDates(listingId: String): Set<Date> {
        return allReservations
            .filter { it.campsiteID == listingId && it.status == "reserved" }
            .flatMap { it.dates }
            .map { it.toDayOnly() }
            .toSet()
    }

    /**
     * Resolves the name to show for a guest.
     *
     * @param entrantId id of the guest's profile.
     * @return the profile name, or [entrantId] itself when the profile is unknown or
     *   its name is blank.
     */
    fun getGuestName(entrantId: String): String {
        val name = _profiles.firstOrNull { it.id == entrantId }?.name
        return if (name.isNullOrBlank()) entrantId else name
    }

    /**
     * Counts a campsite's non cancelled reservations (active plus past stays).
     *
     * @param listingId id of the campsite.
     * @return number of reservations with status "reserved" or 0 if none.
     */
    fun getBookedReservationCount(listingId: String): Int {
        return allReservations.count { it.campsiteID == listingId && it.status == "reserved" }
    }
    /**
     * Gets the profile to show for a guest.
     *
     * @param entrantId id of the guest's profile.
     * @return the profile name, or [entrantId] itself when the profile is unknown or
     *   its name is blank.
     */
    fun getGuestProfile(entrantId: String): Profile? =
        _profiles.firstOrNull { it.id == entrantId }
}

