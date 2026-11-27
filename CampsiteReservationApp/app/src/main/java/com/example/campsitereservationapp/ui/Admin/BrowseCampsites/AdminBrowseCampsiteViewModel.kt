/**
 * This file contains the BrowseCampsiteViewModel, it injects UserCampsiteViewModel
 * since a lot of the functionality is the same
 */
package com.example.campsitereservationapp.ui.Admin.BrowseCampsites

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserCampsiteSearchViewModel
import com.google.firebase.firestore.FieldValue

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
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    /**
     * Takes a deleted campsite out of every lottery it was in, so lotteries don't point at a
     * campsite that no longer exists. Reservations on it are left as they are.
     * @param listingId the deleted campsite.
     */
    private fun removeFromLotteries(listingId: String) {
        _lotteries.filter { listingId in it.campsiteIDs }.forEach { lottery ->
            lotteriesRef.document(lottery.id).update("campsiteIDs", FieldValue.arrayRemove(listingId))
        }
    }



}