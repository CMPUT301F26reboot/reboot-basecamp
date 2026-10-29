package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate
import java.util.Date

class Reservation (var dates: List<LocalDate> = emptyList<LocalDate>(),
                   var entrantID: String = "",
                   var campsiteID: String = "") {
    @DocumentId
    var id = ""
}