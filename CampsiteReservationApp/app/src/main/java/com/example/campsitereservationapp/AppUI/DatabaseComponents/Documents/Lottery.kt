package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import java.util.Date

class Lottery (dates: ArrayList<Date>, start: Date, end: Date, campsiteName: String){
    var waitlist = ArrayList<Profile>()
    fun addEntrant(entrant: Profile){
        waitlist.add(entrant)
    }
}