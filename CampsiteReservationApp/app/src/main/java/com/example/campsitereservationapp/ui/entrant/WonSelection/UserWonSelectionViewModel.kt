package com.example.campsitereservationapp.ui.entrant.WonSelection

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * ViewModel of UserWonSelectionScreen. Accesses database and converts data into proper format used
 * in Screen
 */
class UserWonSelectionViewModel(
) : DatabaseModel() {


    /** Takes in lottery ID and returns lottery dates and lottery campsite information */
    fun getLotteryDateAndListings(lotteryID: String): Pair<String, MutableList<Listing>>? {
        val lottery = _lotteries.find { it.id == lotteryID } ?: return null
        return Pair(lotteryDatesToString(lottery), getLotteryListings(lottery))
    }

    /** Takes a lottery as input and returns start and end date as a formatted string */
    fun lotteryDatesToString(lottery: Lottery): String {
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val dayFormat = SimpleDateFormat("d", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

        val startMonth = monthFormat.format(lottery.start)
        val endMonth = monthFormat.format(lottery.end)
        val startDay = dayFormat.format(lottery.start)
        val endDay = dayFormat.format(lottery.end)
        val startYear = yearFormat.format(lottery.start)
        val endYear = yearFormat.format(lottery.end)

        return when {
            startYear != endYear   -> "$startMonth $startDay, $startYear - $endMonth $endDay, $endYear"
            startMonth != endMonth -> "$startMonth $startDay - $endMonth $endDay, $startYear"
            startDay != endDay     -> "$startMonth $startDay-$endDay, $startYear"
            else                   -> "$startMonth $startDay, $startYear"
        }
    }
    /** Takes in lottery as input and returns the listings under it*/
    fun getLotteryListings(lottery: Lottery): MutableList<Listing> {
        val listings = mutableListOf<Listing>()
        for (campsiteID in lottery.campsiteIDs){
            for (listing in _listings){
                if (listing.id == campsiteID){
                    listings.add(listing)
                }
            }
        }
        return listings
    }


}
