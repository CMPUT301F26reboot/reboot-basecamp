package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile

/**
 * Gives sign up screens the ability to read the device ID, check whether the device already has a
 * profile, and store a new profile in the Profiles collection.
 */
open class ProfileCreatingViewModel : DatabaseModel() {

    /** Returns the ID used to recognize this device, so that no password is needed. */
    @SuppressLint("HardwareIds")
    fun readDeviceId(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    /**
     * Passes true to [onResult] if [deviceId] already has a profile. Passes false if it doesn't or
     * if the check fails, so that the user still gets the sign up form.
     */
    protected fun profileExists(deviceId: String, onResult: (Boolean) -> Unit) {
        profilesRef.document(deviceId).get()
            .addOnSuccessListener { document -> onResult(document.exists()) }
            .addOnFailureListener { onResult(false) }
    }

    /** Writes [profile] to the document named after its device ID. */
    protected fun saveProfile(profile: Profile, onSuccess: () -> Unit, onFailure: () -> Unit) {
        profilesRef.document(profile.deviceId).set(profile)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}