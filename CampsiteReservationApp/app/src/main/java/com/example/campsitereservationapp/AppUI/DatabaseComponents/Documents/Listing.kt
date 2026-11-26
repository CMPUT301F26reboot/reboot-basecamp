/**
 * This file hold the Listing data class, it stores all information about a Listing/Campsitre
 */
package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.location.Location
import android.location.LocationManager
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

/**
 * This class holds information about a listing
 *@param name [String] name of listing
 *@param address [String] address of listing
 *@param maxOccupancy [Int] maximum amount of people that can enter listing
 *@param description [String] description of listing
 *@param rating [Double]
 *@param numOfReviews [Int]
 *@param ownerID [String] ID of owner (same as deviceID)
 *@param ownerName [String] name of owner
 *@param tags [List] of [String] tags of listing
 *@param photoUrls [List] of [String] byte64 representation of image see [com.example.campsitereservationapp.utils.encodePhotoToBase64]
 *@param geoLocation [GeoPoint] latitude and longitude of location
 *@param rules [String]
 *@param rulesAcceptanceRequired [Boolean] default true
 */
class Listing(
    var name: String = "",
    var address: String = "",
    var maxOccupancy: Int = 0,
    var description: String = "",
    var rating: Double = 0.0,
    var numOfReviews: Int = 0,
    var ownerID: String = "",
    var ownerName: String = "",
    var tags: List<String> = emptyList(),
    var photoUrls: List<String> = emptyList(),
    var geoLocation: GeoPoint = GeoPoint(0.0, 0.0),
    var rules: String = "",
    var rulesAcceptanceRequired: Boolean = true
){
    @DocumentId
    var id: String = ""
}
