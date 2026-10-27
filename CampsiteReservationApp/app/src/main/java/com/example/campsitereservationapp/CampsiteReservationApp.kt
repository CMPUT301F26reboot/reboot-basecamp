import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campsitereservationapp.UserCampsiteDetailsScreen

@Composable
fun CampsiteReservationApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "campsiteDetails"){
        composable("campsiteDetails") { UserCampsiteDetailsScreen(navController) }
    }

}