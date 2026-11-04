package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import java.time.LocalDate


/**
 * Adds the user cancel reservation flow (US 01.07.01).
 * filters the wanted reservations by entrant ID and reservation status
 */
class UserReservationsViewModel : ReservationsViewModel() {

    // filters the list of reservation by entrant ID and by reservation status
    fun filteredReservations(entrantId: String): List<Reservation> {
        val today = LocalDate.now()
        val all = allReservations
            .filter{
                it.entrantID == entrantId && it.status != "cancelled"
            }
        return when (filter) {
            ReservationFilter.All -> all
            ReservationFilter.Reserved -> all.filter { (it.dates.maxOrNull() ?: today) >= today }
            ReservationFilter.Past -> all.filter { (it.dates.maxOrNull() ?: today) < today }
        }
    }

    // This tracks which reservation the user tapped "Cancel" on
    var pendingCancelId by mutableStateOf<String?>(null)
        private set

    // The UI calls this when the user taps the "Cancel" button on a card
    fun onCancelClicked(reservationId: String) {
        pendingCancelId = reservationId
    }

    // The UI calls this if the user taps "Keep it"
    fun dismissCancelDialog() {
        pendingCancelId = null
    }

    // The UI calls this when the user taps "Yes, cancel" in the dialog
    fun confirmCancel() {
        val id = pendingCancelId ?: return
        cancelReservation(id) // Calls the function in the parent class
        pendingCancelId = null // Close the dialog
    }
}
