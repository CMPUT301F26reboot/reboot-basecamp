package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


/**
 * Adds the "leave a review" flow (US 01.06.02).
 * Writes to Listings/{campsiteId}/Reviews.
 */
class UserReservationsViewModel : ReservationsViewModel() {

    var reviewingReservationId by mutableStateOf<String?>(null)

    var isSubmittingReview by mutableStateOf(false)

    fun openReview(reservationId: String) { reviewingReservationId = reservationId }
    fun closeReview() { reviewingReservationId = null }

    //Assuming the reviews will be stored in database still not sure how it should be implemented
    fun submitReview(
        reservationId: String,
        campsiteId: String,
        entrantId: String,
        rating: Int,
        text: String
    ) {
        if (isSubmittingReview) return
        isSubmittingReview = true
        val review = mapOf(
            "rating" to rating,
            "text" to text,
            "entrantID" to entrantId,
            "reservationID" to reservationId
        )
        /** Need to ask about where reviews is kept
        listingsRef.document(campsiteId).collection("Reviews").add(review)
            .addOnSuccessListener {
                isSubmittingReview = false
                reviewingReservationId = null
            }
            .addOnFailureListener {
                isSubmittingReview = false
            }
        */
    }
}
