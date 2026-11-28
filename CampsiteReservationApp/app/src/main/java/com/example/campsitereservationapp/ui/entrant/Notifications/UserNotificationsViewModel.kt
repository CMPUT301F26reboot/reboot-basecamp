// Model for User Notifications. Handles the notification searching by keyword for specific user,
// marking notifications as viewed, and turning notifications on or off. KDocs written by Claude Opus 5.5, 2026/11/23

/**
 * UserNotificationsViewModel.kt
 *
 * ViewModel for the entrant's Notifications screen. Filters the live Notifications collection
 * (inherited from DatabaseModel) down to the current entrant, searches it by keyword,
 * marks notifications as viewed, and saves whether the entrant wants phone notifications
 * (US 01.10.01).
 */

package com.example.campsitereservationapp.ui.entrant.Notifications;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.firestore.FieldValue

/**
 * Holds the search results for [UserNotificationsScreen], handles marking notifications viewed,
 * and turns the entrant's notifications on or off.
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

    /** Message to show in a snackbar, or null if there's nothing to report. */
    var message by mutableStateOf<String?>(null)
        private set

    /** Clears [message] once the snackbar has shown it. */
    fun clearMessage() {
        message = null
    }

    /**
     * Checks whether the entrant has notifications turned on. Reads from the live Profiles
     * collection, so it always matches the toggle on the profile screen.
     *
     * @param profileId the Firestore document ID of the entrant's profile.
     * @return the saved setting, or true if the profile hasn't loaded yet.
     */
    fun notificationsEnabled(profileId: String): Boolean {
        val profile = _profiles.find { it.id == profileId }
        // profiles start with notifications on, so default to on until it loads
        return profile?.notificationsEnabled ?: true
    }

    /**
     * Turns the entrant's notifications on or off. The toggle updates on its own once the
     * profile changes, and [message] is set if the save fails.
     *
     * @param profileId the Firestore document ID of the entrant's profile.
     * @param enabled true to turn notifications on, or false to turn them off.
     */
    fun updateNotifications(profileId: String, enabled: Boolean) {
        profilesRef.document(profileId).update("notificationsEnabled", enabled)
            .addOnFailureListener {
                message = "Couldn't save your changes. Please try again."
            }
    }

    /**
     * Searches for notifications based on criteria
     *
     * Queries database based on keyword where recipientIds match entrantId
     *
     * @param keyword is string typed into search bar which is used to do a contains on the subject
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