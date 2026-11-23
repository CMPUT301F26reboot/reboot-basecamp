// Model for User Notifications. Handles the notification searching by keyword for specific user
// and marking notifications as viewed. KDocs written by Claude Opus 5.5, 2026/11/23

/**
 * UserNotificationsViewModel.kt
 *
 * ViewModel for the entrant's Notifications screen. Filters the live Notifications collection
 * (inherited from DatabaseModel) down to the current entrant, searches it by keyword, and
 * marks notifications as viewed.
 *
 * Outstanding issues:
 *  - markViewed sets one shared "viewed" field, so it marks the notification viewed for
 *    every recipient, not just the current entrant.
 */

package com.example.campsitereservationapp.ui.entrant.Notifications;

import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.firestore.FieldValue
import java.util.Date

/**
 * Holds the search results for [UserNotificationsScreen] and handles marking notifications viewed.
 */
class UserNotificationsViewModel: DatabaseModel(){

    /** The entrant's notifications that match the current search. Updated by [doSearch]. */
    var _searchResults = mutableStateListOf<Notifications>()

    /** Retrieves all notifications */
    val allNotifications: List<Notifications>
        get() = _notifications

    /**
     * Adds user profile to the viewedBy array in Firestore
     *
     * @param notificationId the Firestore document ID of the notification.
     * @param profileId the Firestore document ID of the profile the notification is attached to
     */
    fun markViewed(notificationId: String, profileId: String) {
        notificationsRef.document(notificationId).update("viewedBy", FieldValue.arrayUnion(profileId))
    }

    /**
     * Searches for notifications based on criteria
     *
     * Queries database based on keyword where recipientIds match entrantId
     *
     * @param keyword is string typed into search bar which is used to do a prefix query on the subject
     * and message of notifications
     * @param entrantId is a Firestore string id of the profile, to ensure that resulting notifications are
     * only those of the entrant
     *
     */
    fun doSearch(keyword: String, entrantId: String){
        _searchResults.clear()
        val all = allNotifications
            .filter{
                entrantId in it.recipientIds
            }
        for (notification in all){
            if(containsKeyword(keyword, notification)) {
                    _searchResults.add(notification)
            }
        }
    }

    /**
     * Checks whether a notification matches a search keyword, ignoring case.
     *
     * @param keyword the search text; an empty keyword always matches.
     * @param notification the notification to check.
     * @return true if the notification's subject or message starts with [keyword].
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