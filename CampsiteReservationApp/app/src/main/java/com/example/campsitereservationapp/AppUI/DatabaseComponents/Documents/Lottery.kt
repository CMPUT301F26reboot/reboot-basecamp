/**
 * This file hold the lottery data class, it hold all information for a lottery
 */
package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * This class holds information about a lottery
 *@param dates [List] of [Date] list of stay dates for the reservation
 *@param start [Date] registration start date/time when entrants can join
 *@param end [Date] registration end date/time when lottery closes
 *@param campsiteIDs [List] of [String] IDs of campsites included in this lottery
 *@property title [String] title of the lottery event
 *@property waitlist [List] of [String] list of entrant IDs enrolled in the waitlist
 *@property selectedlist [List] of [String] list of entrant IDs selected as winners
 *@property waitlistCapacity [Int] optional maximum capacity for the waitlist
 *@property lotteryWinners [Int] optional number of winners to draw
 *@property cancelled [Boolean] whether this lottery has been cancelled
 */
class Lottery(var dates: List<Date> = emptyList(),
              val start: Date = Date(),
              val end: Date = Date(),
              var campsiteIDs: List<String> = emptyList()) {

    /** Firestore document id, populated automatically on read via [DocumentId]. */
    @DocumentId
    var id: String = ""

    var title: String = ""
    var waitlist = emptyList<String>()
    var selectedlist = emptyList<String>()
    var waitlistCapacity: Int? = null

    var lotteryWinners: Int? = null

    var cancelled: Boolean = false
    var drawn: Boolean = false

    /** Owner who created the lottery. Only they can edit or cancel it. */
    var ownerID: String = ""

    var pendingCampsiteIDs: List<String> = emptyList()

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