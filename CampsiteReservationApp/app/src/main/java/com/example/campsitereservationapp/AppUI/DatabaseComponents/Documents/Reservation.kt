package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.firestore.DocumentId
import java.util.Date

class Reservation (var dates: List<Date> = emptyList(),
                   var entrantID: String = "",
                   var campsiteID: String = "",
                   var status: String = "reserved",
                   var rated: Boolean = false  ) {
    @DocumentId
    var id = ""
}