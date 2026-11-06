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
     * Passes true to [onResult] if [deviceId] already has a profile. Passes false if it doesn't or
     * if the check fails, so that the user still gets the sign up form.
     *
     * @param deviceId the device to look for.
     * @param onResult called with whether the device has a profile.
     */
    protected fun profileExists(deviceId: String, onResult: (Boolean) -> Unit) {
        profilesRef.document(deviceId).get()
            .addOnSuccessListener { document -> onResult(document.exists()) }
            .addOnFailureListener { onResult(false) }
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