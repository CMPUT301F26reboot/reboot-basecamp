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
import com.example.campsitereservationapp.ui.entrant.UserWonBookingViewModel
import com.google.apps.card.v1.Columns

@Composable
fun OwnerLotteryCreationScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCreate: () -> Unit = {},
    viewModel: OwnerLotteryCreationViewModel = viewModel()
){



}

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
    listings: SnapshotStateList<Listing>,
    modifier: Modifier
){

    var selectedListings by rememberSaveable { mutableStateOf(listOf<Listing>()) }

    //optional title and header

    //dropdown menu of listing listing

    for (listing in selectedListings){

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


}