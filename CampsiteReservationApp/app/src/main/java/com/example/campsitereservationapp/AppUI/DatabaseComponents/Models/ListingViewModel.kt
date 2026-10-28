package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import java.util.Date

open class ListingViewModel: DatabaseModel() {
    //Read only list of Listings for screens
    override val listings: List<Listing> get() = _listings

    /**
     * Gets available dates of a listing
     *
     * Takes a listing as input and searches all lotteries that contain its name
     * then returns the dates for all such lotteries
     */
    fun getAvailability(listing: Listing): ArrayList<Date>{
        var dates = ArrayList<Date>()
        for (lot in _lotteries){
            if(listing.name in lot.campsiteNames){
                for (d in lot.dates){
                    dates.add(d)
                }
            }
        }
        return dates
    }
}