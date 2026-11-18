package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.firestore.DocumentId
import java.util.Date

class Notifications(
    var senderId: String = "",
    var recipientIds: List<String> = emptyList(),
    var type: String = "",
    var subject: String = "",
    var message: String = "",
    var lotteryId: String = "",
    var reservationId: String = "",
    var timestamp: Date = Date(),
    var viewed: Boolean = false
) {
    @DocumentId
    var id = ""
}