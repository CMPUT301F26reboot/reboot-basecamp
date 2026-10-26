package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

import java.util.Date

class Lottery (var dates: ArrayList<Date>, val start: Date, val end: Date, var campsiteNames: ArrayList<String>){
    var waitlist = ArrayList<Profile>()
    fun addEntrant(entrant: Profile){
        waitlist.add(entrant)
    }
}