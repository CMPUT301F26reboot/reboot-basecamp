package com.example.campsitereservationapp.ui.entrant.WonBooking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ListingViewModel
import com.google.firebase.firestore.FieldValue
import java.time.YearMonth
import java.util.Calendar
import java.util.Date

/**
 * UserWonBookingViewModel.kt
 *
 * Holds the state for [UserWonBookingScreen]: which campsite and lottery are being shown, which
 * date the entrant picked, and whether they've accepted the rules. Also does the Firestore write
 * for declining an offer.
 *
 *
 * Outstanding issues:
 *  - reservedDays() does not check a reservation's status, so a cancelled reservation would still block that date.
 */
class UserWonBookingViewModel : ListingViewModel() {

    private var lotteryId by mutableStateOf<String?>(null)
    private var campsiteName by mutableStateOf<String?>(null)

    /** Dates the user has tapped in the calendar, before intersecting with [bookableDays]. */
    private var picked by mutableStateOf<Set<Date>>(emptySet())

    /** Whether the entrant has checked "Accept rules & policies". */
    var rulesAccepted by mutableStateOf(false)
        private set

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
    val bookableDays: Set<Date>
        get() {
            val lottery = findLottery() ?: return emptySet()
            val campsite = findCampsite() ?: return emptySet()
            return bookableDays(lottery, campsite)
        }

    /** Days shown dark in the calendar. Picks that became unavailable are dropped. */
    val selectedDays: Set<Date>
        get() = picked.intersect(bookableDays)

    /** Month the calendar should open on: the earliest bookable day, if any. */
    val initialMonth: YearMonth?
        get() = bookableDays.minOrNull()?.let { date ->
            val cal = Calendar.getInstance()
            cal.time = date
            YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        }

    /** True once loading has finished and the lottery has no bookable days left. */
    val noDatesAvailable: Boolean
        get() = !isLoading && bookableDays.isEmpty()

    /**
     * True when the Book button should be enabled: a date must be picked, and if the campsite
     * requires it, the rules checkbox must be checked too.
     */
    val canBook: Boolean
        get() = isValidSelection(selectedDays) && (findCampsite()?.rulesAcceptanceRequired != true || rulesAccepted)

    /** Call once from the screen with the ids passed through navigation. Safe to call repeatedly. */
    fun start(lotteryId: String, campsiteName: String) {
        if (this.lotteryId == lotteryId && this.campsiteName == campsiteName) return
        this.lotteryId = lotteryId
        this.campsiteName = campsiteName
        picked = emptySet()
        rulesAccepted = false
        isFinished = false
        errorMessage = null
    }

    /** Select or unselect a date. Dates that can't be booked are ignored. */
    fun toggleDay(day: Date) {
        if (day !in bookableDays) return
        picked = if (day in picked) picked - day else picked + day
    }

    /** Sets whether the entrant has accepted the campsite's rules & policies. */
    fun onRulesAcceptedChanged(accepted: Boolean) {
        rulesAccepted = accepted
    }

    /** Called when the user confirms the Book dialog. Doesn't save anything yet (see Outstanding issues above). */
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

    /** Finds the lottery the entrant won, using its id. */
    private fun findLottery(): Lottery? {
        val id = lotteryId ?: return null
        return _lotteries.firstOrNull { it.id == id }
    }

    /** Finds the campsite by name among the live listings. */
    private fun findCampsite(): Listing? {
        val name = campsiteName ?: return null
        return _listings.firstOrNull { it.name == name }
    }

    /** Days already booked at the campsite with id [campsiteId]. */
    private fun reservedDays(campsiteId: String): Set<Date> =
        _reservations
            .filter { it.campsiteID == campsiteId }
            .flatMap { it.dates }
            .map { it.toDayOnly() }
            .toSet()

    /** Days [lottery] offers for [campsite], minus days already reserved there. */
    private fun bookableDays(lottery: Lottery, campsite: Listing): Set<Date> {
        if (campsite.id !in lottery.campsiteIDs) return emptySet()
        val lotteryDays = lottery.dates.map { it.toDayOnly() }.toSet()
        return computeBookableDays(lotteryDays, reservedDays(campsite.id))
    }

    /** Current selection rule: at least one date must be picked. */
    private fun isValidSelection(days: Set<Date>): Boolean = days.isNotEmpty()

    private companion object {
        const val DECLINED_FIELD = "declinedProfileIds"
    }
}

/** Normalizes [Date] to midnight local time, so two Dates on the same calendar day compare equal. */
internal fun Date.toDayOnly(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

/** Days the lottery offers, minus days already reserved at the campsite. */
internal fun computeBookableDays(
    lotteryDays: Set<Date>,
    reservedDays: Set<Date>
): Set<Date> = lotteryDays - reservedDays
