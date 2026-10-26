package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * Firestore document model for a campsite lottery: the dates on offer, the registration
 * window, which campsites are included, and the waitlist of interested entrants.
 *
 * Outstanding issues:
 *  - No no-arg constructor, so Firestore's `toObject(Lottery::class.java)` (see `DatabaseModel`)
 *    can fail to deserialize documents into this class.
 */
class Lottery(var dates: ArrayList<Date>, val start: Date, val end: Date, var campsiteNames: ArrayList<String>) {

    /** Firestore document id, populated automatically on read via [DocumentId]. */
    @DocumentId
    var id: String = ""

    var waitlist = ArrayList<Profile>()

    /** Adds [entrant] to this lottery's waitlist. */
    fun addEntrant(entrant: Profile) {
        waitlist.add(entrant)
    }
}