package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.companion.DeviceId
import com.google.firebase.firestore.DocumentId

/**
 * A user's account, stored in the Profiles collection. The document ID is the device ID, so each
 * device has one profile and needs no password.
 *
 * @property name the user's full name.
 * @property deviceId the ID of the device this profile belongs to.
 */
class Profile(var name: String = "",
              val deviceId: String = "") {
    @DocumentId
    var id = ""
    var email = ""
    var phoneNumber = ""
    var profilePicture = "" //should be a URL
}