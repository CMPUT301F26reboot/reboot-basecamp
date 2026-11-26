/**
 * OwnerNotifLogViewModel.kt
 *
 * ViewModel for the owners Notifications screen. Filters the live Notifications collection
 * (inherited from DatabaseModel) down to the current owner, and searches it by keyword
 */

package com.example.campsitereservationapp.ui.owner.Notifications

import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel

/**
 * Holds the search results for [OwnerNotifLogScreen].
 */
class OwnerNotifLogViewModel: DatabaseModel(){

    /** The owners notifications that match the current search. Updated by [doSearch]. */
    var _searchResults = mutableStateListOf<Notifications>()

    /** Retrieves all notifications */
    val allNotifications: List<Notifications>
        get() = _notifications

    /** Retrieves all lotteries so we can get the lottery name */
    val allLotteries: List<Lottery>
        get() = _lotteries

    /** Gets the lottery name */
    fun getLotName(lotteryId: String): String{
        val lottery =  allLotteries.find {it.id == lotteryId}
        return lottery?.title ?: "No lottery found"
    }


    /**
     * Searches for notifications based on criteria
     *
     * Queries database based on keyword where senderIds match profileId
     *
     * @param keyword is string typed into search bar which is used to do a prefix query on the subject
     * and message of notifications
     * @param ownerId is a Firestore string id of the profile, to ensure that resulting notifications are
     * only those of the owner
     *
     */
    fun doSearch(keyword: String, ownerId: String){
        _searchResults.clear()
        val all = allNotifications
            .filter{
                ownerId in it.senderId
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