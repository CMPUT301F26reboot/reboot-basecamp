import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campsitereservationapp.ui.TempNavigationScreen
import com.example.campsitereservationapp.ui.entrant.CampsiteDetails.UserCampsiteDetailsScreen
import com.example.campsitereservationapp.ui.entrant.UserHomeScreen
import com.example.campsitereservationapp.ui.entrant.CampsiteDetails.parseRouteDate
import com.example.campsitereservationapp.ui.owner.CampsiteRegister.OwnerCampDetailsScreen
import com.example.campsitereservationapp.ui.owner.OwnerHomeScreen
import com.example.campsitereservationapp.ui.owner.CampsiteList.OwnerListingsScreen

//private const val CAMPSITE_DETAILS_ROUTE = "campsiteDetails/{listingId}?start={start}&end={end}"

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
                    navController.navigate("userHome")
                },
                onOwner = {
                    navController.navigate("ownerHome")
                }
            )
        }

        composable(
            route = "userHome"
        ){
            UserHomeScreen(
                profileId = "test-user",
                onBack = {
                    navController.navigate("tempNav")
                },
                onSearchCampsites = {
                    // actual:
                    // navController.navigate("userSearchCampsites")

                    // test with a current screen:
                    navController.navigate("campsiteDetails/c2uPtt3WNrF2XfJZggYi?start=2027-05-01&end=2027-05-10")
                },
                onMyLotteries = {
                    //navController.navigate("userLotteries")
                },
                onMyReservations = {
                    //navController.navigate("userReservations")
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
            route = "ownerHome"
        ){
            OwnerHomeScreen(
                profileId = "test-owner",
                onBack = {
                  navController.navigate("tempNav")
                },
                onManageLotteries = {
                    //navController.navigate("ownerLotteries")
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
                    navController.navigate("ownerHome")
                },
                onRegisterNew = {
                    navController.navigate("ownerCampDetails")
                },
                onDetails = {
                    //navController.navigate("ownerCampUpdate")
                }
            )
        }

        composable(
            route = "ownerCampDetails"
        ){
            OwnerCampDetailsScreen(
                ownerId= "test-owner",
                onBack = {
                    navController.navigate("ownerListings")
                },
                onPublished = {
                    navController.navigate("ownerListings")
                },
            )
        }

        composable(
            route = "campsiteDetails/{listingId}?start={start}&end={end}",
            arguments = listOf(
                // TEMPORARY test defaults so the app opens on this listing.
                // Remove once the Search screen navigates here.
                navArgument("listingId") { defaultValue = "c2uPtt3WNrF2XfJZggYi" },
                navArgument("start") { nullable = true; defaultValue = "2027-05-01" },
                navArgument("end") { nullable = true; defaultValue = "2027-05-10" }
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