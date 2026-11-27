/**
 * This file contains the BrowseCampsiteViewModel, it injects UserCampsiteViewModel
 * since a lot of the functionality is the same
 */
package com.example.campsitereservationapp.ui.Admin.BrowseCampsites

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserCampsiteSearchViewModel

/**
 * ViewModel for Browsing Campsite, reuses UserCampsiteSearchViewModel functions
 */
class AdminBrowseCampsiteViewModel(
    val injectedModel: UserCampsiteSearchViewModel =  UserCampsiteSearchViewModel()
): DatabaseModel() {

    //access viewmodel modified variables
    val _searchResults = injectedModel._searchResults
    /**
     * Searches for campsites
     * @param keyword the input text of the search
     */
    fun campsiteSearch(keyword:String){
        injectedModel.doSearch(keyword,"",0)
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
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }



}