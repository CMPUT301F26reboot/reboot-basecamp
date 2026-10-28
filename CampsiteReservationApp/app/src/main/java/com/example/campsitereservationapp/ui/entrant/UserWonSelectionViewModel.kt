package com.example.campsitereservationapp.ui.entrant

import androidx.lifecycle.ViewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel



data class CampsiteInfo(
    val title: String,
    val address: String,
    val occupancy: Int,
    val rating: Double,
    val numOfReviews: Int,
    val availability: String,
    val image: String?

)

/** Converts a database [Listing] document into a UI [CampsiteInfo] model. */
fun Listing.toCampsiteInfo(): CampsiteInfo {
    return CampsiteInfo(
        title = name,
        address = address,
        occupancy = maxOccupancy,
        rating = rating,
        numOfReviews = 0,
        availability = availability,
        image = photoUrls.firstOrNull()
    )
}

class UserWonSelectionViewModel(
    val repository: DatabaseModel = DatabaseModel()
) : ViewModel() {


    /**
     * Mutable list storing [CampsiteInfo] extracted from [DatabaseModel]'s "Listings" collection,
     * falling back to sample data if database listings are empty.
     */
    var CampsiteListings: MutableList<CampsiteInfo>? = if (repository.listings.isNotEmpty()) {
        repository.listings.map { it.toCampsiteInfo() }.toMutableList()
    } else {
        null
    }


    /** Extracts updated listings from [DatabaseModel] into [CampsiteListings]. */
    //ran as soon as screen is rendered
    fun extractListingsFromDatabase() {
        CampsiteListings = if (repository.listings.isNotEmpty()) {
            repository.listings.map { it.toCampsiteInfo() }.toMutableList()
        } else {
            null
        }
    }
}
