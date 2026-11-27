package com.example.campsitereservationapp.ui.Admin.BrowseCampsites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.owner.CampsiteList.CampsiteThumbnail
import com.example.campsitereservationapp.ui.owner.CampsiteList.RatingRow
import com.example.campsitereservationapp.ui.theme.DeleteRed
import com.example.campsitereservationapp.ui.theme.commonButtonColour


/**
 * Campsite Card for Admin Browse Campsite, Copied Campsite card from Campsite Listings
 */
@Composable
fun CampsiteCard(
    listing: Listing,
    onDelete: () -> Unit,
    onImages: () -> Unit,
) {
    //taken from owner campsite listings screen
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp))
        {
            CampsiteThumbnail(photoUrl = listing.photoUrls.firstOrNull())

            Spacer(Modifier.width(12.dp))

            //campsite information
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    listing.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Up to ${listing.maxOccupancy} Guests", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(2.dp))
                RatingRow(rating = listing.rating, numOfReviews = listing.numOfReviews)
                Spacer(Modifier.height(8.dp))
                }
            Spacer(modifier = Modifier.weight(1f))

            //right side buttons
            Column(
            ) {
                //delete button
                IconButton(
                    onClick = onDelete,
                    shape = MaterialTheme.shapes.medium,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = DeleteRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete campsite",
                        )
                }

                //images button
                IconButton(
                    onClick = onImages,
                    shape = MaterialTheme.shapes.medium,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = commonButtonColour,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "View Images",

                        )
                }

            }
            }
        }
}

