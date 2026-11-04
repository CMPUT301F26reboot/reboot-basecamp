package com.example.campsitereservationapp.ui.Owner

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.firestore.SetOptions
import java.util.Date

class OwnerLotteryCreationViewModel : DatabaseModel() {

    /** True while a save is in flight, so a double tap can't create two lotteries. */
    private var isCreating = false

    fun getOwnerListings(ownerID: String): List<Listing> {
        return _listings.filter { it.ownerID == ownerID }
    }

    /**
     * Saves a new lottery. Firestore generates its ID.
     *
     * Winners, waitlist cap and per-campsite dates aren't fields on [Lottery], so they are
     * merged into the same document in one atomic write.
     *
     * @param availability the picked days for each selected campsite, keyed by campsite ID.
     */
    fun createAndAddLottery(
        start: Date,
        end: Date,
        availability: Map<String, Set<Date>>,
        winnersToDraw: Int,
        waitlistCap: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit = {}
    ) {
        if (isCreating) return
        isCreating = true

        val lottery = Lottery(
            dates = availability.values.flatten().distinct(),
            start = start,
            end = end,
            campsiteIDs = availability.keys.toList()
        )
        val extraFields = mapOf(
            WINNERS_FIELD to winnersToDraw,
            WAITLIST_CAP_FIELD to waitlistCap,
            CAMPSITE_DATES_FIELD to availability.mapValues { (_, days) -> days.sorted() }
        )

        val document = lotteriesRef.document()
        db.batch()
            .set(document, lottery)
            .set(document, extraFields, SetOptions.merge())
            .commit()
            .addOnSuccessListener {
                isCreating = false
                onSuccess()
            }
            .addOnFailureListener {
                isCreating = false
                onFailure()
            }
    }

    private companion object {
        const val WINNERS_FIELD = "winnersToDraw"
        const val WAITLIST_CAP_FIELD = "waitlistCap"
        const val CAMPSITE_DATES_FIELD = "campsiteDates"
    }
}