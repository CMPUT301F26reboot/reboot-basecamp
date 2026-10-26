package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.companion.DeviceId

class Profile(name: String, deviceId: DeviceId) {
    var email = ""
    var phoneNumber = 0
    var profilePicture = "" //should be a URL
    fun updateProfile(newName: String, newEmail: String, newPhoneNumber: Int, newProfilePicture: String){
        email = newEmail
        phoneNumber = newPhoneNumber
        profilePicture = newProfilePicture
    }
}