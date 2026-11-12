package com.example.campsitereservationapp.ui.entrant.WonSelection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.utils.decodeBase64Image
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.theme.RatingAmber


/**
 * Screen composable displaying the list of won campsite options in a lottery for the entrant to choose from.
 * Displays the lottery dates banner, campsite card selection list, and a booking action button.
 * @param lotteryId string input passed in from naviagtion
 * @param onBack callback triggered when tapping the top back button.
 * @param onFinished callback triggered when tapping the "Book Campsite" button after selecting a campsite.
 * @param viewModel injected ViewModel holding the list of won campsite options.
 */
@Composable
fun UserWonSelectionScreen(
    lotteryId: String = "3pTqrKarXVyXaGdHkLGo" ,
    onBack: () -> Unit,
    onFinished: (campsiteName: String) -> Unit,
    viewModel: UserWonSelectionViewModel = viewModel()
) {

    val lotDateAndListings = viewModel.getLotteryDateAndListings(lotteryId)
    val lotteryDate = lotDateAndListings?.first
    val listings = lotDateAndListings?.second


    //state variable to remember what was selected, default null (nothing selected)
    var selectedCampsite by remember {mutableStateOf<Listing?>(value = null)}

    Scaffold(
        //==============BACKBUTTON================
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 30.dp, bottom = 8.dp)
            ) {
                FilledTonalIconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        //bottom button
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val campsite = selectedCampsite
                        if (campsite  != null){
                            onFinished(campsite.name)
                        }
                    },
                    enabled = selectedCampsite != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Book Campsite",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        //
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) { //=======================TITLE AND SUBHEADER=====================
                Text(
                    text = "Choose a campsite",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select a campsite from this lottery to view its details before booking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                //=====================DATE BANNER========================
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Lottery period:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        if (lotteryDate != null) {
                            Text(
                                text = lotteryDate,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                .fillMaxWidth(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                //==================CAMPSITE LISTING HEADER==============
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Title
                    Text(
                        text = "Campsites in this lottery",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    //number of campsites counter
                    Text(
                        text = "${listings.orEmpty().size} sites",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            //===============LISTED CAMPSITES============================
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(listings.orEmpty()) { listing ->
                    val isSelected = (listing == selectedCampsite)

                    UserWonSelectionCampsiteCard(
                        listing = listing,
                        isSelected = isSelected,
                        onSelect = { selectedCampsite = listing }
                    )
                }
            }
        }
    }
}


/**
 * Composable card displaying details for a single campsite option within a won lottery.
 * Includes thumbnail image, location, capacity, star rating, availability dates, and a selection radio button.
 *
 * @param modifier optional [Modifier] applied to the outer [Surface].
 * @param isSelected whether this campsite card is currently selected by the user.
 * @param onSelect callback triggered when the user clicks this campsite card or its radio button.
 */
@Composable
fun UserWonSelectionCampsiteCard(
    listing: Listing,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {}
) {
    val cardBackground = if (isSelected) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface
    val cardBorderColor = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant
    val cardBorderWidth = if (isSelected) 2.dp else 1.dp

    Surface(
        onClick = onSelect,
        shape = MaterialTheme.shapes.large,
        color = cardBackground,
        border = BorderStroke(cardBorderWidth, cardBorderColor),
        modifier = modifier
            .fillMaxWidth()
            .height(176.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image Box (occupies 25% width of row)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .height(95.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    //select first photo, show nothing if Null
                    val firstPhoto = listing.photoUrls.firstOrNull()
                    if (firstPhoto!= null) {
                        AsyncImage(
                            model = decodeBase64Image(firstPhoto),
                            contentDescription = listing.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Information details about campsite (occupies rest of space)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = listing.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = listing.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Up to ${listing.maxOccupancy} guests",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = RatingAmber
                        )
                        Text(
                            text = "${listing.rating} (${listing.numOfReviews} reviews)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Selected / Select campsite bottom row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSelected) "Selected campsite" else "Select campsite",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )

                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.onSurface,
                        unselectedColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }
        }
    }
}
