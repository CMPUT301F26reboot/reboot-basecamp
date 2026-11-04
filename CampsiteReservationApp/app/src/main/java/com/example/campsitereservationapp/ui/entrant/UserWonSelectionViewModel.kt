package com.example.campsitereservationapp.ui.entrant

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import java.text.SimpleDateFormat
import java.time.Month


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

    /** Takes a lottery as input and returns start and end date as formated string */
    fun lotteryDatesToString(lottery: Lottery): String {
        val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

        //turn the date into string format
        val startString = SimpleDateFormat("MM-dd-yyyy").format(lottery.start);
        val endString = SimpleDateFormat("MM-dd-yyyy").format(lottery.end);

        //split the string
        val startStringSplit= startString.split("-")
        val endStringSplit = endString.split("-")


        //convert the months from numbers into abreviation
        val startMonth = months[startStringSplit[0].toInt() - 1]
        val endMonth = months[startStringSplit[0].toInt() - 1]

        //format the string
        val startDay = startStringSplit[1]
        val endDay = endStringSplit[1]
        val startYear = startStringSplit[2]
        val endYear = endStringSplit[2]

        return if (startYear != endYear) { //different years: show both years
            "$startMonth $startDay, $startYear - $endMonth $endDay, $endYear"
        } else if (startMonth == endMonth) { //same year and month: show the month and year once
            "$startMonth $startDay-$endDay, $startYear"
        } else { //same year, different months: show the year once
            "$startMonth $startDay - $endMonth $endDay, $startYear"
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
