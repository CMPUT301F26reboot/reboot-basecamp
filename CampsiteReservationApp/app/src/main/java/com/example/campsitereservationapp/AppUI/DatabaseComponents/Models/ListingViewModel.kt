package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import java.util.Date

open class ListingViewModel: DatabaseModel() {
    val listings: List<Listing> get() = _listings

    fun getAvailability(listing: Listing): ArrayList<Date>{
        var dates = ArrayList<Date>()
        lotteriesRef.whereEqualTo("campsiteName", listing.name)
    }
}