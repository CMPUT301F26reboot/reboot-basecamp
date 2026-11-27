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
    fun getImages(listingID: String): List<String> {
        return getListing(listingID)?.photoUrls.orEmpty()
    }

    /**
     * Deletes image, of a listing given the image's index
     *
     * @param listingId id of listing that contains the image
     * @param index index of image in listing.photos array
     * @param onSuccess action after successful image deletion
     * @param onFailure action after image deletion failure
     *
     * COPILOT PROMPT: how do I delete a field, when the field is a list and the element being deleted is given
     */
    fun deleteImage(
        listingId: String,
        index: Int,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val listing = getListing(listingId) ?: return
        if (index !in listing.photoUrls.indices) return

        val updatedPhotos = listing.photoUrls.toMutableList().apply {
            removeAt(index)
        }

        listingsRef.document(listingId)
            .update("photoUrls", updatedPhotos)
            .addOnSuccessListener {
                listing.photoUrls = updatedPhotos
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}