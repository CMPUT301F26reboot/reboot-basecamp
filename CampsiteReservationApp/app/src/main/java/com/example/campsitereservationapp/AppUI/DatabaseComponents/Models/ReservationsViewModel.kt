package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import java.time.LocalDate

enum class ReservationFilter { All, Reserved, Past }

/**
 * Backs the User Reservations screen (US 01.06.01, 01.06.02, 01.07.01).
 *
 * A reservation is "past" when its last night is before today. "Reserved" (active) is
 * everything else that hasn't been cancelled. Cancelled reservations are excluded from both.
 */

open class ReservationsViewModel: DatabaseModel() {

    var filter by mutableStateOf(ReservationFilter.All)

    //need to implement this for user and potentially for when owner rejects
    var isCancelling by mutableStateOf(false)

    fun onFilterSelected(f: ReservationFilter) {
        filter = f
    }

    //all reservations for entrantId
    fun allReservations(entrantId: String): List<Reservation> =
        _reservations
            .filter { it.entrantID == entrantId }

    //filtered reservations
    fun filteredReservations(entrantId: String): List<Reservation> {
        val today = LocalDate.now()
        val all = allReservations(entrantId)
        return when (filter) {
            ReservationFilter.All -> all
            ReservationFilter.Reserved -> all.filter { (it.dates.maxOrNull() ?: today) >= today }
            ReservationFilter.Past -> all.filter { (it.dates.maxOrNull() ?: today) < today }
        }
    }

    // get listings for the screen
    fun listingFor(reservation: Reservation): Listing? =
        _listings.firstOrNull { it.id == reservation.campsiteID }

    //still to be implemented
    fun cancelReservation(reservationId: String) {

    }

}

