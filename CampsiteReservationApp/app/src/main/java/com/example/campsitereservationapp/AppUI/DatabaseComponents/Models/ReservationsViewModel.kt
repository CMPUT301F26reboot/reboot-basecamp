package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.firebase.firestore.pipeline.Expression.Companion.documentId
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

enum class ReservationFilter { All, Reserved, Past, Cancelled }

/**
 * Backs the User Reservations screen (US 01.06.01, 01.06.02, 01.07.01).
 *
 * Holds the shared list, the filter selection, and the cancel logic. The entrant screen
 * extends this and adds its own filtering plus review handling. An owner screen could
 * extend it later and filter by owner instead.
 */

open class ReservationsViewModel: DatabaseModel() {

    var filter by mutableStateOf(ReservationFilter.All)

    /** Called by the filter buttons when the user picks a new filter. */
    fun onFilterSelected(f: ReservationFilter) {
        filter = f
    }

    val allReservations: List<Reservation>
        get() = _reservations

    /** Finds the Listing a reservation points at, or null if it was deleted. */
    fun listingFor(reservation: Reservation): Listing? =
        _listings.firstOrNull { it.id == reservation.campsiteID }

    // Gemini 2026/11/25
    // Q. "I want to print all the information in a reservation document to a csv in kotlin android studio.
    // Just say we pass reservation as type Reservation where a databasemodel syncs firestore data of the
    // reservation document into reservation of type Reservation. one of the fields is reservation.dates, which
    // which is an array of timestamps, how can I format them to just yyyy-MM-dd so they are not too long in the
    // csv file?

    fun exportToCsv(reservation: Reservation, outputFile: File) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Join multiple dates with a semicolon so they stay cleanly inside one CSV cell
        val formattedDates = reservation.dates.joinToString("; ") { date ->
            dateFormat.format(date)
        }

        val headers = listOf(
            "Reservation ID",
            "Camp Name",
            "Campsite ID",
            "Entrant ID",
            "Dates",
            "Status",
            "Rated"
        )

        val row = listOf(
            reservation.id,
            reservation.campName,
            reservation.campsiteID,
            reservation.entrantID,
            formattedDates,
            reservation.status,
            reservation.rated.toString()
        )

        csvWriter().writeAll(listOf(headers, row), outputFile)
    }

}

