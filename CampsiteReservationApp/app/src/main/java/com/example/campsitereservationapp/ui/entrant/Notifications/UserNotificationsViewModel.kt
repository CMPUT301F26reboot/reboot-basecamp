package com.example.campsitereservationapp.ui.entrant.Notifications;

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import java.util.Date

class UserNotificationsViewModel: DatabaseModel(){
    /** Retrieves all notifications */
    val allNotifications: List<Notifications>
        get() = _notifications

    /** Returns this entrant's notifications, with date filter applied */
    fun filteredNotifications(entrantId: String): List<Notifications> {
        val today = Date()
        val all = allNotifications
            .filter{
                entrantId in it.recipientIds
            }
        return all
    }
}