/**
 * This file contains the viewModel of CampsiteImagesScreen
 */
package com.example.campsitereservationapp.ui.Admin.CampsiteImages

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.firestore.FieldValue

class CampsiteImagesViewModel: DatabaseModel() {

    /**
     * Gets the listing given the ID
     */
    fun getListing(listingId: String): Listing? {
        return _listings.firstOrNull { it.id == listingId }
    }

    /**
     * Gets the images in listing
     */
    fun getImages(listingID: String): List<String>{
        return getListing(listingID)?.photoUrls.orEmpty()
    }

    /**
     * Deletes image, of a listing given the image's index
     *
     * @param listingId id of listing that contains the image
     * @param photoUrl field is called 'photoUrl' still, but it's the byte64 representation of it
     * @param onSuccess action after successful image deletion
     * @param onFailure action after image deletion failure
     *
     * COPILOT PROMPT: how do I delete a field, when the field is a list and the element being deleted is given
     * COPILOT RETURN: thats a dumb way of doing it, just use the photo URL as so:
     */
    fun deleteImage(
        listingId: String,
        photoUrl: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        if (listingId.isBlank() || photoUrl.isBlank()) return

        // 1. Remove photoUrl from the "photoUrls" array field in Firestore
        listingsRef.document(listingId)
            .update("photoUrls", FieldValue.arrayRemove(photoUrl))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}