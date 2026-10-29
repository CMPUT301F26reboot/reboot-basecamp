package com.example.campsitereservationapp.ui.entrant

import androidx.lifecycle.ViewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel





class UserWonSelectionViewModel(
    val repository: DatabaseModel = DatabaseModel()
) : ViewModel() {

    //get listings from repository
    val listings: List<Listing> get() = repository.listings

    init {//adding test data
        repository.addListing(
            Listing(
                name = "Title",
                address = "adress",
                maxOccupancy = 404,
                rating = 1.0,
                numOfReviews = 100,
            )
        )
        repository.addListing(
            Listing(
                name = "LISTINGS 2",
                address = "adress",
                maxOccupancy = 404,
                rating = 1.0,
                numOfReviews = 100,
            )
        )
    }




}
