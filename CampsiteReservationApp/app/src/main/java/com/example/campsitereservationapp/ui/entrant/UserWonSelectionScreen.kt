package com.example.campsitereservationapp.ui.entrant

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing


/**
 * Screen composable displaying the list of won campsite options in a lottery for the entrant to choose from.
 * Displays the lottery dates banner, campsite card selection list, and a booking action button.
 *
 * @param onBack callback triggered when tapping the top back button.
 * @param onFinished callback triggered when tapping the "Book Campsite" button after selecting a campsite.
 * @param viewModel injected ViewModel holding the list of won campsite options.
 */
@Composable
fun UserWonSelectionScreen(
    onBack: () -> Unit,
    onFinished: () -> Unit,
    viewModel: UserWonSelectionViewModel = viewModel()
) {

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
                    onClick = onFinished,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF111827),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
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
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select a campsite from this lottery to view its details before booking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                //=====================DATE BANNER========================
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3F4F6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = viewModel.lottery_date, //TODO: implement date for lottery
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Fixed lottery dates · Same for all campsites",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
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
                        color = Color(0xFF111827)
                    )
                    //number of campsites counter
                    Text(
                        text = "${viewModel.listings.size} sites",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
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
                items(viewModel.listings.orEmpty()) { listing ->
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
    val cardBackground = if (isSelected) Color(0xFFF9FAFB) else Color.White
    val cardBorderColor = if (isSelected) Color(0xFF111827) else Color(0xFFE5E7EB)
    val cardBorderWidth = if (isSelected) 2.dp else 1.dp

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(16.dp),
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    //select first photo, show nothing if Null
                    val firstPhoto = listing.photoUrls.firstOrNull()
                    if (firstPhoto!= null) {
                        AsyncImage(
                            model = firstPhoto,
                            contentDescription = listing.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF6B7280)
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = listing.address,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color(0xFF6B7280)
                        )
                        Text(
                            text = "Up to ${listing.maxOccupancy} guests",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
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
                            tint = Color(0xFFF59E0B)
                        )
                        Text(
                            text = "${listing.rating} (${listing.numOfReviews} reviews)",
                            fontSize = 11.sp,
                            color = Color(0xFF111827),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color(0xFF6B7280)
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
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF111827) else Color(0xFF6B7280)
                )

                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF111827),
                        unselectedColor = Color(0xFFE5E7EB)
                    )
                )
            }
        }
    }
}
