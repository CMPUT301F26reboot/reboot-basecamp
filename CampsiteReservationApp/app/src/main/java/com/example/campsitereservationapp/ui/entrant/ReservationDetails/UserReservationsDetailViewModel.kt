package com.example.campsitereservationapp.ui.entrant.ReservationDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel

enum class ReservationFilter { All, Reserved, Past }

/**
 * Backs the User Reservations screen (US 01.06.01, 01.06.02, 01.07.01).
 *
 * Holds the shared list, the filter selection, and the cancel logic. The entrant screen
 * extends this and adds its own filtering plus review handling. An owner screen could
 * extend it later and filter by owner instead.
 * Outstanding issues:
 *  - cancelReservation doesn't send the user a confirmation message yet (US 01.07.02)
 */

open class ReservationsViewModel: DatabaseModel() {

    var filter by mutableStateOf(ReservationFilter.All)

    //need to implement this for user and potentially for when owner rejects
    var isCancelling by mutableStateOf(false)

    /** Called by the filter buttons when the user picks a new filter. */
    fun onFilterSelected(f: ReservationFilter) {
        filter = f
    }

    val allReservations: List<Reservation>
        get() = _reservations



    /** Finds the Listing a reservation points at, or null if it was deleted. */
    fun listingFor(reservation: Reservation): Listing? =
        _listings.firstOrNull { it.id == reservation.campsiteID }

    /**
     * Flips the reservation's status to "cancelled" in Firestore
     */
    fun cancelReservation(reservationId: String) {
        if (isCancelling) return
        isCancelling = true
        // need to send notification to user here.
        reservationsRef.document(reservationId)
            .update("status", "cancelled")
            .addOnSuccessListener { isCancelling = false }
            .addOnFailureListener { isCancelling = false }
    }

}

