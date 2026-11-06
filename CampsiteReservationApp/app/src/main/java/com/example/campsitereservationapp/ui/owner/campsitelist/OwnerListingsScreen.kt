package com.example.campsitereservationapp.ui.owner.campsitelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ListingViewModel


/**
 * Shows the owner's campsites. Filters ListingViewModel's listings down to this owner.
 * @param ownerId - String ID of owner from fb
 * @param onBack - event -> navigate to previous screen
 * @param onRegisterNew - event -> navigate to Campsite Creation
 * @param onDetails -> event -> navigate to details of selected campsite
 * @param modifier [Modifier] to be applied to the root composable of the screen layout.
 * Outstanding issues:
 *  - No edit icon to edit a listing yet.
 *  - No QR code icon yet.
 *  - Ratings aren't collected by the app yet; the number shown is just existing data hardcoded in to firebase.
 *  - Tapping Details doesn't open a campsite details view yet.
 */
@Composable
fun OwnerListingsScreen(
    ownerId: String,
    onBack: () -> Unit,
    onRegisterNew: () -> Unit,
    onDetails: (Listing) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListingViewModel = viewModel()
) {
    val ownerListings = viewModel.listings.filter { it.ownerID == ownerId }

    OwnerListingsContent(
        listings = ownerListings,
        onBack = onBack,
        onRegisterNew = onRegisterNew,
        onDetails = onDetails,
        modifier = modifier
    )
}

/**
 * Pure UI for the campsite list. Stateless so it can be driven entirely by its parameters in a
 * Compose Preview, with no database involved.
 */
@Composable
fun OwnerListingsContent(
    listings: List<Listing>,
    onBack: () -> Unit,
    onRegisterNew: () -> Unit,
    onDetails: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            RegisterNewCampsiteButton(
                onClick = onRegisterNew,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "My Campsites",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Manage your registered campsites",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(listings, key = { it.id }) { listing ->
                CampsiteCard(listing = listing, onDetailsClick = { onDetails(listing) })
            }
        }
    }
}

/** Black button at the bottom that starts registering a new campsite. */
@Composable
private fun RegisterNewCampsiteButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Register New Campsite", fontWeight = FontWeight.Bold)
    }
}
