/**
 * This class hold the profile data class, it contains all information about the profile
 * and also settings (right now it's only if notification are on or not)
 *
 */
package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.companion.DeviceId
import com.google.firebase.firestore.DocumentId

/**
 * A user's account, stored in the Profiles collection. The document ID is the device ID, so each
 * device has one profile and needs no password.
 *
 * @property name the user's full name.
 * @property deviceId the ID of the device this profile belongs to.
 * @property id the Firestore document ID, which is the same as [deviceId].
 * @property email the user's email address.
 * @property phoneNumber the user's phone number, as they typed it.
 * @property profilePicture the user's photo as Base64 text, or empty if they haven't added one.
 * @property notificationsEnabled whether the user wants to receive notifications.
 */
class Profile(var name: String = "",
              val deviceId: String = "") {
    @DocumentId
    var id = ""
    var email = ""
    var phoneNumber = ""
    var profilePicture = "" //Base64 text, see PhotoUtils
    var notificationsEnabled = true
    var role = ""           //either Entrant, Owner, or Admin for entrant, owner, and admin
}