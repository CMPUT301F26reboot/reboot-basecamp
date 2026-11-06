package com.example.campsitereservationapp.ui.entrant.SignUp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent

/** Each section of the policies, as its heading and its text. */
private val POLICY_SECTIONS = listOf(
    "Your account" to "You are identified by your device, so no password is needed. Your device " +
            "ID is stored with your profile. Please provide accurate contact details.",
    "Your information" to "Your name, email address and phone number are used to " +
            "manage your account, lotteries and reservations. You can update or delete your profile " +
            "at any time.",
    "Lotteries" to "Joining a lottery does not guarantee a campsite. Winners are drawn from the " +
            "waitlist and must confirm their booking to keep their spot.",
    "Campsite rules" to "Follow the rules set by each campsite owner. Respect other campers, " +
            "wildlife and the land, and leave your site as you found it.",
    "Enforcement" to "Accounts or campsites that violate these policies may be removed by an " +
            "administrator."
)

/**
 * Shows the app's policies and guidelines (US 01.11.01). Needs no profile, so it can be viewed
 * before an account is created.
 *
 * @param onBack called when the user taps the back button.
 */
@Composable
fun PoliciesScreen(onBack: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            BackButtonAndHeaderScreenComponent(title = "Policies & Guidelines", onBack = onBack)
            POLICY_SECTIONS.forEach { (title, body) ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}