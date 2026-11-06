package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.companion.DeviceId
import com.google.firebase.firestore.DocumentId

class Profile(name: String = "",
              deviceId: String = "") {
    @DocumentId
    var id = ""
    var email = ""
    var phoneNumber = ""
    var profilePicture = "" //should be a URL
}