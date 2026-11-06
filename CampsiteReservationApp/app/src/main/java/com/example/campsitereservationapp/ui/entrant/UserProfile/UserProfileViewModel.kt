package com.example.campsitereservationapp.ui.entrant.UserProfile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.utils.encodePhotoToBase64
import com.example.campsitereservationapp.utils.isValidEmail
import com.example.campsitereservationapp.utils.isValidPhoneNumber
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * An editable text field on the profile screen.
 *
 * @property key the name of the field in the Firestore document.
 * @property label the text shown beside the field.
 */
enum class ProfileField(val key: String, val label: String) {
    NAME("name", "Name"),
    EMAIL("email", "Email Address"),
    PHONE_NUMBER("phoneNumber", "Phone Number")
}

/**
 * ViewModel of UserProfileScreen. Loads the user's profile and gives the screen the ability to
 * change its details (US 01.07.03), change the notification setting (US 01.10.01), and delete it
 * (US 01.08.01).
 *
 * Outstanding issues:
 *  - Nothing sends notifications yet, so [notificationsEnabled] is saved but not acted on.
 *  - The toggle isn't on the notification log screen yet, because that screen doesn't exist.
 *  - [deleteProfile] removes the device ID from waitlists, but other screens still join lotteries
 *    with a hardcoded ID until navigation passes the real one.
 *  - Firestore doesn't fail a save while offline, so [isSaving] stays true until the device
 *    reconnects and the save goes through.
 */
class UserProfileViewModel : DatabaseModel() {

    /** The Firestore document ID of the loaded profile. */
    private var profileId = ""

    /** The user's saved name. */
    var name by mutableStateOf("")
        private set

    /** The user's saved email address. */
    var email by mutableStateOf("")
        private set

    /** The user's saved phone number. */
    var phoneNumber by mutableStateOf("")
        private set

    /** The user's saved photo as Base64 text, or empty if they haven't added one. */
    var profilePicture by mutableStateOf("")
        private set

    /** Whether the user wants to receive notifications. */
    var notificationsEnabled by mutableStateOf(true)
        private set

    /** The field being edited, or null if there isn't one. */
    var editingField by mutableStateOf<ProfileField?>(null)
        private set

    /** The text typed into [editingField]. */
    var draft by mutableStateOf("")

    /** The message to show under [editingField], or null if it is valid. */
    var fieldError by mutableStateOf<String?>(null)
        private set

    /** True until the profile has loaded. */
    var isLoading by mutableStateOf(true)
        private set

    /** True while a change is being written. */
    var isSaving by mutableStateOf(false)
        private set

    /** True once the profile has been deleted. */
    var isDeleted by mutableStateOf(false)
        private set

    /** Message to show in a snackbar, or null if there's nothing to report. */
    var message by mutableStateOf<String?>(null)
        private set

    /** Clears [message] once the snackbar has shown it. */
    fun clearMessage() {
        message = null
    }

    /**
     * Loads the profile with the document ID [id].
     *
     * @param id the Firestore document ID of the profile, which is the device ID.
     */
    fun load(id: String) {
        profileId = id
        profilesRef.document(id).get()
            .addOnSuccessListener { document ->
                document.toObject(Profile::class.java)?.let { profile ->
                    name = profile.name
                    email = profile.email
                    phoneNumber = profile.phoneNumber
                    profilePicture = profile.profilePicture
                    notificationsEnabled = profile.notificationsEnabled
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                message = "Couldn't load your profile. Please try again."
            }
    }

    /**
     * Returns the saved value of [field].
     *
     * @param field the field to look up.
     */
    fun valueOf(field: ProfileField): String = when (field) {
        ProfileField.NAME -> name
        ProfileField.EMAIL -> email
        ProfileField.PHONE_NUMBER -> phoneNumber
    }

    /**
     * Starts editing [field], beginning with its saved value.
     *
     * @param field the field to edit.
     */
    fun startEditing(field: ProfileField) {
        editingField = field
        draft = valueOf(field)
        fieldError = null
    }

    /** Validates [draft] and, if it is valid, saves it to [editingField]. */
    fun saveEdit() {
        val field = editingField ?: return
        if (isSaving) return
        val value = draft.trim()

        fieldError = errorFor(field, value)
        if (fieldError != null) return

        if (value == valueOf(field)) {
            editingField = null
            return
        }

        isSaving = true
        profilesRef.document(profileId).update(field.key, value)
            .addOnSuccessListener {
                isSaving = false
                setValue(field, value)
                editingField = null
                message = "Changes saved"
            }
            .addOnFailureListener {
                isSaving = false
                message = "Couldn't save your changes. Please try again."
            }
    }

    /**
     * Turns notifications on or off, and goes back to the old setting if the write fails.
     *
     * @param enabled true to turn notifications on, or false to turn them off.
     */
    fun updateNotifications(enabled: Boolean) {
        notificationsEnabled = enabled
        profilesRef.document(profileId).update("notificationsEnabled", enabled)
            .addOnFailureListener {
                notificationsEnabled = !enabled
                message = "Couldn't save your changes. Please try again."
            }
    }

    /**
     * Saves the photo at [uri] as the profile picture.
     *
     * @param context used to read the photo.
     * @param uri the photo the user picked.
     */
    fun updateProfilePicture(context: Context, uri: Uri) {
        if (isSaving) return
        isSaving = true
        viewModelScope.launch {
            val encoded = withContext(Dispatchers.IO) { encodePhotoToBase64(context, uri) }
            if (encoded == null) {
                isSaving = false
                message = "Couldn't read that photo. Please try another one."
                return@launch
            }
            profilesRef.document(profileId).update("profilePicture", encoded)
                .addOnSuccessListener {
                    isSaving = false
                    profilePicture = encoded
                    message = "Changes saved"
                }
                .addOnFailureListener {
                    isSaving = false
                    message = "Couldn't save your changes. Please try again."
                }
        }
    }

    /** Deletes the profile and takes the user off every lottery waitlist they joined. */
    fun deleteProfile() {
        if (isSaving) return
        isSaving = true
        lotteriesRef.whereArrayContains("waitlist", profileId).get()
            .addOnSuccessListener { lotteries ->
                val batch = db.batch()
                lotteries.documents.forEach { lottery ->
                    batch.update(lottery.reference, "waitlist", FieldValue.arrayRemove(profileId))
                }
                batch.delete(profilesRef.document(profileId))
                batch.commit()
                    .addOnSuccessListener {
                        isSaving = false
                        isDeleted = true
                    }
                    .addOnFailureListener { onDeleteFailed() }
            }
            .addOnFailureListener { onDeleteFailed() }
    }

    /** Lets the user try again after a delete fails. */
    private fun onDeleteFailed() {
        isSaving = false
        message = "Couldn't delete your profile. Please try again."
    }

    /** Stores [value] as the saved value of [field]. */
    private fun setValue(field: ProfileField, value: String) {
        when (field) {
            ProfileField.NAME -> name = value
            ProfileField.EMAIL -> email = value
            ProfileField.PHONE_NUMBER -> phoneNumber = value
        }
    }

    /** Returns the message to show if [value] isn't valid for [field], or null if it is. */
    private fun errorFor(field: ProfileField, value: String): String? = when (field) {
        ProfileField.NAME -> if (value.isBlank()) "Enter your name" else null
        ProfileField.EMAIL -> if (isValidEmail(value)) null else "Enter a valid email address"
        ProfileField.PHONE_NUMBER ->
            if (isValidPhoneNumber(value)) null else "Enter a valid phone number"
    }
}
