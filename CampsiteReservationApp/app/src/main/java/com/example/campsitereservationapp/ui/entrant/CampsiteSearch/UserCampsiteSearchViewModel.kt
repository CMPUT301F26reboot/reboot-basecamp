package com.example.campsitereservationapp.ui.entrant.CampsiteSearch

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateSetOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.utils.DateFormatter
import java.util.Calendar
import java.util.Date

class UserCampsiteSearchViewModel: DatabaseModel() {
     var _searchResults = mutableStateListOf<Listing>()
     var _selectedDates = mutableStateSetOf<Date>()



    val listings: List<Listing> get() = _searchResults

    init {
        for(listing in _listings){
            _searchResults.add(listing)
        }
    }

    /**
     * Searches for campsites based on criteria
     *
     * Queries database based on keyword, then filters results based on [selectedDates], [location], and [capacity]
     * Adds these filtered results to _searchResults
     *
     * [keyword] is string typed into search bar which is used to do a prefix query on the names of each campsite
     * [selectedDates] is set of dates the entrant wished to book
     * [location] is location of campsite
     * [capacity] is minimum MaxOccupancy of each campsite
     */
    fun doSearch(keyword: String, location: String, capacity: Int){
        _searchResults.clear()
        for (listing in _listings){
            if(meetsDateRequirements(_selectedDates, listing) &&
                meetsKeywordRequirements(keyword, listing) &&
                meetsLocationRequirements(location, listing) &&
                listing.maxOccupancy >= capacity
                )
            {
                _searchResults.add(listing)
            }
        }
    }


    internal fun meetsDateRequirements(selectedDates: Set<Date>, listing: Listing): Boolean{
        if(selectedDates.isEmpty()){
            return true
        }
        var availableDates = mutableSetOf<Date>()
        for(lottery in _lotteries){
            if(listing.id in lottery.campsiteIDs){
                for(date in lottery.dates){
                    availableDates.add(date)
                }
            }
        }
        return (selectedDates - availableDates).isEmpty() //if all selected dates are in available dates then requirement satisfied
    }

    /**
     * Shows if a listing meets the keyword requirements
     *
     * Compares the keyword with the listing name using compareTo
     */
    internal fun meetsKeywordRequirements(keyword: String, listing: Listing): Boolean{
        if(keyword.isEmpty())
            return true

        return (keyword.compareTo(listing.name, ignoreCase = true) <= 0                 //keyword is a prefix or comes before
                && listing.name.compareTo(keyword + '\uffff', ignoreCase = true) < 0) //keyword + large character is larger
    }

    internal fun meetsLocationRequirements(location: String, listing: Listing): Boolean{
        if(location.isEmpty())
            return true
        return (location.compareTo(listing.address, ignoreCase = true) == 0)
    }

    /**
     * Adds or removes a date from _selected dates
     *
     * @param [date] is a date that should be toggled. If it's already in _selectedDates, it should be removed and vice versa
     */
    fun toggleDate(date: Date){
        if(date in _selectedDates){
            _selectedDates.remove(date)
        }else{
            _selectedDates.add(date)
        }
    }

    internal fun get365Dates(): Set<Date>{
        val formatter = DateFormatter()
        val calendar = Calendar.getInstance() // Starts at today
        val dates = mutableStateListOf<Date>()

        for (i in 1..365) {
            dates.add(formatter.formatDate(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to the next day
        }

        return dates.toSet()
    }
}
