import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campsitereservationapp.ui.entrant.TempNavigationScreen
import com.example.campsitereservationapp.ui.entrant.UserCampsiteDetailsScreen
import com.example.campsitereservationapp.ui.entrant.parseRouteDate

//private const val CAMPSITE_DETAILS_ROUTE = "campsiteDetails/{listingId}?start={start}&end={end}"

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
                    navController.navigate("campsiteDetails/c2uPtt3WNrF2XfJZggYi?start=2027-05-01&end=2027-05-10")
                },
                onOwner = {
                    //empty for now
                }
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