/**
 * Base ViewModel for sign up, checks if this device already has a profile and saves new profiles to Firestore.
 */
package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import android.content.Context
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.utils.getDeviceId

/**
 * Gives sign up screens the ability to read the device ID, check whether the device already has a
 * profile, and store a new profile in the Profiles collection.
 */
open class ProfileCreatingViewModel : DatabaseModel() {

    /**
     * Returns the ID used to recognize this device, so that no password is needed.
     *
     * @param context used to read the device ID.
     */
    fun readDeviceId(context: Context): String = getDeviceId(context)

    /**
     * Passes the role of [deviceId]'s profile to [onResult], or null if the device has no profile
     * or the check fails, so that the user still gets the sign up form. A profile saved before
     * roles existed has no role and counts as an entrant.
     *
     * @param deviceId the device to look for.
     * @param onResult called with the profile's role, or null if there is no profile.
     */
    protected fun findProfileRole(deviceId: String, onResult: (String?) -> Unit) {
        profilesRef.document(deviceId).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) onResult(null)
                else onResult(document.getString("role")?.takeIf { it.isNotBlank() } ?: Profile.ROLE_ENTRANT)
            }
            .addOnFailureListener { onResult(null) }
    }

    /**
     * Writes [profile] to the document named after its device ID.
     *
     * @param profile the profile to store.
     * @param onSuccess called once the profile has been written.
     * @param onFailure called if the profile could not be written.
     */
    protected fun saveProfile(profile: Profile, onSuccess: () -> Unit, onFailure: () -> Unit) {
        profilesRef.document(profile.deviceId).set(profile)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}