package com.example.campsitereservationapp.ui.owner.WaitList

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.components.formatDateRange

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

    // gets  profile name by entrant ID
    fun getEntrantName(entrantID: String): String {
        val profile = _profiles.find { it.id == entrantID || it.deviceId == entrantID }
        return profile?.name?.ifBlank { entrantID } ?: entrantID
    }

}