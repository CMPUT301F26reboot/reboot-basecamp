/**
 * This file holds the Profile Card used in AdminBrowseProfileScreen
 */
package com.example.campsitereservationapp.ui.Admin.BrowseProfile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.ui.owner.CampsiteList.CampsiteThumbnail
import com.example.campsitereservationapp.ui.theme.DeleteRed
import com.example.campsitereservationapp.ui.theme.commonButtonColour

/**
 * Profile Card for Admin Browse Profile, Copied Campsite card from Campsite Listings
 * @param profile being displayed
 * @param onDelete action after clicking delete
 */
@Composable
fun ProfileCard(
    profile: Profile,
    onDeleteProfile: () -> Unit,
    onDeleteProfilePicture: () -> Unit
) {
    //copied from owner campsite listings screen
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp))
        {
            //AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-26_AdminBrowseAndDelete.md
            //adding delete button to top right of image if they have one
            Box {
                CampsiteThumbnail(photoUrl = profile.profilePicture)

                if (profile.profilePicture.isNotEmpty()) {
                    IconButton(
                        onClick = onDeleteProfilePicture,
                        shape = MaterialTheme.shapes.small,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = DeleteRed,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete profile picture",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            //campsite information
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    profile.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    profile.phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            //right side buttons
            Column(
            ) {
                //delete button
                IconButton(
                    onClick = onDeleteProfile,
                    shape = MaterialTheme.shapes.medium,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = DeleteRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete profile",
                    )
                }


            }
        }
    }
}