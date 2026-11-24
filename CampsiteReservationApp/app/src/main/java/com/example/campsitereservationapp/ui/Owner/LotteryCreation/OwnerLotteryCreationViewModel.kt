package com.example.campsitereservationapp.ui.owner

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import java.util.Date
import kotlin.collections.List


class OwnerLotteryCreationViewModel: DatabaseModel(
){

    fun getOwnerListings(ownerID: String): List<Listing>{
        return _listings.filter { it.ownerID == ownerID }
    }

    /** takes in lottery params, then adds to database. Firestore creates the lottery ID.
     * winnersToDraw and waitlistCap aren't fields on Lottery, so they are merged into the same document
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
        onFailure: () -> Unit
    ) {
        val lottery = Lottery(dates, start, end, campsiteIDs)
        lottery.title = title
        lottery.waitlistCapacity = waitlistCap
        lottery.lotteryWinners = winnersToDraw
        lotteriesRef.add(lottery)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }




    }

    /** Returns the lottery with [lotteryId], or null until it has loaded (or if it was deleted). */
    fun getLottery(lotteryId: String): Lottery? = _lotteries.firstOrNull { it.id == lotteryId }

    /**
     * Saves the edited fields onto the existing lottery [lotteryId]. Only the fields the form
     * edits are changed, so the waitlist and drawn winners are kept.
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
        onFailure: () -> Unit
    ) {
        val changes = mapOf(
            "title" to title,
            "dates" to dates,
            "start" to start,
            "end" to end,
            "campsiteIDs" to campsiteIDs,
            "lotteryWinners" to winnersToDraw,
            "waitlistCapacity" to waitlistCap
        )
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