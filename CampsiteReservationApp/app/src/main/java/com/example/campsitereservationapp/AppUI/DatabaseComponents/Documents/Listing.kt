package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.location.Location

class Listing(
    var name: String = "",
    var address: String = "",
    var maxOccupancy: Int = 0,
    var description: String = "",
    var rating: Double = 0.0, //change F->D (firebase only has doubles)
    var numOfReviews: Int = 0,
    var ownerName: String = "",
    var tags: ArrayList<String> = ArrayList(),
    var photoUrls: ArrayList<String> = ArrayList(),
    var availability: String = ""
)