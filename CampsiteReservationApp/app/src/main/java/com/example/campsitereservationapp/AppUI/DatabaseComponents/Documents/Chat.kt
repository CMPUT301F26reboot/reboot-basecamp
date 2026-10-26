package com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents

/**
 * A message log from an owner to an entrant
 *
 * Keeps track of messages between an owner to an entrant to be displayed
 * Has functionality to add a message
 *
 */
class Chat (owner: Profile, entrant: Profile, ){
    var Logs = ArrayList<LogEntry>() //list of messages

    /**
     * adds a message to the chat
     */
    fun add(message: LogEntry){ //adding message to chat
        Logs.add(message)
    }
}