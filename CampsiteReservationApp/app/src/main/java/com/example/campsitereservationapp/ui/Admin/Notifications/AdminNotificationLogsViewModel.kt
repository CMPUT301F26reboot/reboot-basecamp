/**
 * AdminNotificationLogsViewModel.kt
 *
 * ViewModel for the admins Notifications screen. Filters the live Notifications collection
 * (inherited from DatabaseModel) and searches it by keyword
 */

package com.example.campsitereservationapp.ui.Admin.Notifications

import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel

/**
 * Holds the search results for [AdminNotificationLogsScreen].
 */
class AdminNotificationLogsViewModel: DatabaseModel(){

    /** The notifications that match the current search. Updated by [doSearch]. */
    var _searchResults = mutableStateListOf<Notifications>()

    /** Retrieves all notifications */
    val allNotifications: List<Notifications>
        get() = _notifications

    /** Retrieves all profiles so we can get the sender name */
    val allProfiles: List<Profile>
        get() = _profiles

    /** Finds the name of the given profileId
     * @param profileId given id of the sender the notification belongs to
     * @return the matching profile name, or "System"
     */
    fun getSenderName(profileId: String): String{
        val profile =  allProfiles.find {it.id == profileId || it.deviceId == profileId}
        return profile?.name ?: "System"
    }

    /**
     * Deletes a notification from the database.
     *
     * @param notificationId the Firestore ID of the notification to delete.
     */
    fun deleteNotification(notificationId: String) {
        notificationsRef.document(notificationId).delete()
    }

    /**
     * Searches for notifications based on criteria
     *
     * Queries database based on keyword
     *
     * @param keyword is string typed into search bar which is used to do a query on the subject
     * and message of notifications
     *
     */
    fun doSearch(keyword: String){
        _searchResults.clear()
        for (notification in allNotifications){
            if(containsKeyword(keyword, notification)) {
                _searchResults.add(notification)
            }
        }
        // Sort by descending timestamp so that newest notifications show first
        _searchResults.sortByDescending{ it.timestamp }
    }

    /**
     * Checks whether a notification matches a search keyword, ignoring case.
     *
     * @param keyword the search text; an empty keyword always matches.
     * @param notification the notification to check.
     * @return true if the notification's subject or message contains [keyword].
     */
    internal fun containsKeyword(keyword: String, notification: Notifications): Boolean{
        if(keyword.isEmpty())
            return true

        val subjectMatch = notification.subject.contains(keyword, ignoreCase = true)
        val messageMatch = notification.message.contains(keyword, ignoreCase = true)

        // Return true if either match
        return subjectMatch || messageMatch
    }
}