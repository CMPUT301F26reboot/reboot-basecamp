/**
 * This file holds the UI for the Admin Browse Profile Screen
 */
package com.example.campsitereservationapp.ui.Admin.BrowseProfile

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.ConfirmDeleteDialog
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.DeleteDialog
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.SearchBarComponent
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
@Composable
        /**
         * The Screen for Admin Browsing Profile,
         * basically a copy of AdminBrowseScreenProfile with modified card
         *
         * @param onBack navigate to previous screen
         */
fun AdminBrowseProfilesScreen(
    onBack:() -> Unit,
    viewModel: AdminBrowseProfileViewModel = viewModel(),
){
    val profiles = viewModel._searchResults
    val deletionType = remember { mutableStateOf("") }
    val showDeletedDialog = remember { mutableStateOf(false) }
    val showDeletedFailedDialog = remember { mutableStateOf(false) }
    // what is waiting for the admin to confirm: "Profile" or "Profile Picture", and whose
    val confirmDelete = remember { mutableStateOf<Pair<String, Profile>?>(null) }

    // show every profile once they load, and refresh the list when one is added or removed
    LaunchedEffect(viewModel.profileCount) {
        viewModel.refreshSearch()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BackButtonAndHeaderScreenComponent(
            title = "Browse Profiles",
            onBack = onBack
        )

        Spacer(Modifier.height(24.dp))

        SearchBarComponent(
            onSearch = { query ->
                viewModel.profileSearch(query)
            }
        )

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(profiles, key = {it.id}) { profile ->
                ProfileCard(
                    profile = profile,
                    onDeleteProfile = { confirmDelete.value = "Profile" to profile },
                    onDeleteProfilePicture = { confirmDelete.value = "Profile Picture" to profile }
                )
            }

        }

        confirmDelete.value?.let { (type, profile) ->
            ConfirmDeleteDialog(
                deletedType = type,
                name = if (type == "Profile") profile.name else "",
                onConfirm = {
                    confirmDelete.value = null
                    deletionType.value = type
                    if (type == "Profile") {
                        viewModel.profileDelete(
                            profile = profile,
                            onSuccess = { showDeletedDialog.value = true },
                            onFailure = { showDeletedFailedDialog.value = true },
                        )
                    } else {
                        viewModel.profilePictureDelete(
                            profile = profile,
                            onSuccess = { showDeletedDialog.value = true },
                            onFailure = { showDeletedFailedDialog.value = true },
                        )
                    }
                },
                onDismiss = { confirmDelete.value = null }
            )
        }

        //popup dialog on successful or failure on listing deletion
        DeleteDialog(
            deletedType = deletionType.value,
            showDeletedDialog = showDeletedDialog,
            showDeletedFailedDialog = showDeletedFailedDialog
        )

    }

}