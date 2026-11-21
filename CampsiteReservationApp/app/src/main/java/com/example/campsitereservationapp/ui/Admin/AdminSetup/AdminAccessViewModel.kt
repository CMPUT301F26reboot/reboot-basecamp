package com.example.campsitereservationapp.ui.Admin.AdminSetup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel

/**
 * Decides whether this device belongs to an administrator. A device is an admin when a document
 * named after its device ID exists in the Firestore `Admins` collection. There is no admin sign up:
 * admins are added and removed by hand in the Firebase console, and nothing in the app writes to
 * `Admins`, so the app itself can never make someone an admin.
 *
 * Outstanding issues:
 *  - The app has no Firebase Authentication, so this check only runs inside the app; Firestore
 *    security rules cannot verify a device ID.
 *  - The result is read once per screen, so removing an admin in the console takes effect the next
 *    time the screen opens.
 */
class AdminAccessViewModel : DatabaseModel() {

    /** True once the check has found this device in `Admins`. */
    var isAdmin by mutableStateOf(false)
        private set

    /** True until the check for the current device has finished. */
    var isChecking by mutableStateOf(true)
        private set

    /** The device last checked, so recomposition and rotation don't repeat the lookup. */
    private var checkedDeviceId: String? = null

    /**
     * Looks up [deviceId] in `Admins` and updates [isAdmin]. A failed lookup, such as being offline
     * with nothing cached, counts as not an admin.
     *
     * @param deviceId the ID of this device, which is also the admin document's ID.
     */
    fun check(deviceId: String) {
        if (checkedDeviceId == deviceId) return
        checkedDeviceId = deviceId
        isChecking = true
        adminsRef.document(deviceId).get()
            .addOnSuccessListener { document ->
                isAdmin = document.exists()
                isChecking = false
            }
            .addOnFailureListener {
                isAdmin = false
                isChecking = false
            }
    }
}
