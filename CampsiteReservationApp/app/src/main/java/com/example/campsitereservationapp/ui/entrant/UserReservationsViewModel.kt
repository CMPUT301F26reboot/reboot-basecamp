package com.example.campsitereservationapp.ui.entrant

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import java.util.Date
import kotlin.math.round


/**
 * Entrant version of the reservations ViewModel.
 *
 * Adds the entrant-specific stuff on top of ReservationsViewModel:
 *  - Filtering by entrant id, hiding cancelled reservations.
 *  - The cancel confirmation dialog state.
 *  - The review dialog state, plus the writes that update the reservation and the
 *    campsite's average rating.
 *
 * Outstanding issues:
 *  - submitReview does a read-then-write on the listing's rating. If two people submit at
 *    the same time, the average could be slightly off.
 */
class UserReservationsViewModel : ReservationsViewModel() {

    /**
     * Returns this entrant's reservations, with cancelled ones removed and the current filter
     * applied.
     *
     * "Reserved" means the last booked day is today or later. "Past" means the last booked day
     * has already gone by. We compare timestamps directly.
     */
    fun filteredReservations(entrantId: String): List<Reservation> {
        val today = Date()
        val all = allReservations
            .filter{
                it.entrantID == entrantId && it.status != "cancelled"
            }
        return when (filter) {
            ReservationFilter.All -> all

            ReservationFilter.Reserved -> all.filter {
                val lastDate = it.dates.maxOrNull()
                lastDate == null || lastDate.after(today)
            }

            ReservationFilter.Past -> all.filter {
                val lastDate = it.dates.maxOrNull()
                lastDate != null && lastDate.before(today)
            }
        }
    }

    /** This tracks which reservation the user tapped "Cancel" on*/
    var pendingCancelId by mutableStateOf<String?>(null)
        private set

    /**  The UI calls this when the user taps the "Cancel" button on a card*/
    fun onCancelClicked(reservationId: String) {
        pendingCancelId = reservationId
    }

    /** The UI calls this if the user taps "Keep it"*/
    fun dismissCancelDialog() {
        pendingCancelId = null
    }

    /** The UI calls this when the user taps "Yes, cancel" in the dialog*/
    fun confirmCancel() {
        val id = pendingCancelId ?: return
        cancelReservation(id)
        pendingCancelId = null // Close the dialog
    }

    //implement review function
    /** How many stars are currently selected (0 = nothing picked). */
    var reviewRating by mutableIntStateOf(0)

    /** Called when the user taps a star. */
    fun onRatingChanged(newRating: Int) {
        reviewRating = newRating
    }
    var reviewingReservationId by mutableStateOf<String?>(null)

    /**
     * Opens the rating dialog for the given reservation. Resets the stars to 0 so each new
     * review starts blank.
     */
    fun onLeaveReviewClicked(reservationId: String) {
        reviewingReservationId = reservationId
        reviewRating = 0
    }

    /** Closes the rating dialog without submitting. Resets the stars. */
    fun dismissReviewSheet() {
        reviewingReservationId = null
        reviewRating = 0
    }

    /**
     * Saves the rating for the currently-open reservation.
     *
     * Does two writes:
     *  1. Marks the reservation as rated, so the "Leave a review!" button doesn't show up
     *     again.
     *  2. Updates the campsite's average and review count using the standard incremental
     *     formula, rounded to one decimal place.
     *
     * The dialog closes after both writes settle, whether they succeeded or not
     */
    fun submitReview(rating: Int) {
        val reservationId = reviewingReservationId ?: return
        val reservation = allReservations.firstOrNull { it.id == reservationId } ?: return
        val listing = listingFor(reservation) ?: return

        val oldCount = listing.numOfReviews
        val oldRating = listing.rating
        val newCount = oldCount + 1
        val rawAverage = ((oldRating * oldCount) + rating) / newCount
        val newRating = round(rawAverage * 10) / 10.0

        reservationsRef.document(reservationId)
            .update("rated", true)
            .addOnSuccessListener {
                listingsRef.document(listing.id)
                    .update(
                        "rating", newRating,
                        "numOfReviews", newCount
                    )
                    .addOnSuccessListener {
                        println("Rating saved to listing!")
                        reviewingReservationId = null
                        reviewRating = 0
                    }
                    .addOnFailureListener { e ->
                        println("Failed to update listing: ${e.message}")
                        reviewingReservationId = null
                        reviewRating = 0
                    }
            }
    }
}
