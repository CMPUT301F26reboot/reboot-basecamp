/**
 * This file contains the Reservation data class, it holds all the information of when a User
 * reservers a campsite
 */
package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * This class holds information about a reservation
 *@param dates [List] of [Date] list of reserved dates
 *@param entrantID [String] ID of entrant who made the reservation (same as deviceID)
 *@param campsiteID [String] ID of reserved campsite listing
 *@param status [String] status of reservation (default "reserved")
 *@param rated [Boolean] whether entrant has rated this reservation (default false)
 */
class Reservation (var dates: List<Date> = emptyList(),
                   var entrantID: String = "",
                   var campsiteID: String = "",
                   var campName: String = "",
                   var status: String = "reserved",
                   var rated: Boolean = false  ) {
    @DocumentId
    var id = ""
}