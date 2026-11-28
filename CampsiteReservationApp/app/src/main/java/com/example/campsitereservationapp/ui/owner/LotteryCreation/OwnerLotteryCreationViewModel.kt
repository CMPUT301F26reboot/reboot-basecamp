/**
 * This file contains the ViewModel of the Owner Lottery Creation Screen
 */
package com.example.campsitereservationapp.ui.owner

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import java.util.Date
import kotlin.collections.List


/**
 * Creates and edits lotteries. A lottery can include other owners' campsites: those are saved as
 * invitations in [Lottery.pendingCampsiteIDs] and only join the lottery when their owner accepts,
 * so no owner's campsite is added without permission. Invited owners see the invitation on their
 * Owner Lotteries screen.
 */
class OwnerLotteryCreationViewModel: DatabaseModel(
){

    fun getOwnerListings(ownerID: String): List<Listing>{
        return _listings.filter { it.ownerID == ownerID }
    }

    /** Campsites belonging to other owners, which [ownerID] can invite to a lottery. */
    fun getOtherOwnersListings(ownerID: String): List<Listing> {
        return _listings.filter { it.ownerID != ownerID }
    }

    /** takes in lottery params, then adds to database. Firestore creates the lottery ID.
     * winnersToDraw and waitlistCap aren't fields on Lottery, so they are merged into the same document
     * @param ownerID the owner creating the lottery, saved as its creator
     * @param invitedCampsiteIDs other owners' campsites to invite; they join once accepted
     * @param onSuccess event call after the lottery is saved
     */
    fun createAndAddLottery(
        dates: List<Date>,
        title: String,
        start: Date,
        end: Date,
        campsiteIDs: List<String>,
        winnersToDraw: Int,
        waitlistCap: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        ownerID: String = "",
        invitedCampsiteIDs: List<String> = emptyList()
    ) {
        val lottery = Lottery(dates, start, end, campsiteIDs)
        lottery.title = title
        lottery.waitlistCapacity = waitlistCap
        lottery.lotteryWinners = winnersToDraw
        lottery.ownerID = ownerID
        lottery.pendingCampsiteIDs = invitedCampsiteIDs
        lottery.drawn = false
        lotteriesRef.add(lottery)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }




    }

    /** Returns the lottery with [lotteryId], or null until it has loaded (or if it was deleted). */
    fun getLottery(lotteryId: String): Lottery? = _lotteries.firstOrNull { it.id == lotteryId }

    /**
     * Saves the edited fields onto the existing lottery [lotteryId]. Only the fields the form
     * edits are changed, so the waitlist and drawn winners are kept.
     * @param pendingCampsiteIDs invitations still waiting for an answer, or null to leave them as they are
     * @param onSuccess event call after the lottery is saved
     */
    fun updateLottery(
        lotteryId: String,
        dates: List<Date>,
        title: String,
        start: Date,
        end: Date,
        campsiteIDs: List<String>,
        winnersToDraw: Int,
        waitlistCap: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        pendingCampsiteIDs: List<String>? = null
    ) {
        val changes = mutableMapOf<String, Any>(
            "title" to title,
            "dates" to dates,
            "start" to start,
            "end" to end,
            "campsiteIDs" to campsiteIDs,
            "lotteryWinners" to winnersToDraw,
            "waitlistCapacity" to waitlistCap
        )
        if (pendingCampsiteIDs != null) changes["pendingCampsiteIDs"] = pendingCampsiteIDs
        lotteriesRef.document(lotteryId).update(changes)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    /**
     * Cancels lottery [lotteryId] by marking it as cancelled. The lottery stays in Firestore and
     * shows under the owner's Cancelled tab.
     * @param onSuccess event call after the lottery is cancelled
     */
    fun cancelLottery(lotteryId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        lotteriesRef.document(lotteryId).update("cancelled", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

}

/**
 * The owner's own campsites, and invited campsites that were already accepted, go in
 * [Lottery.campsiteIDs]; every other invited campsite waits in [Lottery.pendingCampsiteIDs].
 *
 * @param ownIDs the creator's own campsites that are selected.
 * @param invitedIDs other owners' campsites that are selected.
 * @param acceptedIDs the lottery's current campsiteIDs, empty when creating.
 * @return the new campsiteIDs and pendingCampsiteIDs.
 */
internal fun splitCampsites(
    ownIDs: List<String>,
    invitedIDs: List<String>,
    acceptedIDs: List<String>
): Pair<List<String>, List<String>> {
    val (accepted, pending) = invitedIDs.partition { it in acceptedIDs }
    return Pair(ownIDs + accepted, pending)
}
