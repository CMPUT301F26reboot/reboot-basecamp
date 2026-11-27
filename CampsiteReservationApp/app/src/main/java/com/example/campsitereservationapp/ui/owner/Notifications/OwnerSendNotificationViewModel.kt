/**
 * OwnerSendNotificationViewModel.kt
 *
 * ViewModel for the owners Send Notifications screen. Obtains the lottery from a lottery ID
 * to help with notification creation, as well as sends the notification given the neccessary information
 * through parameters
 */

package com.example.campsitereservationapp.ui.owner.Notifications

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import java.util.Date


/**
 * Looks up the lottery and sends the owner's notification [OwnerSendNotificationScreen].
 */
class OwnerSendNotificationViewModel: DatabaseModel(){

    /**
     * Finds a lottery by id.
     *
     * @param lotteryId id of the [Lottery] to look up.
     * @return the matching lottery, or null if no loaded lottery has that id.
     */
    fun getLottery(lotteryId: String): Lottery? {
        return _lotteries.firstOrNull { it.id == lotteryId }
    }


    /**
     * Creations the notification and sends it to all the entrants of a lottery.
     * Called in OwnerSendNotificationScreen. Calls getLottery passing the lotteryId
     * in order to obtain the waitlist (recipients)
     *
     * @param subject String that is the title of the notification.
     * @param message String that is the message contents of the notification.
     * @param ownerId String the ID of the owner
     * @param lotteryId String the ID of the lottery that the waitlist is apart of
     * @param onSuccess function to be called if the notification is created successfully
     * @param onFailure function to be called if the notification fails to be created
     */
    fun onSend(subject: String, message: String, ownerId: String, lotteryId: String,
               onSuccess: () -> Unit, onFailure: () -> Unit){

        val lottery = getLottery(lotteryId)
        if (lottery == null) {
            onFailure()
            return
        }

        // NOTIFICATION: create notification for owner notification message
        val notificationInfo = mapOf(
            "lotteryId" to lotteryId,
            "message" to message,
            "recipientIds" to lottery.waitlist,
            "reservationId" to "",
            "senderId" to ownerId,
            "subject" to subject,
            "timestamp" to Date(),
            "type" to "ownerNotif",
            "viewedBy" to emptyList<String>()
        )
        notificationsRef.add(notificationInfo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

}