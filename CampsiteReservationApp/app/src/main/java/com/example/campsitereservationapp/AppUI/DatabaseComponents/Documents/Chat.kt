package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

/**
 * A message log from an owner to an entrant
 *
 * Keeps track of messages between an owner to an entrant to be displayed
 * Has functionality to add a message
 *
 */
class Chat (val owner: Profile, val entrant: Profile, ){
    var Logs = ArrayList<LogEntry>() //list of messages
}