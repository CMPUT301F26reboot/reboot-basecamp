package com.example.campsitereservationapp.ui.Owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun OwnerLotteryCreationScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCreate: () -> Unit = {},
    viewModel: OwnerLotteryCreationViewModel = viewModel()
){



}



//==================================================================================================
//============================Components Specific to this Screen====================================
//==================================================================================================

/** Drop down selection for a list of <Listing>, then displays them below the dropdown
 * @param title optional bolded title string
 * @param subHeader optional grey subheader string
 * @param listings list of listings from owner ID
 * @param modifier modifier of parent compose
 */
@Composable
fun ListingDropDownSelection(
    title: String? = null,
    subHeader: String? = null,
    listingsHeader: String? = null,
    listings: List<Listing>,
    modifier: Modifier
){


    //optional title and header



    //dropdown menu

    //optional listings header


    //listing cards


}

/**Cards shown under Selected Campsites,
 * @param listing listing of <Listing> object to be shown
 * @param onDelete event call after clicking x in top right of card
 * @param modifier modifier from parent compose
 */
@Composable
fun ListingCard(
    listing: Listing,
    onDelete: () -> Unit,
    modifier: Modifier
){
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = listing.photoUrls.firstOrNull(),
                contentDescription = "Photo of ${listing.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = listing.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                ListingDetail(
                    icon = Icons.Outlined.LocationOn,
                    text = listing.address
                )
                ListingDetail(
                    icon = Icons.Outlined.Person,
                    text = "Up to ${listing.maxOccupancy} guests"
                )
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                Icons.Filled.Close,
                contentDescription = "Remove ${listing.name}",
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onDelete)
            )
        }
    }
}

/** A small icon next to one line of listing detail text.
 * @param icon icon shown before the text
 * @param text detail text to show
 */
@Composable
private fun ListingDetail(
    icon: ImageVector,
    text: String
){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListingCardPreview(){
    ListingCard(
        listing = Listing(
            name = "Bow Valley Riverside Campground",
            address = "Banff National Park, AB",
            maxOccupancy = 5
        ),
        onDelete = {},
        modifier = Modifier
    )
}