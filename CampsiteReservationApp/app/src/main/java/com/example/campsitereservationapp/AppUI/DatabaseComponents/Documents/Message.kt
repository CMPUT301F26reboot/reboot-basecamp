/**
 * This file is for the message data class, it holds the data for a message, who it's sent by,
 * a timestamp, and who it's sent to
 */
package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

/**
 * Stores a message between two users in their chat
 */
class Message(
    var text: String = "",
    var senderID: String = "",
    var receiverID: String = "",
    var timestamp: Timestamp = Timestamp.now()
) {
    @DocumentId
    var id = ""
}