package com.example.campsitereservationapp.ui.entrant

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.firestore.FieldValue

/**
 * Provieds data and functionality to UserCurrentLotteriesScreen
 *
 * Manages a list of ongoing and won lotteries.
 * Also provides functions to get an image for a lottery and to leave a lottery
 *
 */
class UserCurrentLotteriesViewModel: DatabaseModel() {

    var ongoingLotteries = mutableStateListOf<Lottery>()
    var wonLotteries = mutableStateListOf<Lottery>()


    /**
     * Initializes lottrer lists
     *
     * Called at the start of composable to populate the ongoingLotteries and wonLotteries lists
     *
     * @param [profileID] is profileID of the entrant used to look up which lotteries they're in
     */
    fun start(profileID: String){
        getOngoingLotteries(profileID)
        getWonLotteries(profileID)
    }

    internal fun getOngoingLotteries(profileID: String){
        ongoingLotteries.clear()
        for(lottery in _lotteries){
            if(profileID in lottery.waitlist){
                ongoingLotteries.add(lottery)
            }
        }
    }

    internal fun getWonLotteries(profileID: String){
        wonLotteries.clear()
        for(lottery in _lotteries){
            if(profileID in lottery.selectedlist){
                wonLotteries.add(lottery)
            }
        }
    }

    /**
     * Gets an image for the lottery
     *
     * Gets the first image from the first listing encountered in the db
     * that is also within the lottery
     *
     * @param [lottery] is the lottery to get an image for
     */
    fun getLotteryImage(lottery: Lottery): String{
        var listingImg = ""
        for(listing in _listings){
            if(listing.id in lottery.campsiteIDs){
                listingImg = listing.photoUrls.first()
                if(listingImg.isNotEmpty())
                    break
            }
        }
        return listingImg
    }

    /**
     * Leaves the waitlist of a lottery
     *
     * Accesses the lottery document in the db and removes the entrant's
     * profileID from the waitlist
     *
     * @param [lottery] is the lottery to leave
     * @param [profileID] is the document id of the entrant's profile (not deviceID)
     */
    fun leaveLottery(lottery: Lottery, profileID: String){
        lotteriesRef.document(lottery.id).update(
            "waitlist", FieldValue.arrayRemove(profileID)
        )
    }

}