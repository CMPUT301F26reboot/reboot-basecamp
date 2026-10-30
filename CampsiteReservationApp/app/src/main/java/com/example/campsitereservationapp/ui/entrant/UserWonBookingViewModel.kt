package com.example.campsitereservationapp.ui.entrant

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ListingViewModel
import com.google.firebase.firestore.FieldValue
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

/**
 * ViewModel backing [UserWonBookingScreen] (US 01.04.01, 01.04.03).
 * Extends [ListingViewModel] to reuse its live Firestore-backed listing/lottery/reservation
 * lists; this class only adds the state specific to this screen (which dates are picked, and
 * the book/decline write operations).
 *
 * Outstanding issues:
 *  - [onBookClicked] does not create a Reservation yet, so US 01.05.01 is not fully implemented.
 */

class UserWonBookingViewModel : ListingViewModel() {

    private var lotteryId by mutableStateOf<String?>(null)
    private var campsiteName by mutableStateOf<String?>(null)

    /** Dates the user has tapped in the calendar, before intersecting with [bookableDays]. */
    private var picked by mutableStateOf<Set<LocalDate>>(emptySet())

    /** True while the decline write to Firestore is in flight. */
    var isDeclining by mutableStateOf(false)
        private set

    /** True once decline succeeds; the screen should navigate away when this flips. */
    var isFinished by mutableStateOf(false)
        private set

    /** Message to show in the snackbar, or null if there's nothing to report. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** True until both the matching lottery and campsite have been found in the live lists. */
    val isLoading: Boolean
        get() = findLottery() == null || findCampsite() == null

    /** The campsite the user won a slot for, or null while still loading. */
    val campsite: Listing?
        get() = findCampsite()

    /** Days shown green in the calendar: open in the lottery and not already reserved. */
    val bookableDays: Set<LocalDate>
        get() {
            val lottery = findLottery() ?: return emptySet()
            val campsite = findCampsite() ?: return emptySet()
            return bookableDays(lottery, campsite)
        }

    /** Days shown dark in the calendar. Picks that became unavailable are dropped. */
    val selectedDays: Set<LocalDate>
        get() = picked.intersect(bookableDays)

    /** Month the calendar should open on: the earliest bookable day, if any. */
    val initialMonth: YearMonth?
        get() = bookableDays.minOrNull()?.let { YearMonth.from(it) }

    /** True once loading has finished and the lottery has no bookable days left. */
    val noDatesAvailable: Boolean
        get() = !isLoading && bookableDays.isEmpty()

    /** True when the current selection is valid enough to enable the Book button. */
    val canBook: Boolean
        get() = isValidSelection(selectedDays)

    /** Call once from the screen with the ids passed through navigation. Safe to call repeatedly. */
    fun start(lotteryId: String, campsiteName: String) {
        if (this.lotteryId == lotteryId && this.campsiteName == campsiteName) return
        this.lotteryId = lotteryId
        this.campsiteName = campsiteName
        picked = emptySet()
        isFinished = false
        errorMessage = null
    }

    /** Select or unselect a date. Dates that can't be booked are ignored. */
    fun toggleDay(day: LocalDate) {
        if (day !in bookableDays) return
        picked = if (day in picked) picked - day else picked + day
    }

    /**
     * Called when the user confirms the Book dialog.
     *
     * Outstanding: does not yet create a Reservation document or mark the lottery slot as
     * claimed (US 01.05.01). Currently, a no-op.
     */
    fun onBookClicked() {
    }

    /**
     * Called when the user confirms the Decline dialog (US 01.04.03).
     * Records the decline by adding [profileId] to the lottery's declined-ids field, and sets
     * [isFinished] once the write succeeds so the screen can navigate away.
     */
    fun declineOffer(profileId: String) {
        val id = lotteryId ?: return
        if (isDeclining || isFinished) return
        isDeclining = true
        errorMessage = null

        lotteriesRef.document(id)
            .update(DECLINED_FIELD, FieldValue.arrayUnion(profileId))
            .addOnSuccessListener {
                isDeclining = false
                isFinished = true
            }
            .addOnFailureListener {
                isDeclining = false
                errorMessage = "Couldn't decline the offer. Please try again."
            }
    }

    /** Dismisses the current [errorMessage] after the snackbar has shown it. */
    fun clearError() {
        errorMessage = null
    }

    /** Looks up the won lottery by its Firestore document id (see [Lottery.id]). */
    private fun findLottery(): Lottery? {
        val id = lotteryId ?: return null
        return _lotteries.firstOrNull { it.id == id }
    }

    /** Looks up the campsite by name, as in [ListingViewModel.getAvailability]. */
    private fun findCampsite(): Listing? {
        val name = campsiteName ?: return null
        return _listings.firstOrNull { it.name == name }
    }

    /**
     * Days already reserved at the campsite with id [campsiteId].
     *
     */
    private fun reservedDays(campsiteId: String): Set<LocalDate> =
        _reservations
            .filter { it.campsiteID == campsiteId }
            .flatMap { it.dates }
            .map { it.toLocalDay() }
            .toSet()

    /** Days [lottery] offers for [campsite], minus days already reserved there. */
    private fun bookableDays(lottery: Lottery, campsite: Listing): Set<LocalDate> {
        if (campsite.id !in lottery.campsiteIDs) return emptySet()
        val lotteryDays = lottery.dates.map { it.toLocalDay() }.toSet()
        return computeBookableDays(lotteryDays, reservedDays(campsite.id))
    }

    /** Current selection rule: at least one date must be picked. */
    private fun isValidSelection(days: Set<LocalDate>): Boolean = days.isNotEmpty()

    private companion object {
        const val DECLINED_FIELD = "declinedProfileIds"
    }
}

/** Firestore Timestamps carry a time of day, so compare by calendar day instead. */
internal fun Date.toLocalDay(): LocalDate =
    toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

/** Days the lottery offers, minus days already reserved at the campsite. */
internal fun computeBookableDays(
    lotteryDays: Set<LocalDate>,
    reservedDays: Set<LocalDate>
): Set<LocalDate> = lotteryDays - reservedDays
