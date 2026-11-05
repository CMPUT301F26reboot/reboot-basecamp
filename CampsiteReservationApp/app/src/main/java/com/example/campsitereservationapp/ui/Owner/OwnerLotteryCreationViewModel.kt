package com.example.campsitereservationapp.ui.Owner

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.firestore.SetOptions
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
        start: Date,
        end: Date,
        campsiteIDs: List<String>,
        winnersToDraw: Int,
        waitlistCap: Int,
    ) {
        val lottery = Lottery(dates, start, end, campsiteIDs)
        lottery.waitlistCapacity = waitlistCap
        lottery.lotteryWinners = winnersToDraw
        addLottery(lottery)





    }

}