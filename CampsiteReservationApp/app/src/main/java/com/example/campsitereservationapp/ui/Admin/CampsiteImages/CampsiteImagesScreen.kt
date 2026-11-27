/**
 * This file contains the screen to view a campsite's images
 */
package com.example.campsitereservationapp.ui.Admin.CampsiteImages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.components.CustomDialogScreenComponent
import com.example.campsitereservationapp.ui.owner.CampsiteDetails.CampsiteDetailsCard
import com.example.campsitereservationapp.ui.theme.DeleteRed
import com.example.campsitereservationapp.utils.decodeBase64Image
import kotlin.collections.orEmpty

@Composable
fun CampsiteImagesScreen(
    listingID: String,
    onBack: () -> Unit,
    viewModel: CampsiteImagesViewModel = viewModel()
) {
    val listing_doc = viewModel.getListing(listingID)
    val photos = viewModel.getImages(listingID)
    var showDeletedDialog by remember { mutableStateOf(false) }
    var showDeletedFailedDialog by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                if (listing_doc != null){
                    CampsiteDetailsCard(listing_doc)
                }
            }
            //AI Usage - Copilot, items() is prompted
            //Prompt, how do I display my 2 photo cards side by side with items()
            items(photos.chunked(2)) { photoPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    photoPair.forEach { photo ->
                        Box(modifier = Modifier.weight(1f)) {
                            PhotoCard(
                                photo = photo,
                                onDelete = {
                                    viewModel.deleteImage(
                                        listingId = listingID,
                                        photoUrl = photo,
                                        onSuccess = { showDeletedDialog = true },
                                        onFailure = { showDeletedFailedDialog = true }
                                    )
                                }
                            )
                        }
                    }
                    // Fill remaining space if the last row only has 1 photo
                    if (photoPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                //popup dialog on successful or failure on listing deletion
                if (showDeletedDialog) {
                    CustomDialogScreenComponent(
                        onDismissRequest = {
                            showDeletedDialog = false
                        },
                        title = "Image Deleted"
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
    }
}

/**
 * Cards for each photo
 */
@Composable
fun PhotoCard(
    photo: String,
    onDelete: () -> Unit
){
    Column()
    {
        AsyncImage(
            model = decodeBase64Image(photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        //delete button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDelete,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = DeleteRed,
                contentColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text("Delete")
        }
    }

}