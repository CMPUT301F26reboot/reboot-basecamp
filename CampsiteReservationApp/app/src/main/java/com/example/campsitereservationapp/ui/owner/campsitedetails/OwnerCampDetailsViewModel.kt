package com.example.campsitereservationapp.ui.owner.campsitedetails

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.util.UUID
class OwnerCampDetailsViewModel : DatabaseModel() {

    /** Locally picked photo URIs, in upload order. The first is the cover photo. */
    var photos by mutableStateOf<List<Uri>>(emptyList())
        private set

    var siteName by mutableStateOf("")
    var location by mutableStateOf("")
    var maxOccupancy by mutableStateOf(MIN_OCCUPANCY)
        private set
    var overview by mutableStateOf("")
    var rules by mutableStateOf("")
    var rulesAcceptanceRequired by mutableStateOf(true)
    var tags by mutableStateOf<List<String>>(emptyList())
        private set

    var geoLocation by mutableStateOf(Listing().geoLocation)
        private set

    /** True while photos are uploading and the listing document is being written. */
    var isPublishing by mutableStateOf(false)
        private set

    /** True once the listing document has been written successfully. */
    var isPublished by mutableStateOf(false)
        private set

    /** Message to show in a snackbar, or null if there's nothing to report. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** True once [siteName] and [location] are filled in and nothing is already in flight. */
    val canPublish: Boolean
        get() = siteName.isNotBlank() && location.isNotBlank() && !isPublishing

    /** Adds [uris] to [photos], de-duplicated and capped at [MAX_PHOTOS]. */
    fun addPhotos(uris: List<Uri>) {
        photos = (photos + uris).distinct().take(MAX_PHOTOS)
    }

    fun removePhoto(uri: Uri) {
        photos = photos - uri
    }

    fun incrementOccupancy() {
        maxOccupancy += 1
    }

    fun decrementOccupancy() {
        if (maxOccupancy > MIN_OCCUPANCY) maxOccupancy -= 1
    }
    fun addTag(tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isNotEmpty() && trimmed !in tags) tags = tags + trimmed
    }

    fun removeTag(tag: String) {
        tags = tags - tag
    }
    fun clearError() {
        errorMessage = null
    }
    fun publish(ownerId: String) {
        if (!canPublish) return
        isPublishing = true
        errorMessage = null

        if (photos.isEmpty()) {
            saveListing(ownerId, emptyList())
            return
        }

        val storageRoot = Firebase.storage.reference.child("listings").child(ownerId)
        val uploadTasks = photos.map { uri ->
            val photoRef = storageRoot.child(UUID.randomUUID().toString())
            photoRef.putFile(uri).continueWithTask { uploadTask ->
                if (!uploadTask.isSuccessful) {
                    throw uploadTask.exception ?: IllegalStateException("Photo upload failed")
                }
                photoRef.downloadUrl
            }
        }

        Tasks.whenAllSuccess<Uri>(uploadTasks)
            .addOnSuccessListener { downloadUrls -> saveListing(ownerId, downloadUrls.map { it.toString() }) }
            .addOnFailureListener {
                isPublishing = false
                errorMessage = "Couldn't upload photos. Please try again."
            }
    }

    private fun saveListing(ownerId: String, photoUrls: List<String>) {
        val listing = Listing(
            name = siteName.trim(),
            address = location.trim(),
            maxOccupancy = maxOccupancy,
            description = overview.trim(),
            ownerID = ownerId,
            tags = tags,
            photoUrls = photoUrls,
            geoLocation = geoLocation,
            rules = rules.trim(),
            rulesAcceptanceRequired = rulesAcceptanceRequired
        )

        listingsRef.add(listing)
            .addOnSuccessListener {
                isPublishing = false
                isPublished = true
            }
            .addOnFailureListener {
                isPublishing = false
                errorMessage = "Couldn't publish the listing. Please try again."
            }
    }

    private companion object {
        const val MAX_PHOTOS = 6
        const val MIN_OCCUPANCY = 1
    }
}
