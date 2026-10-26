package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.location.Location

/**
 * Firestore document model for a campsite listing, as created by a campsite owner.
 *
 * Outstanding issues:
 *  - No no-arg constructor, so Firestore's `toObject(Listing::class.java)` (see `DatabaseModel`)
 *    can fail to deserialize documents into this class.
 *  - No document id field, so screens currently match a `Listing` by [name] instead of id.
 */
class Listing(
    var name: String,
    var address: String,
    var maxOccupancy: Int,
    var description: String,
    var rating: Float,
    var ownerName: String,
    var tags: ArrayList<String>,
    var photoUrls: ArrayList<String>,
    var geoLocation: Location
)