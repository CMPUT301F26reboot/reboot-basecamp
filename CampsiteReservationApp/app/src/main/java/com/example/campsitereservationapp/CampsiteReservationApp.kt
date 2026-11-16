package com.example.campsitereservationapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campsitereservationapp.ui.owner.OwnerLotteryCreationScreen
import com.example.campsitereservationapp.ui.TempNavigationScreen
import com.example.campsitereservationapp.ui.entrant.CampsiteDetails.UserCampsiteDetailsScreen
import com.example.campsitereservationapp.ui.entrant.UserHomeScreen
import com.example.campsitereservationapp.ui.entrant.SignUp.PoliciesScreen
import com.example.campsitereservationapp.ui.entrant.SignUp.UserSignUpScreen
import com.example.campsitereservationapp.ui.entrant.UserProfile.UserProfileScreen
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserCampsiteSearchScreen
import com.example.campsitereservationapp.ui.entrant.CurrentLotteries.UserCurrentLotteriesScreen
import com.example.campsitereservationapp.ui.entrant.ReservationDetails.ReservationDetailScreen
import com.example.campsitereservationapp.ui.entrant.Reservations.UserReservationsScreen
import com.example.campsitereservationapp.ui.entrant.WonBooking.UserWonBookingScreen
import com.example.campsitereservationapp.ui.entrant.WonSelection.UserWonSelectionScreen
import com.example.campsitereservationapp.ui.owner.CampsiteRegister.OwnerCampDetailsScreen
import com.example.campsitereservationapp.ui.owner.OwnerHomeScreen
import com.example.campsitereservationapp.ui.owner.CampsiteList.OwnerListingsScreen
import com.example.campsitereservationapp.ui.owner.TempOwnerLotteryListScreen
import com.example.campsitereservationapp.utils.getDeviceId
import java.util.Date

// We do not have Compasables for the navController.navigate's that are in comments
// They are just commented there as placeholders, the route name may change depending on
// what the respective composable route name is.

// OWNER_ID is hardcoded for now since no owner signup/device setup yet.
private const val OWNER_ID = "test-owner"

@Composable
fun CampsiteReservationApp() {
    val navController = rememberNavController()

    // get the users deviceID
    val context = LocalContext.current
    val deviceId = remember { getDeviceId(context) }

    NavHost(navController = navController, startDestination = "tempNav") {
        composable(
            route = "tempNav"
        ) {
            TempNavigationScreen(
                profileId = deviceId,
                onUser = {
                    navController.navigate("userSignUp")
                },
                onOwner = {
                    navController.navigate("ownerHome")
                }
            )
        }

        // ------------------------ User/Entrant ------------------------- ///

        composable(
            route = "userSignUp"
        ){
            UserSignUpScreen(
                onSignedUp = {
                    navController.navigate("userHome") {
                        popUpTo("userSignUp") { inclusive = true }
                    }
                },
                onOwnerSignUp = {
                    //navController.navigate("ownerSignUp")
                },
                onPolicies = {
                    navController.navigate("policies")
                }
            )
        }

        composable(
            route = "policies"
        ){
            PoliciesScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "userHome"
        ){
            UserHomeScreen(
                profileId = deviceId,
                onBack = {
                    navController.navigate("tempNav")
                },
                onSearchCampsites = {
                    navController.navigate("userSearchCampsites")
                },
                onMyLotteries = {
                    navController.navigate("userLotteries")
                },
                onMyReservations = {
                    navController.navigate("userReservations")
                },
                onNotifications = {
                    //navController.navigate("userNotifications")
                },
                onProfile = {
                    navController.navigate("userProfile")
                }
            )
        }

        composable(
            route = "userProfile"
        ){
            UserProfileScreen(
                profileId = deviceId,
                onBack = {
                    navController.popBackStack()
                },
                onPolicies = {
                    navController.navigate("policies")
                },
                onDeleted = {
                    navController.navigate("userSignUp") {
                        popUpTo("tempNav")
                    }
                }
            )
        }

        composable(
            route = "userSearchCampsites"
        ){
            UserCampsiteSearchScreen(
                onBack = {
                    navController.popBackStack()
                },
                onToDetails = { listingId, start, end ->
                    val dates = if (start != null && end !=null)
                            "?start=${start.time}&end=${end.time}" else ""
                    navController.navigate("campsiteDetails/$listingId$dates")
                }
            )
        }

        composable(
            route = "campsiteDetails/{listingId}?start={start}&end={end}",
            arguments = listOf(
                navArgument("listingId") { type = NavType.StringType },
                navArgument("start") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("end") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { entry ->
            UserCampsiteDetailsScreen(
                listingId = entry.arguments?.getString("listingId").orEmpty(),
                profileId = deviceId,
                searchStart = entry.arguments?.getString("start")?.toLongOrNull()?.let(::Date),
                searchEnd = entry.arguments?.getString("end")?.toLongOrNull()?.let(::Date),
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "userReservations"
        ){
            UserReservationsScreen(
                profileId = deviceId,
                onBack  = {
                    navController.navigate("userHome")
                },
                onToLotteries = {
                    navController.navigate("userLotteries")
                },
                onOpenReservation = { reservationId ->
                    navController.navigate("userReservationDetails/$reservationId")
                }
            )
        }

        composable(
            route = "userReservationDetails/{reservationId}",
            arguments = listOf(
                navArgument("reservationId") {
                    type = NavType.StringType
                }
            )

        ){ entry ->
            ReservationDetailScreen(
                reservationId = entry.arguments?.getString("reservationId").orEmpty(),
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "userLotteries"
        ){
            UserCurrentLotteriesScreen(
                profileId = deviceId,
                onBack = {
                    navController.navigate("userHome")
                },
                onToBooking = { lotteryId ->
                    navController.navigate("userWonSelection/$lotteryId")
                },
                onToReservations = {
                    navController.navigate("userReservations")
                }
            )
        }

        composable(
            route = "userWonSelection/{lotteryId}",
            arguments = listOf(
                navArgument("lotteryId"){
                    type = NavType.StringType
                }
            )
        ){ entry ->
            val lotteryId = entry.arguments?.getString("lotteryId").orEmpty()
            UserWonSelectionScreen(
                lotteryId = lotteryId,
                onBack = {
                    navController.popBackStack()
                },
                onFinished = { campsiteName ->
                    navController.navigate("userWonBooking/$lotteryId/$campsiteName")
                },
            )
        }

        composable(
            route = "userWonBooking/{lotteryId}/{campsiteName}",
            arguments = listOf(
                navArgument("lotteryId") {
                    type = NavType.StringType
                },
                navArgument("campsiteName") {
                    type = NavType.StringType
                }
            )
        ){ entry ->
            UserWonBookingScreen(
                lotteryId = entry.arguments?.getString("lotteryId").orEmpty(),
                campsiteName = entry.arguments?.getString("campsiteName").orEmpty(),
                profileId = deviceId,
                onBack = {
                    navController.popBackStack()
                },
                onFinished = {
                    //keep the user home at the top of the stack
                    navController.popBackStack("userHome", inclusive = false)
                },
            )
        }


        // ------------------------ Owner ------------------------ //

        composable(
            route = "ownerHome"
        ){
            OwnerHomeScreen(
                profileId = OWNER_ID,
                onBack = {
                    navController.navigate("tempNav")
                },
                onManageLotteries = {
                    navController.navigate("ownerLotteries")
                },
                onManageCampsites = {
                    navController.navigate("ownerListings")
                },
                onNotificationHistory = {
                    //navController.navigate("ownerNotificationHistory")
                },
                onProfile = {
                    //navController.navigate("ownerProfile")
                }
            )
        }

        composable(
            route = "ownerListings"
        ){
            OwnerListingsScreen(
                ownerId = OWNER_ID,
                onBack = {
                    navController.popBackStack()
                },
                onRegisterNew = {
                    navController.navigate("ownerCampDetails")
                },
                onDetails = { listing ->
                    //navController.navigate("ownerCampUpdate/${listing.id}")
                },
                onEdit = { listing ->
                    navController.navigate("ownerCampEdit/${listing.id}")
                }
            )
        }

        // Note: This is a Temp file, so the paramaters will likely be different for the real
        // OwnerLotteryListScreen (like onViewLotteries won't need to exist b/c it will display them by default)
        composable(
            route = "ownerLotteries"
        ){
            TempOwnerLotteryListScreen(
                profileId = OWNER_ID,
                onBack = {
                    navController.popBackStack()
                },
                onCreateLottery = {
                    navController.navigate("createLottery")
                },
                onViewLotteries = {
                    //navController.navigate("viewLotteries")
                }
            )
        }

        composable(
            route = "createLottery"
        ){
            OwnerLotteryCreationScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCreate = {
                    navController.popBackStack()
                },
                ownerID = OWNER_ID,
            )
        }

        composable(
            route = "ownerCampDetails"
        ){
            OwnerCampDetailsScreen(
                ownerId= OWNER_ID,
                onBack = {
                    navController.popBackStack()
                },
                onPublished = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = "ownerCampEdit/{listingId}",
            arguments = listOf(
                navArgument("listingId") {
                    type = NavType.StringType
                }
            )
        ){ entry ->
            OwnerCampDetailsScreen(
                ownerId = OWNER_ID,
                listingId = entry.arguments?.getString("listingId").orEmpty(),
                onBack = {
                    navController.popBackStack()
                },
                onPublished = {
                    navController.popBackStack()
                },
            )
        }
    }
}