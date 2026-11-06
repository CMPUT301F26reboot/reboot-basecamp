/*
 * AI use:
 * - Q: How do I keep Publish disabled until the form is valid?
 *   A: A computed canPublish (name and address filled, not already publishing) bound to enabled.
 * - Q: Firebase Storage or keep photos in the document?
 *   A: Storage is standard. Base64 in the document works only if images are compressed and few,
 *      because of the 1 MiB document limit.
 * - Q: Should screens connect to Firebase directly, or use getters and setters? I was worried my
 *      changes might crash Firebase (it hasn't happened).
 *   A: Go through the ViewModel. Screens read its state (getters) and call its functions, and only
 *      the ViewModel/DatabaseModel touches Firestore. Keep default values on every model field so
 *      Firestore can rebuild the objects.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp.ui.owner.CampsiteRegister

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.utils.encodePhotoToBase64
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    /** Updates [geoLocation] from a point picked on the map (search or dropped pin). */
    fun setLocation(latitude: Double, longitude: Double) {
        geoLocation = GeoPoint(latitude, longitude)
    }

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
    fun publish(ownerId: String, context: Context) {
        if (!canPublish) return
        isPublishing = true
        errorMessage = null

        viewModelScope.launch {
            val encodedPhotos = withContext(Dispatchers.IO) {
                photos.mapNotNull { uri -> encodePhotoToBase64(context, uri) }
            }

            if (encodedPhotos.size != photos.size) {
                isPublishing = false
                errorMessage = "Couldn't read one of the photos. Please try again."
                return@launch
            }

            saveListing(ownerId, encodedPhotos)
        }
    }

    private fun saveListing(ownerId: String, encodedPhotos: List<String>) {
        val listing = Listing(
            name = siteName.trim(),
            address = location.trim(),
            maxOccupancy = maxOccupancy,
            description = overview.trim(),
            ownerID = ownerId,
            tags = tags,
            photoUrls = encodedPhotos,
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
        const val MAX_PHOTOS = 4
        const val MIN_OCCUPANCY = 1
    }
}
