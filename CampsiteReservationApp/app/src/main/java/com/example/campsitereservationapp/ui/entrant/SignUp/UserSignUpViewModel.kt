package com.example.campsitereservationapp.ui.entrant.SignUp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ProfileCreatingViewModel
import com.example.campsitereservationapp.utils.isValidEmail
import com.example.campsitereservationapp.utils.isValidPhoneNumber

/**
 * ViewModel of UserSignUpScreen. Recognizes returning devices, holds the form, validates it, and
 * stores the new profile.
 *
 * Outstanding issues:
 *  - Firestore doesn't fail a save while offline, so [isSaving] stays true until the device
 *    reconnects and the save goes through.
 *  - Offline, a device that recently had a profile can be signed in from Firestore's local cache.
 */
class UserSignUpViewModel : ProfileCreatingViewModel() {

    /** The text in the Name field. */
    var name by mutableStateOf("")

    /** The text in the Username field. */
    var username by mutableStateOf("")

    /** The text in the Email Address field. */
    var email by mutableStateOf("")

    /** The text in the Phone Number field. */
    var phoneNumber by mutableStateOf("")

    /** The message to show under the Name field, or null if it is valid. */
    var nameError by mutableStateOf<String?>(null)
        private set

    /** The message to show under the Username field, or null if it is valid. */
    var usernameError by mutableStateOf<String?>(null)
        private set

    /** The message to show under the Email Address field, or null if it is valid. */
    var emailError by mutableStateOf<String?>(null)
        private set

    /** The message to show under the Phone Number field, or null if it is valid. */
    var phoneNumberError by mutableStateOf<String?>(null)
        private set

    /** True until we know whether this device already has a profile. */
    var isCheckingDevice by mutableStateOf(true)
        private set

    /** True while the profile is being written. */
    var isSaving by mutableStateOf(false)
        private set

    /** True once this device has a profile, either from before or just created. */
    var isSignedUp by mutableStateOf(false)
        private set

    /** Message to show in a snackbar, or null if there's nothing to report. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** Clears [errorMessage] once the snackbar has shown it. */
    fun clearError() {
        errorMessage = null
    }

    /**
     * Signs the user in automatically if [deviceId] already has a profile (US 01.09.01).
     *
     * @param deviceId the ID of this device.
     */
    fun checkDevice(deviceId: String) {
        profileExists(deviceId) { exists ->
            isCheckingDevice = false
            if (exists) isSignedUp = true
        }
    }

    /**
     * Validates the form and, if it is valid, creates a profile for [deviceId].
     *
     * @param deviceId the ID of this device, which also becomes the ID of the profile.
     */
    fun signUp(deviceId: String) {
        if (isSaving || !validate()) return
        isSaving = true

        val profile = Profile(name = name.trim(), deviceId = deviceId)
        profile.username = username.trim()
        profile.email = email.trim()
        profile.phoneNumber = phoneNumber.trim()

        saveProfile(
            profile,
            onSuccess = {
                isSaving = false
                isSignedUp = true
            },
            onFailure = {
                isSaving = false
                errorMessage = "Couldn't create your account. Please try again."
            }
        )
    }

    /** Sets an error on each invalid field and returns true if there are none. */
    private fun validate(): Boolean {
        nameError = if (name.isBlank()) "Enter your name" else null
        usernameError = if (username.isBlank()) "Enter a username" else null
        emailError =
            if (isValidEmail(email)) null
            else "Enter a valid email address"
        phoneNumberError =
            if (isValidPhoneNumber(phoneNumber)) null
            else "Enter a valid phone number"

        return listOf(nameError, usernameError, emailError, phoneNumberError).all { it == null }
    }
}