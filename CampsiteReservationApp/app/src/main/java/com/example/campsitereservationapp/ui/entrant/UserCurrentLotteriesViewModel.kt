package com.example.campsitereservationapp.ui.entrant

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.firestore.FieldValue

class UserCurrentLotteriesViewModel: DatabaseModel() {

    var ongoingLotteries = mutableStateListOf<Lottery>()
    var wonLotteries = mutableStateListOf<Lottery>()

    val lotteries: List<Lottery> get() = _lotteries
    val listings: List<Listing> get() = _listings

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

    fun leaveLottery(lottery: Lottery, profileID: String){
        lotteriesRef.document(lottery.id).update(
            "waitlist", FieldValue.arrayRemove(profileID)
        )
    }

}