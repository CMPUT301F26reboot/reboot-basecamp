package com.example.campsitereservationapp.ui.Owner

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



    /** takes in lottery params,creates lottery ID, then adds to database*/
    fun createAndAddLottery(
        dates: List<Date>,
        start: Date,
        end:Date,
        campsiteIDs: List<String>,
        ownerID: String
    ){

        //create ID and add to database
        val lottery = Lottery(dates,start,end,campsiteIDs)
        lottery.id = ownerID
        addLottery(lottery)

    }


}