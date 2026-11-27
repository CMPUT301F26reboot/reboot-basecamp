/**
 * This file holds the UI for Admin Browse Campsites,
 */
package com.example.campsitereservationapp.ui.Admin.BrowseCampsites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
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
 * UI for Admin Campsite Search
 * @param onBack navigate to previous screen
 * @param onImages navigate to images of campsite
 */
fun AdminBrowseCampsitesScreen(
    onBack:() -> Unit,
    onImages: (listingID: String) -> Unit,
    viewModel: AdminBrowseCampsiteViewModel = viewModel(),
){
    val listings = viewModel._searchResults
    val showDeletedDialog = remember { mutableStateOf(false) }
    val showDeletedFailedDialog = remember { mutableStateOf(false) }
    // campsite waiting for the admin to confirm its deletion
    val confirmDelete = remember { mutableStateOf<Listing?>(null) }

    // show every campsite once they load, and refresh the list when one is added or removed
    LaunchedEffect(viewModel.listingCount) {
        viewModel.refreshSearch()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BackButtonAndHeaderScreenComponent(
            title = "Browse Campsites",
            onBack = onBack
        )
        Spacer(Modifier.height(24.dp))
        SearchBarComponent(
            onSearch = { query ->
                viewModel.campsiteSearch(query)
            }
        )
        Spacer(Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listings, key = {it.id}) { listing ->
                CampsiteCard(
                    listing = listing,
                    onDelete = { confirmDelete.value = listing },
                    onImages = {onImages(listing.id)}
                )
            }

        }

        confirmDelete.value?.let { listing ->
            ConfirmDeleteDialog(
                deletedType = "Campsite",
                name = listing.name,
                onConfirm = {
                    confirmDelete.value = null
                    viewModel.campsiteDelete(
                        listing = listing,
                        onSuccess = { showDeletedDialog.value = true },
                        onFailure = { showDeletedFailedDialog.value = true },
                    )
                },
                onDismiss = { confirmDelete.value = null }
            )
        }

        //popup dialog on successful or failure on listing deletion
        DeleteDialog(
            deletedType = "Campsite",
            showDeletedDialog = showDeletedDialog,
            showDeletedFailedDialog = showDeletedFailedDialog
        )


    }

}