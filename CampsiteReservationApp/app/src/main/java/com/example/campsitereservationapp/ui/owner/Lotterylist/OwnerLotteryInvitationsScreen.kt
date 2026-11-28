/**
 * This file contains the Owner Lottery Invitations screen, which lists every invitation from
 * another owner to add this owner's campsites to their lottery (US 02.04.08).
 */
package com.example.campsitereservationapp.ui.owner.Lotterylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import com.example.campsitereservationapp.ui.components.CustomDialogScreenComponent

/**
 * Lists the invitations waiting for the owner, each with Accept and Decline. Stateful wrapper
 * that wires [OwnerLotteryListViewModel] to [OwnerLotteryInvitationsContent]. An answered
 * invitation disappears from the list, since it is no longer pending.
 *
 * @param ownerId the current owner.
 * @param onBack event -> navigate back to the lottery list.
 */
@Composable
fun OwnerLotteryInvitationsScreen(
    ownerId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OwnerLotteryListViewModel = viewModel()
) {
    var isSaving by remember { mutableStateOf(false) }
    var errorTitle by remember { mutableStateOf<String?>(null) }

    OwnerLotteryInvitationsContent(
        invitations = viewModel.invitations(ownerId),
        enabled = !isSaving,
        onBack = onBack,
        onAccept = { invite ->
            isSaving = true
            viewModel.acceptInvite(
                invite.lotteryId, invite.campsiteId,
                onSuccess = { isSaving = false },
                onFailure = { isSaving = false; errorTitle = "Couldn't accept the invitation" }
            )
        },
        onDecline = { invite ->
            isSaving = true
            viewModel.declineInvite(
                invite.lotteryId, invite.campsiteId,
                onSuccess = { isSaving = false },
                onFailure = { isSaving = false; errorTitle = "Couldn't decline the invitation" }
            )
        },
        modifier = modifier
    )

    errorTitle?.let { title ->
        CustomDialogScreenComponent(
            onDismissRequest = { errorTitle = null },
            title = title,
            description = "Please try again."
        )
    }
}

/**
 * UI for the invitations screen. Stateless so it can be driven by its parameters in a Preview.
 *
 * @param invitations invitations waiting for the owner's answer.
 * @param enabled false while an answer is being saved, so it can't be sent twice.
 * @param onAccept event call when the owner accepts an invitation.
 * @param onDecline event call when the owner declines an invitation.
 */
@Composable
fun OwnerLotteryInvitationsContent(
    invitations: List<LotteryInvite>,
    enabled: Boolean,
    onBack: () -> Unit,
    onAccept: (LotteryInvite) -> Unit,
    onDecline: (LotteryInvite) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BackButtonAndHeaderScreenComponent(
                    title = "Lottery Invitations",
                    subHeader = "Other owners want to add your campsites to their lotteries.",
                    onBack = onBack,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            if (invitations.isEmpty()) {
                item {
                    Text(
                        "No invitations right now.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                    )
                }
            }
            items(invitations, key = { "invite-${it.lotteryId}-${it.campsiteId}" }) { invite ->
                LotteryInvitationCard(
                    invite = invite,
                    enabled = enabled,
                    onAccept = { onAccept(invite) },
                    onDecline = { onDecline(invite) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
