package com.example.campsitereservationapp.ui.entrant

import androidx.lifecycle.ViewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel





class UserWonSelectionViewModel(
    val repository: DatabaseModel = DatabaseModel()
) : ViewModel() {

    init {//adding test data
        repository.addListing(
            Listing(
                name = "Title",
                address = "adress",
                maxOccupancy = 404,
                rating = 1.0,
                numOfReviews = 100,
                availability = "May1-2",
            )
        )
        repository.addListing(
            Listing(
                name = "LISTINGS 2",
                address = "adress",
                maxOccupancy = 404,
                rating = 1.0,
                numOfReviews = 100,
                availability = "May1-2",
            )
        )
    }




}
