package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

class Message(
    var text: String = "",
    var senderID: String = "",
    var receiverID: String = "",
    var timestamp: Timestamp = Timestamp.now()
) {
    @DocumentId
    var id = ""
}