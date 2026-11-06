package com.example.campsitereservationapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.campsitereservationapp.ui.entrant.CampsiteDetails.parseRouteDate
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserCampsiteSearchScreen
import com.example.campsitereservationapp.ui.entrant.ReservationDetails.ReservationDetailScreen
import com.example.campsitereservationapp.ui.entrant.Reservations.UserReservationsScreen
import com.example.campsitereservationapp.ui.entrant.TempMyLotteriesScreen
import com.example.campsitereservationapp.ui.entrant.WonBooking.UserWonBookingScreen
import com.example.campsitereservationapp.ui.entrant.WonSelection.UserWonSelectionScreen
import com.example.campsitereservationapp.ui.owner.CampsiteRegister.OwnerCampDetailsScreen
import com.example.campsitereservationapp.ui.owner.OwnerHomeScreen
import com.example.campsitereservationapp.ui.owner.CampsiteList.OwnerListingsScreen
import com.example.campsitereservationapp.ui.owner.TempOwnerLotteryListScreen

// We do not have Compasables for the navController.navigate's that are in comments
// They are just commented there as placeholders, the route name may change depending on
// what the respective composable route name is.
@Composable
fun CampsiteReservationApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "tempNav") {
        composable(
            route = "tempNav"
        ) {
            TempNavigationScreen(
                profileId = "123",
                onUser = {
                    navController.navigate("userSignUp")
                },
                onOwner = {
                    navController.navigate("ownerHome")
                }
            )
        }

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
                profileId = "test-user",
                onBack = {
                    navController.popBackStack()
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
                    //navController.navigate("userProfile")
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
                onToDetails = { listingId ->
                    navController.navigate("campsiteDetails/$listingId")
                }
            )
        }

        composable(
            route = "userReservations"
        ){
            UserReservationsScreen(
                profileId = "test-user",
                onBack  = {
                    navController.popBackStack()
                },
                onOpenReservation = { reservationId ->
                    navController.navigate("userReservationDetails/$reservationId")
                }
            )
        }

        composable(
            route = "userReservationDetails/{reservationId}"
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
            TempMyLotteriesScreen(
                profileId = "test-owner",
                onBack = {
                    navController.popBackStack()
                },
                onWonSelection = {
                    navController.navigate("userWonSelection")
                }
            )
        }

        composable(
            route = "userWonSelection"
        ){
            UserWonSelectionScreen(
                lotteryId = "3pTqrKarXVyXaGdHkLGo",
                onBack = {
                    navController.popBackStack()
                },
                onFinished = {
                    navController.navigate("userWonBooking")
                },
            )
        }

        composable(
            route = "userWonBooking"
        ){
            UserWonBookingScreen(
                lotteryId = "3pTqrKarXVyXaGdHkLGo",
                campsiteName = "Test",
                profileId = "user-test",
                onBack = {
                    navController.popBackStack()
                },
                onFinished = {
                    navController.navigate("userHome")
                },
            )
        }

        composable(
            route = "ownerHome"
        ){
            OwnerHomeScreen(
                profileId = "test-owner",
                onBack = {
                    navController.popBackStack()
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
                ownerId = "test-owner",
                onBack = {
                    navController.popBackStack()
                },
                onRegisterNew = {
                    navController.navigate("ownerCampDetails")
                },
                onDetails = {
                    //navController.navigate("ownerCampUpdate")
                }
            )
        }

        // Note: This is a Temp file, so the paramaters will likely be different for the real
        // OwnerLotteryListScreen (like onViewLotteries won't need to exist b/c it will display them by default)
        composable(
            route = "ownerLotteries"
        ){
            TempOwnerLotteryListScreen(
                profileId = "test-owner",
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
                ownerID = "test-owner",
            )
        }

        composable(
            route = "ownerCampDetails"
        ){
            OwnerCampDetailsScreen(
                ownerId= "test-owner",
                onBack = {
                    navController.popBackStack()
                },
                onPublished = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = "campsiteDetails/{listingId}?start={start}&end={end}",
            arguments = listOf(
                navArgument("start") { nullable = true; defaultValue = null },
                navArgument("end") { nullable = true; defaultValue = null }
            )
        ) { entry ->
            UserCampsiteDetailsScreen(
                listingId = entry.arguments?.getString("listingId").orEmpty(),
                searchStart = parseRouteDate(entry.arguments?.getString("start")),
                searchEnd = parseRouteDate(entry.arguments?.getString("end")),
                onBack = { navController.popBackStack() }
            )
        }
    }
}