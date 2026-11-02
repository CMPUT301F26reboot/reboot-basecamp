package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.location.Location
import android.location.LocationManager
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

class Listing(
    var name: String = "",
    var address: String = "",
    var maxOccupancy: Int = 0,
    var description: String = "",
    var rating: Double = 0.0,
    var numOfReviews: Int = 0,
    var ownerID: String = "",
    var tags: List<String> = emptyList(),
    var photoUrls: List<String> = emptyList(),
    var geoLocation: GeoPoint = GeoPoint(0.0, 0.0)
){
    @DocumentId
    val id: String = ""
}
