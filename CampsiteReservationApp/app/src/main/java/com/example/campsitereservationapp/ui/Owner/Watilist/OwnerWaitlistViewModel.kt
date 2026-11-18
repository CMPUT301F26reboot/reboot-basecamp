package com.example.campsitereservationapp.ui.Owner.Watilist;

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel


class OwnerWaitlistViewModel: DatabaseModel(){




    //get lottery, returns null if not found
    fun getLottery(lotteryID:String): Lottery?{
        return _lotteries.find { it.id == lotteryID }

    }

}
