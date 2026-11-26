/**
 * This file contains the ListingViewModel, it has a single function that returns the
 * availability of a listing
 */
package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import java.util.Date


/**
 * Simply gets available dates of a listing, has only one method
 */
open class ListingViewModel: DatabaseModel() {
    //Read only list of Listings for screens
    val listings: List<Listing> get() = _listings

    /**
     * Gets available dates of a listing
     *
     * Takes a listing as input and searches all lotteries that contain its id
     * then returns the dates for all such lotteries
     */
    fun getAvailability(listing: Listing): Set<Date>{
        var dates = mutableSetOf<Date>()
        for (lot in _lotteries){
            if(listing.id in lot.campsiteIDs){
                for(date in lot.dates){
                    dates.add(date)
                }
            }
        }
        return dates
    }
}