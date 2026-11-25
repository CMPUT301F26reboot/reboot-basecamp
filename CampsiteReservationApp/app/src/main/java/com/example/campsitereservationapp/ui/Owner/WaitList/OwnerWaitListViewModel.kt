package com.example.campsitereservationapp.ui.owner.WaitList

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.components.formatDateRange

class OwnerWaitlistViewModel: DatabaseModel(){




    //get lottery, returns null if not found
    fun getLottery(lotteryID:String): Lottery?{
        return _lotteries.find { it.id == lotteryID }

    }

    //get registration dates in formated string
    fun getFormatedRegistrationWindowDates(lottery: Lottery?): String?{
        if (lottery == null) {return null}
        return formatDateRange(listOf(lottery.start,lottery.end))
    }

    //get booking dates in formated string
    fun getFormatedBookingStayDatesDates(lottery: Lottery?): String?{
        if (lottery == null) {return null}
        return formatDateRange(lottery.dates)
    }

    // gets  profile name by entrant ID
    fun getEntrantName(entrantID: String): String {
        val profile = _profiles.find { it.id == entrantID || it.deviceId == entrantID }
        return profile?.name?.ifBlank { entrantID } ?: entrantID
    }

}