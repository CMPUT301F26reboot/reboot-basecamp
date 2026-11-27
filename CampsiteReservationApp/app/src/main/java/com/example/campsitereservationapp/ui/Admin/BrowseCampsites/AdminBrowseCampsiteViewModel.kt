/**
 * This file contains the BrowseCampsiteViewModel, which lets an admin browse campsites.
 */
package com.example.campsitereservationapp.ui.Admin.BrowseCampsites

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserCampsiteSearchViewModel
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.startsAfterToday
import com.google.firebase.firestore.FieldValue
import java.util.Date

/**
 * ViewModel for Browsing Campsite, reuses UserCampsiteSearchViewModel functions
 */
class AdminBrowseCampsiteViewModel(
    val injectedModel: UserCampsiteSearchViewModel =  UserCampsiteSearchViewModel()
): DatabaseModel() {

    //access viewmodel modified variables
    val _searchResults = injectedModel._searchResults

    private var lastKeyword = ""
    val listingCount: Int
        get() = _listings.size

    /**
     * Searches for campsites
     * @param keyword the input text of the search
     */
    fun campsiteSearch(keyword:String){
        lastKeyword = keyword
        injectedModel.doSearch(keyword,"",0)
    }

    /** Runs the last search again, e.g. once campsites have loaded from Firestore. */
    fun refreshSearch() {
        campsiteSearch(lastKeyword)
    }

    /**
     * Deletes campsite
     * @param listing what listing is being deleted
     * @param onSuccess action if deletion successful
     * @param onFailure action if deletion not successful
     */
    fun campsiteDelete(
        listing: Listing,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (listing.id.isBlank()) return

        listingsRef.document(listing.id).delete()
            .addOnSuccessListener {
                _searchResults.remove(listing)
                removeFromLotteries(listing.id)
                cancelFutureReservations(listing)
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    /**
     * Cancels the deleted campsite's reservations that start after today and tells each entrant.
     * Stays happening now are kept so the guests can finish them, and finished stays are left
     * as history.
     *
     * @param listing the deleted campsite.
     * @param now the current time.
     */
    internal fun cancelFutureReservations(listing: Listing, now: Date = Date()) {
        _reservations
            .filter { it.campsiteID == listing.id && startsAfterToday(it, now) }
            .forEach { reservation ->
                reservationsRef.document(reservation.id).update("status", "cancelled")
                notificationsRef.add(
                    mapOf(
                        "lotteryId" to "",
                        "message" to "Your reservation at ${listing.name} was cancelled because " +
                                "the campsite was removed by an administrator.",
                        "recipientIds" to listOf(reservation.entrantID),
                        "reservationId" to reservation.id,
                        "senderId" to "",
                        "subject" to "Reservation Cancelled",
                        "timestamp" to now,
                        "type" to "reserveCancel",
                        "viewedBy" to emptyList<String>()
                    )
                )
            }
    }

    /**
     * Takes a deleted campsite out of every lottery it was in, so lotteries don't point at a
     * campsite that no longer exists.
     * @param listingId the deleted campsite.
     */
    private fun removeFromLotteries(listingId: String) {
        _lotteries.filter { listingId in it.campsiteIDs }.forEach { lottery ->
            lotteriesRef.document(lottery.id).update("campsiteIDs", FieldValue.arrayRemove(listingId))
        }
    }



}