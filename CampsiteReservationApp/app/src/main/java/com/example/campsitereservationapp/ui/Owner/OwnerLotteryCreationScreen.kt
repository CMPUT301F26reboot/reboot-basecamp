package com.example.campsitereservationapp.ui.Owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableIntStateOf
import com.example.campsitereservationapp.ui.components.formatDateRange
import com.example.campsitereservationapp.ui.components.nextDateRange
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.window.PopupProperties
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import com.example.campsitereservationapp.ui.components.CustomDialogScreenComponent
import com.example.campsitereservationapp.ui.components.InputDateScreenComponent
import com.example.campsitereservationapp.ui.components.IntInputScreenComponent
import com.example.campsitereservationapp.utils.decodeBase64Image
import java.util.Date
@Composable
fun OwnerLotteryCreationScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCreate: () -> Unit = {},
    ownerID: String = "test-owner",
    viewModel: OwnerLotteryCreationViewModel = viewModel()
){
    var lotteryDates by remember { mutableStateOf(listOf<Date>()) }
    val dates = remember  {mutableStateListOf<Date>()}
    var selectedListingIDs by remember { mutableStateOf(listOf<String>()) }
    var winnersToDraw by remember { mutableIntStateOf(0) }
    var waitlistCap by remember { mutableIntStateOf(0) }
    var showCreatedDialog by remember { mutableStateOf(false) }
    var showFailedDialog by remember { mutableStateOf(false) }
    var lotteryName by remember { mutableStateOf("") }

    val ownerListings = viewModel.getOwnerListings(ownerID)
    val selectedListings = ownerListings.filter { it.id in selectedListingIDs }

    val canCreate = lotteryDates.isNotEmpty() &&
            selectedListings.isNotEmpty() &&
            dates.isNotEmpty() &&
            winnersToDraw > 0 &&
            winnersToDraw < waitlistCap

    Scaffold(modifier = modifier) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BackButtonAndHeaderScreenComponent(
                title = "Create Lottery",
                subHeader = "Choose your campsites and lottery dates.",
                onBack = onBack
            )

            // Input lottery title:
            OutlinedTextField(
                value = lotteryName,
                onValueChange = { lotteryName = it },
                placeholder = { Text("Lottery Title") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )



            //lottery start and end dates
            InputDateScreenComponent(
                title = "Lottery dates",
                subheader = "Set when the lottery starts and ends.",
                infoDescription = "These dates apply to all selected campsites.",
                dates = lotteryDates,
                onSelection = { day -> lotteryDates = nextDateRange(lotteryDates, day) },
                modifier = Modifier.fillMaxWidth()
            )

            //campsites
            ListingDropDownSelection(
                title = "Campsites",
                subHeader = "Select one or more of your campsites.",
                listingsHeader = "Selected campsites",
                listings = ownerListings,
                selectedListings = selectedListings,
                onSelect = { selectedListingIDs = selectedListingIDs + it.id },
                onRemove = { selectedListingIDs = selectedListingIDs - it.id },
                modifier = Modifier
            )

            //availability, one input for all campsites, stored in Lottery.dates
            InputDateScreenComponent(
                title = "Availability for all campsites",
                dates = dates,
                removedDates = lotteryDates,
                onSelection = { day ->
                    val newDates = nextDateRange(dates, day)
                    dates.clear()
                    dates.addAll(newDates)
                },
                modifier = Modifier.fillMaxWidth()
            )

            //lottery quotas
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Lottery quotas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Set the number of winners and the waitlist capacity.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IntInputScreenComponent(
                        title = "Winners to Draw",
                        num = winnersToDraw,
                        onNumChange = { winnersToDraw = it }
                    )
                    IntInputScreenComponent(
                        title = "Waitlist Capacity Cap",
                        subHeader = "Maximum entrants allowed",
                        num = waitlistCap,
                        onNumChange = { waitlistCap = it }
                    )
                }
            }

            //create button
            Button(
                onClick = {
                    viewModel.createAndAddLottery(
                        title = lotteryName,
                        dates = dates.toList(),
                        start = lotteryDates[0],
                        end = lotteryDates[lotteryDates.lastIndex],
                        campsiteIDs = selectedListingIDs,
                        winnersToDraw = winnersToDraw,
                        waitlistCap = waitlistCap,
                        onSuccess = { showCreatedDialog = true},
                        onFailure = { showFailedDialog = true}
                    )
                },
                enabled = canCreate,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Create", fontWeight = FontWeight.Bold)
            }
        }
    }

    //popup dialog on successful or failure on listing addition
    if (showCreatedDialog) {
        val names = selectedListings.joinToString(" and ") { it.name }
        val end = lotteryDates[lotteryDates.lastIndex]
        CustomDialogScreenComponent(
            onDismissRequest = {
                showCreatedDialog = false
                onCreate()
            },
            title = "Lottery created",
            description = "Your lottery for $names is live. " +
                    "Campers can enter until ${formatDateRange(listOf(end))}."
        )
    } else if (showFailedDialog)
     {
        CustomDialogScreenComponent(
            onDismissRequest = {
                showFailedDialog = false
            },
            title = "Lottery creation Failed",

        )
    }
}



//==================================================================================================
//============================Components Specific to this Screen====================================
//==================================================================================================
/** Drop down selection for a list of <Listing>, then displays them below the dropdown
 * @param title optional bolded title string
 * @param subHeader optional grey subheader string
 * @param listingsHeader optional grey header shown above the selected listings
 * @param listings list of listings from owner ID
 * @param selectedListings listings that are currently selected
 * @param onSelect event call with the listing picked from the dropdown
 * @param onRemove event call with the listing whose x was clicked
 * @param modifier modifier of parent compose
 */
@Composable
fun ListingDropDownSelection(
    title: String? = null,
    subHeader: String? = null,
    listingsHeader: String? = null,
    listings: List<Listing>,
    selectedListings: List<Listing>,
    onSelect: (Listing) -> Unit,
    onRemove: (Listing) -> Unit,
    modifier: Modifier
){
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            //optional title and header
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (subHeader != null) {
                Text(
                    text = subHeader,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (title != null || subHeader != null) {
                Spacer(Modifier.height(12.dp))
            }

            //dropdown menu
            listingDropDownMenu(
                listings = listings,
                selectedListings = selectedListings,
                onSelect = onSelect
            )

            //optional listings header
            if (listingsHeader != null && selectedListings.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = listingsHeader,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            //listing cards
            selectedListings.forEach { listing ->
                Spacer(Modifier.height(8.dp))
                ListingCard(
                    listing = listing,
                    onDelete = { onRemove(listing) },
                    modifier = Modifier
                )
            }
        }
    }
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
                model = decodeBase64Image(listing.photoUrls.firstOrNull()),
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


/** Searchable drop down of listings. Listings that are already selected are greyed out.
 * @param listings all listings that can be chosen
 * @param selectedListings listings that are already selected
 * @param onSelect event call with the listing that was clicked
 */
@Composable
fun listingDropDownMenu(
    listings: List<Listing>,
    selectedListings: List<Listing>,
    onSelect: (Listing) -> Unit,
){
    var searchText by rememberSaveable { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }

    val filteredListings = listings.filter { it.name.contains(searchText, ignoreCase = true) }
    val expanded = menuOpen && filteredListings.isNotEmpty()

    // Opens the menu when the field is tapped.
    val fieldInteraction = remember { MutableInteractionSource() }
    val fieldPressed by fieldInteraction.collectIsPressedAsState()
    LaunchedEffect(fieldPressed) {
        if (fieldPressed) {
            menuOpen = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val menuWidth = maxWidth
        Box {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    menuOpen = true
                },
                placeholder = { Text("Search campsites by name") },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null)
                },
                trailingIcon = {
                    Icon(
                        if (expanded) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                interactionSource = fieldInteraction,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { menuOpen = false },
                // Not focusable, so the keyboard stays on the field while typing.
                properties = PopupProperties(focusable = false),
                modifier = Modifier.width(menuWidth)
            ) {
                filteredListings.forEach { listing ->
                    val isSelected = selectedListings.any { it.id == listing.id }

                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = listing.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = listing.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) Color.Unspecified
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            onSelect(listing)
                            searchText = ""
                            menuOpen = false
                        },
                        enabled = !isSelected,
                        modifier = if (isSelected) {
                            Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        } else {
                            Modifier
                        }
                    )
                }
            }
        }
    }
}











//==============================================Previews===============================
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
@Preview(showBackground = true)
@Composable
fun ListingDropDownSelectionPreview(){
    val listings = remember {
        listOf(
            Listing(
                name = "Bow Valley Riverside Campground",
                address = "Banff National Park, AB",
                maxOccupancy = 5
            ).apply { id = "1" },
            Listing(
                name = "Coastal Cliffside",
                address = "Big Sur, CA",
                maxOccupancy = 4
            ).apply { id = "2" },
            Listing(
                name = "Desert Oasis",
                address = "Joshua Tree, CA",
                maxOccupancy = 6
            ).apply { id = "3" }
        )
    }
    var selectedListings by remember { mutableStateOf(listOf<Listing>()) }

    ListingDropDownSelection(
        title = "Campsites",
        subHeader = "Select one or more of your campsites.",
        listingsHeader = "Selected campsites",
        listings = listings,
        selectedListings = selectedListings,
        onSelect = { selectedListings = selectedListings + it },
        onRemove = { selectedListings = selectedListings - it },
        modifier = Modifier
    )
}


