/**
 * This file contains the screen to view a campsite's images
 */
package com.example.campsitereservationapp.ui.Admin.CampsiteImages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.ConfirmDeleteDialog
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.DeleteDialog
import com.example.campsitereservationapp.ui.owner.CampsiteDetails.CampsiteDetailsCard

@Composable
        /**
         * Screen to view images of a campsite,
         * code is a modified version of Owner campsite details screen
         *
         * @param listingID ID of listing/campsite clicked
         * @param onBack navigate to previous screen
         */
fun CampsiteImagesScreen(
    listingID: String,
    onBack: () -> Unit,
    viewModel: CampsiteImagesViewModel = viewModel()
) {
    val listingDoc = viewModel.getListing(listingID)
    val photos = viewModel.getImages(listingID)
    val showDeletedDialog = remember { mutableStateOf(false) }
    val showDeletedFailedDialog = remember { mutableStateOf(false) }
    // position of the photo waiting for the admin to confirm its deletion
    val confirmDeleteIndex = remember { mutableStateOf<Int?>(null) }

    //top header, taken from owner campsite details screen
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        "Campsite images",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { padding ->

        //Images being displayed, there are 2 columns of images
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                if (listingDoc != null){
                    CampsiteDetailsCard(listingDoc)
                }
            }
            //AI Usage - Copilot, items() is prompted
            //Prompt, how do I display my 2 photo cards side by side with items()
            itemsIndexed(photos.chunked(2)) { row, photoPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    photoPair.forEachIndexed { column, photo ->
                        Box(modifier = Modifier.weight(1f)) {
                            PhotoCard(
                                photo = photo,
                                // position in photoUrls: 2 photos per row
                                onDelete = { confirmDeleteIndex.value = row * 2 + column }
                            )
                        }
                    }
                    // Fill remaining space if the last row only has 1 photo
                    if (photoPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

            }
        }

        confirmDeleteIndex.value?.let { index ->
            ConfirmDeleteDialog(
                deletedType = "Image",
                name = "",
                onConfirm = {
                    confirmDeleteIndex.value = null
                    viewModel.deleteImage(
                        listingId = listingID,
                        index = index,
                        onSuccess = { showDeletedDialog.value = true },
                        onFailure = { showDeletedFailedDialog.value = true }
                    )
                },
                onDismiss = { confirmDeleteIndex.value = null }
            )
        }

        //popup dialog on successful or failure on listing deletion
        DeleteDialog(
            deletedType = "Image",
            showDeletedDialog = showDeletedDialog,
            showDeletedFailedDialog = showDeletedFailedDialog
        )
    }
}

