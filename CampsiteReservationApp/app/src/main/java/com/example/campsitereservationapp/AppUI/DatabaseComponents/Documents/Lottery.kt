package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate
import java.util.Date

class Lottery(var dates: List<LocalDate> = emptyList(),
              val start: LocalDate = LocalDate.now(),
              val end: LocalDate = LocalDate.now(),
              var campsiteIDs: List<String> = emptyList()) {

    /** Firestore document id, populated automatically on read via [DocumentId]. */
    @DocumentId
    var id: String = ""

    var waitlist = emptyList<String>()

    /** Adds [entrant] to this lottery's waitlist.
     *
     * Takes the DeviceID of the entrant and adds it to the waiting list*/
    fun addEntrant(entrantID: String) {
        var templist = ArrayList<String>()
        for(id in waitlist){
            templist.add(id)
        }
        templist.add(entrantID)
        waitlist = templist
    }
}