/**
 * This file contains the popup dialog after every deletion in
 * Profile,Campsite, and images Screens
 *
 * Copied from OwnerLotteryCreation
 */
package com.example.campsitereservationapp.ui.Admin.BrowseComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.font.FontWeight
import com.example.campsitereservationapp.ui.components.CustomDialogScreenComponent
import com.example.campsitereservationapp.ui.theme.DeleteRed

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

/**
 * Asks the admin to confirm before something is deleted, since deleting can't be undone.
 *
 * @param deletedType what is being deleted, such as "Campsite", "Profile" or "Image".
 * @param name name of the item, shown in the message; blank to leave it out.
 * @param onConfirm called when the admin taps "Delete".
 * @param onDismiss called when the admin taps "Cancel" or closes the dialog.
 */
@Composable
fun ConfirmDeleteDialog(
    deletedType: String,
    name: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val target = if (name.isBlank()) "this ${deletedType.lowercase()}" else "\"$name\""
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete $deletedType?") },
        text = { Text("Are you sure you want to delete $target? This can't be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = DeleteRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
