/**
 * This file contains the popup dialog after every deletion in
 * Profile,Campsite, and images Screens
 *
 * Copied from OwnerLotteryCreation
 */
package com.example.campsitereservationapp.ui.Admin.BrowseComponents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.example.campsitereservationapp.ui.components.CustomDialogScreenComponent

@Composable
        /**
         * Popup dialog to show confirmation if deletion was successful or not
         * @param deletedType [String] what is type is being deleted
         * @param showDeletedDialog MutableState variable altered set to true
         * after successful deletion
         * @param showDeletedFailedDialog MutableState variable altered set to true
         * after failed deletion
         */
fun DeleteDialog(
    deletedType: String,
    showDeletedDialog: MutableState<Boolean>,
    showDeletedFailedDialog: MutableState<Boolean>
    ){
    if (showDeletedDialog.value) {
        CustomDialogScreenComponent(
            onDismissRequest = {
                showDeletedDialog.value = false
            },
            title = "$deletedType deleted"
        )
    } else if (showDeletedFailedDialog.value)
    {
        CustomDialogScreenComponent(
            onDismissRequest = {
                showDeletedFailedDialog.value = false
            },
            title = "Error: Deletion Unsuccessful"
        )
    }

}