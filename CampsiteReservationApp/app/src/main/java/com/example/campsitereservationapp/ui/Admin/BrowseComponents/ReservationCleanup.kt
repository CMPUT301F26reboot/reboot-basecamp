/**
 * This file contains the rules for which reservations are cancelled when an admin deletes a
 * campsite or a profile.
 *  - Deleting a campsite cancels its future reservations, but lets stays that are happening now
 *    finish, so nobody is told to leave while camping.
 *  - Deleting a profile cancels all of its reservations that haven't finished, as if the account
 *    never existed.
 */
package com.example.campsitereservationapp.ui.Admin.BrowseComponents

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.utils.endOfDay
import com.example.campsitereservationapp.utils.toDayOnly
import java.util.Date

/**
 * True when [reservation] is still booked and its stay starts after today, so it can be
 * cancelled when its campsite is deleted.
 *
 * @param now the current time.
 */
internal fun startsAfterToday(reservation: Reservation, now: Date): Boolean {
    val firstNight = reservation.dates.minOrNull() ?: return false
    return reservation.status == "reserved" && firstNight.toDayOnly().after(now)
}

/**
 * True when [reservation] is still booked and its stay hasn't finished yet (upcoming or happening
 * now), so it is cancelled when its entrant's profile is deleted. A reservation without dates
 * counts as not finished.
 *
 * @param now the current time.
 */
internal fun notFinished(reservation: Reservation, now: Date): Boolean {
    if (reservation.status != "reserved") return false
    val lastNight = reservation.dates.maxOrNull() ?: return true
    return lastNight.endOfDay().after(now)
}
