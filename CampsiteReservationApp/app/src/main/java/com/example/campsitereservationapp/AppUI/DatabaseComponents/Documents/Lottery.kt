package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.firestore.DocumentId
import java.util.Date

class Lottery(var dates: List<Date> = emptyList(),
              val start: Date = Date(),
              val end: Date = Date(),
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