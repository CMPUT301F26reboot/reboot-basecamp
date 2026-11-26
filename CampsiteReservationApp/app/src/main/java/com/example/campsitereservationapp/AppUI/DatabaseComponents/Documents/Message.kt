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
 *@param text [String] content text of the message
 *@param senderID [String] ID of the user who sent the message (same as deviceID)
 *@param receiverID [String] ID of the user receiving the message (same as deviceID)
 *@param timestamp [Timestamp] timestamp of when the message was sent (default as Timestamp.now())
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