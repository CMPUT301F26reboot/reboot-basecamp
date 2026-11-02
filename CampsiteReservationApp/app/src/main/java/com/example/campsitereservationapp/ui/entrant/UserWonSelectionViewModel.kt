package com.example.campsitereservationapp.ui.entrant

import androidx.lifecycle.ViewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel




class UserWonSelectionViewModel(
    //val repository: DatabaseModel = DatabaseModel()
) : ViewModel() {

    val listings: List<Listing> = listOf(
        Listing(
            name = "Pine Valley Retreat",
            address = "Yosemite, CA",
            maxOccupancy = 4,
            rating = 4.9,
            numOfReviews = 128,
        ),
        Listing(
            name = "Coastal Cliffside",
            address = "Big Sur, CA",
            maxOccupancy = 4,
            rating = 4.7,
            numOfReviews = 74,
        )
    )
    val lottery_date = "May 1-7,2027"









}
