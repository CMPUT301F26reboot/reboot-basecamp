package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.companion.DeviceId
import com.google.firebase.firestore.DocumentId

class Profile(name: String = "",
              deviceId: String = "") {
    @DocumentId
    protected var id = ""
    protected var email = ""
    protected var phoneNumber = 0
    protected var profilePicture = "" //should be a URL
}