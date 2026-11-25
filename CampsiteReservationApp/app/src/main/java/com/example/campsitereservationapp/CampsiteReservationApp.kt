package com.example.campsitereservationapp

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.qrcode.CampsiteQrLink
import com.example.campsitereservationapp.ui.Admin.AdminSetup.AdminAccessViewModel
import com.example.campsitereservationapp.ui.Admin.AdminSetup.AdminGate
import com.example.campsitereservationapp.ui.Admin.AdminHomeScreen
import com.example.campsitereservationapp.ui.owner.OwnerLotteryCreationScreen
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
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
import com.example.campsitereservationapp.ui.owner.Lotterylist.OwnerLotteryListScreen
import com.example.campsitereservationapp.utils.getDeviceId
import java.util.Date

// We do not have Compasables for the navController.navigate's that are in comments
// They are just commented there as placeholders, the route name may change depending on
// what the respective composable route name is.


/**
 * Sign-up route. Sign-up forwards at once when the device already has a profile.
 *  - next: where to go after sign-up. The Admin button passes next=adminHome, so an admin
 *    without a profile signs up first and an admin with one goes straight to Admin Home.
 *  - listingId: filled in by the campsite QR deep link (basecamp://campsite/{listingId}), so a
 *    scanned code opens that campsite's details after the sign-up check.
 */
private const val SIGN_UP_ROUTE = "userSignUp?listingId={listingId}"

/**
 * Picks the screen to open once the device is signed in.
 *  - A scanned campsite QR code opens that campsite's details.
 *  - A device in the Admins collection opens Admin Home (Role Switch reaches the other homes).
 *  - An owner profile opens Owner Home (manage campsites, manage lotteries).
 *  - Anyone else opens User Home.
 *
 * @param isAdmin true if this device is in the Admins collection.
 * @param role the profile's role, Profile.ROLE_ENTRANT or Profile.ROLE_OWNER.
 * @param scannedListingId the listing from a scanned QR code, or null.
 * @return the route to navigate to.
 */
internal fun homeRouteFor(isAdmin: Boolean, role: String, scannedListingId: String?): String = when {
    scannedListingId != null -> "campsiteDetails/$scannedListingId"
    isAdmin -> "adminHome"
    role == Profile.ROLE_OWNER -> "ownerHome"
    else -> "userHome"
}

@Composable
fun CampsiteReservationApp() {
    val navController = rememberNavController()

    // get the users deviceID
    val context = LocalContext.current
    val deviceId = remember { getDeviceId(context) }

    // Home screens are the bottom of the stack, so their back button leaves the app unless an
    // admin reached them through Role Switch.
    val backOrExit: () -> Unit = {
        if (navController.previousBackStackEntry != null) navController.popBackStack()
        else (context as? Activity)?.finish()
    }

    NavHost(navController = navController, startDestination = SIGN_UP_ROUTE) {

        // ------------------------ User/Entrant ------------------------- ///

        composable(
            route = SIGN_UP_ROUTE,
            arguments = listOf(
                navArgument("listingId") { type = NavType.StringType; nullable = true; defaultValue = null }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = CampsiteQrLink.URI_PATTERN })
        ){ entry ->
            val scannedListingId = entry.arguments?.getString("listingId")
            val adminAccess: AdminAccessViewModel = viewModel()
            LaunchedEffect(deviceId) {
                adminAccess.check(deviceId)
            }

            // Wait for both the sign-up and the admin check before choosing the home screen.
            var signedInRole by remember { mutableStateOf<String?>(null) }
            LaunchedEffect(signedInRole, adminAccess.isChecking) {
                val role = signedInRole
                if (role != null && !adminAccess.isChecking) {
                    navController.navigate(homeRouteFor(adminAccess.isAdmin, role, scannedListingId)) {
                        popUpTo(SIGN_UP_ROUTE) { inclusive = true }
                    }
                }
            }

            UserSignUpScreen(
                onSignedUp = { role ->
                    signedInRole = role
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
                onBack = backOrExit,
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
            val adminAccess: AdminAccessViewModel = viewModel()
            LaunchedEffect(deviceId) {
                adminAccess.check(deviceId)
            }

            UserProfileScreen(
                profileId = deviceId,
                isAdmin = adminAccess.isAdmin,
                onBack = {
                    navController.popBackStack()
                },
                onPolicies = {
                    navController.navigate("policies")
                },
                onDeleted = {
                    navController.navigate("userSignUp") {
                        popUpTo(navController.graph.id) { inclusive = true }
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
                profileId = deviceId,
                onBack = backOrExit,
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
                ownerId = deviceId,
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

        composable(
            route = "ownerLotteries"
        ){
            OwnerLotteryListScreen(
                ownerId = deviceId,
                onBack = {
                    navController.popBackStack()
                },
                onCreateLottery = {
                    navController.navigate("createLottery")
                },
                onViewEntrants = { lotteryId ->
                    //navController.navigate("lotteryEntrants/$lotteryId")
                },
                onEditLottery = { lotteryId ->
                    navController.navigate("editLottery/$lotteryId")
                },
                onViewResults = { lotteryId ->
                    //navController.navigate("lotteryResults/$lotteryId")
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
                ownerID = deviceId,
            )
        }

        composable(
            route = "editLottery/{lotteryId}",
            arguments = listOf(
                navArgument("lotteryId") {
                    type = NavType.StringType
                }
            )
        ){ entry ->
            OwnerLotteryCreationScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCreate = {
                    navController.popBackStack()
                },
                ownerID = deviceId,
                lotteryId = entry.arguments?.getString("lotteryId").orEmpty(),
                onCancelled = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = "ownerCampDetails"
        ){
            OwnerCampDetailsScreen(
                ownerId= deviceId,
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
                ownerId = deviceId,
                listingId = entry.arguments?.getString("listingId").orEmpty(),
                onBack = {
                    navController.popBackStack()
                },
                onPublished = {
                    navController.popBackStack()
                },
            )
        }

        // ------------------------ Admin ------------------------- ///

        composable(
            route = "adminHome"
        ){
            AdminGate(
                deviceId = deviceId,
                onDenied = {
                    navController.navigate("userHome") {
                        popUpTo("adminHome") { inclusive = true }
                    }
                }
            ) {
                AdminHomeScreen(
                    onBack = backOrExit,
                    onManageCampsites = {
                        //navController.navigate("adminCampsites")
                    },
                    onManageProfiles = {
                        //navController.navigate("adminProfiles")
                    },
                    onNotificationLogs = {
                        //navController.navigate("adminNotificationLogs")
                    },
                    onMessageLogs = {
                        //navController.navigate("adminMessageLogs")
                    },
                    onUserRoleSwitch = {
                        navController.navigate("userHome")
                    },
                    onOwnerRoleSwitch = {
                        navController.navigate("ownerHome")
                    },
                    onProfile = {
                        navController.navigate("userProfile")
                    }
                )
            }
        }
    }
}