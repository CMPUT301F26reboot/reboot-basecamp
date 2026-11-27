package com.example.campsitereservationapp.ui.Admin.AdminMessage

import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message

class AdminMessageLogViewModel: DatabaseModel() {
    var messages = mutableStateListOf<Message>()
    /**
     * Sorts and returns all messages
     */
    fun reloadMessages(){
        messages.clear()
        for(message in _messages){
            messages.add(message)
        }
        messages.sortByDescending { it.timestamp }
    }

    /**
     * Gets the name of a profile based on a profile id
     *
     * @param [profileID] document id of the profile you want to get the name of
     */
    fun getName(profileID: String): String{
        for(profile in _profiles){
            if(profile.id == profileID){
                return profile.name
            }
        }
        return ""
    }
}