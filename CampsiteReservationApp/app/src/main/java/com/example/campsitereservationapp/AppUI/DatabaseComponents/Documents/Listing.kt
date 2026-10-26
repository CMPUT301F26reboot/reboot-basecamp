package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.location.Location


class Listing (
    var name: String,
    var location: String,
    var capacity: Int,
    var description: String,
    var rating: Float,
    var photo: String,
    var geoLocation: Location
){

}