package com.example.campsitereservationapp.ui.Admin.BrowseProfile

import androidx.compose.foundation.lazy.items
import com.example.campsitereservationapp.ui.Admin.BrowseCampsites.AdminBrowseCampsiteViewModel
import com.example.campsitereservationapp.ui.Admin.BrowseCampsites.CampsiteCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.SearchBarComponent
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import com.example.campsitereservationapp.ui.components.CustomDialogScreenComponent
@Composable
        /**
         * UI for Admin Profile Search
         */
fun AdminBrowseProfilesScreen(
    onBack:() -> Unit,
    viewModel: AdminBrowseProfileViewModel = viewModel(),
){
    val profiles = viewModel._searchResults
    var showDeletedDialog by remember { mutableStateOf(false) }
    var showDeletedFailedDialog by remember { mutableStateOf(false) }


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
                    onDelete = {
                        viewModel.profileDelete(
                            profile = profile,
                            onSuccess = { showDeletedDialog = true },
                            onFailure = { showDeletedFailedDialog = true },
                        )
                    },
                )
            }

        }

        //popup dialog on successful or failure on listing deletion
        if (showDeletedDialog) {
            CustomDialogScreenComponent(
                onDismissRequest = {
                    showDeletedDialog = false
                },
                title = "Profile Deleted"
            )
        } else if (showDeletedFailedDialog)
        {
            CustomDialogScreenComponent(
                onDismissRequest = {
                    showDeletedFailedDialog = false
                },
                title = "Error: Deletion Failure",
            )
        }

    }

}