/**
 * viewmodel of Owner Waitlist, all database accesses needed for the UI is done here
 */
package com.example.campsitereservationapp.ui.owner.WaitList

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.components.formatDateRange

class OwnerWaitlistViewModel: DatabaseModel(){


    /**
     * Takes in lottery ID, returns lottery object
     */
    fun getLottery(lotteryID:String): Lottery?{
        return _lotteries.find { it.id == lotteryID }

    }

    /**
     * Takes in lottery ID, returns formated String registration dates
     */
    fun getFormatedRegistrationWindowDates(lottery: Lottery?): String?{
        if (lottery == null) {return null}
        return formatDateRange(listOf(lottery.start,lottery.end))
    }

    /**
     * Takes in lottery ID, returns formated String booking dates
     */
    fun getFormatedBookingStayDatesDates(lottery: Lottery?): String?{
        if (lottery == null) {return null}
        return formatDateRange(lottery.dates)
    }

    /**
     * Takes in entrant ID, returns name, if profile doesn't have one just use profile ID
     */
    fun getEntrantName(entrantID: String): String {
        val profile = _profiles.find { it.id == entrantID || it.deviceId == entrantID }
        return profile?.name?.ifBlank { entrantID } ?: entrantID
    }

}