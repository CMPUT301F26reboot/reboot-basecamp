package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import android.devicelock.DeviceId
import android.provider.Settings
import com.google.firebase.firestore.DocumentId

/**
 * A message log from an owner to an entrant
 *
 * Keeps track of messages between an owner to an entrant to be displayed
 * Has functionality to add a message
 *
 */
class Chat (val ownerID: String = "", val entrantID: String = ""){
    @DocumentId
    var id = ""
    var LogIDList = emptyList<String>() //list of LogEntry ids
}