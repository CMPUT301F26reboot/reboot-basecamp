package com.example.campsitereservationapp.ui.entrant

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel

class UserCurrentLotteriesViewModel: DatabaseModel() {

    var ongoingLotteries = mutableStateListOf<Lottery>()
    var wonLotteries = mutableStateListOf<Lottery>()

    fun start(profileID: String){
        getOngoingLotteries(profileID)
        getWonLotteries(profileID)
    }

    internal fun getOngoingLotteries(profileID: String){
        for(lottery in _lotteries){
            if(profileID in lottery.waitlist){
                ongoingLotteries.add(lottery)
            }
        }
    }

    internal fun getWonLotteries(profileID: String){
        for(lottery in _lotteries){
            if(profileID in lottery.selectedlist){
                wonLotteries.add(lottery)
            }
        }
    }

    fun getLotteryImage(lottery: Lottery): String{
        val listingID = lottery.campsiteIDs[0]
        var listingImg = ""
        for(listing in _listings){
            if(listing.id == listingID){
                listingImg = listing.photoUrls[0]
                break
            }
        }
        return listingImg
    }


}