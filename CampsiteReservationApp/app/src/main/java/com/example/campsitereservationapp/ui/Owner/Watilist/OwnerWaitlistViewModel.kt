package com.example.campsitereservationapp.ui.Owner.Watilist;

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.Owner.LotteryCreation.components.formatDateRange

class OwnerWaitlistViewModel: DatabaseModel(){




    //get lottery, returns null if not found
    fun getLottery(lotteryID:String): Lottery?{
        return _lotteries.find { it.id == lotteryID }

    }

    //uses date formater from OwnerLotteryCreationViewModel to get formated dates
    fun getFormatedDates(lottery: Lottery?): String?{
        if (lottery == null) {return null}
        return formatDateRange(listOf(lottery.start,lottery.end))
    }

}
