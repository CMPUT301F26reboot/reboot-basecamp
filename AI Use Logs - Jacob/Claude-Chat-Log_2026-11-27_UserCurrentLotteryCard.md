# Claude Chat Log

- **Model:** Claude Opus 5.5 (claude-opus-5-5)
- **Date created:** 2026-11-27
- **Time created:** 7:11 PM (UTC-06:00)
- **Marketplace plugin used:** None (`i-have-adhd:i-have-adhd` was requested but is not available in this session)

## Information uploaded by user

| Prompt | Item | Type | Notes |
|---|---|---|---|
| 1 | AGENT.md | File | Chat log instructions |
| 1 | CampsiteReservationApp | Folder reference | Project location |
| 2 | OngoingLotteryCard code | Pasted text | `OngoingLotteryCard` composable |
| 3 | LotteryCard.kt code | Pasted text | Empty `LotteryCard` stub |
| 4 | UserCurrentLotteriesScreen code | Pasted text | `LazyColumn` blocks that call the two cards |

**Layout:** USER is on the right. CLAUDE is on the left.

---

## Prompt 1

<div align="right">

**USER**

@"AGENT.md"
/i-have-adhd:i-have-adhd name it UserCurrentLotteryCard. Path CampsiteReservationApp

</div>

**CLAUDE**

(Actions: Read AGENT.md, got the current time, moved the session to the project folder, created this log.)

Setup is done:

- **Log name:** `Claude-Chat-Log_2026-11-27_UserCurrentLotteryCard.md`, in the same folder as AGENT.md.
- **Project location:** `CampsiteReservationApp`. The session now works in that folder.

One problem: `/i-have-adhd:i-have-adhd` is not a skill or plugin that is installed in this session, so I could not run it. If you meant to use it, install the plugin and send the command again.

What do you want to work on in the project? (For example, the `UserCurrentLotteryCard` component.) Per your log rules, I will not edit your project files. I will give code changes in my reply for you to apply.

---

## Prompt 2

<div align="right">

**USER**

Read owner lottery card and the following code:

```kotlin
@Composable
fun OngoingLotteryCard(
    lottery: Lottery,
    imageURL: String,
    profileId: String,
    onToLeave: (Lottery, String) -> Unit
){
    OutlinedCard(modifier = Modifier.fillMaxWidth(fraction = 0.9f)){
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
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    //select first photo, show nothing if Null
                    val firstPhoto = imageURL
                    if (firstPhoto.isNotEmpty()) {
                        AsyncImage(
                            model = firstPhoto,
                            contentDescription = lottery.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = lottery.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        onClick = {onToLeave(lottery, profileId)},
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(vertical = 14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ){
                        Text("Leave")
                    }

                }
            }
        }
    }
}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Searched for lottery card composables, read OwnerLotteryCard.kt and LotteryCard.kt.)

Read both. Also saw `LotteryCard.kt` is an empty stub.

**How `OngoingLotteryCard` differs from `OwnerLotteryCard`:**

| | OwnerLotteryCard | OngoingLotteryCard |
|---|---|---|
| Card | `Card` + 1dp `outlineVariant` border, `shapes.large`, `surface` color | `OutlinedCard`, default shape/color |
| Width | `modifier` param, `fillMaxWidth()` | hardcoded `fillMaxWidth(0.9f)`, no `modifier` param |
| Padding / spacing | 16.dp, `spacedBy(12.dp)` | 12.dp, `SpaceBetween` |
| Title | `titleLarge`, left aligned, `maxLines = 2` + ellipsis | `titleLarge` + Bold, centered, no line limit |
| Buttons | `shapes.medium`, padding 12/10, `labelLarge` Bold text | `shapes.medium`, vertical 14, plain text, 0.7 width |
| Image | none | 25% width image box |

**Small things in the pasted code:**

- `Column` uses `fillMaxSize()` inside the card. `fillMaxWidth()` is enough.
- `val firstPhoto = imageURL` is not needed. Use `imageURL` directly.
- The comment says "show nothing if Null", but it shows a calendar icon.

What do you want next? For example: restyle `OngoingLotteryCard` to match the owner card, or fill in the `LotteryCard` stub using this code.

---

## Prompt 3

<div align="right">

**USER**

based on ownerlotterycard, create a userLotteryCard that shows a red cancel button when buttonlabel = "Cancel" and the commonbuttoncolor (look in Theme/Color) when button label is "Claim". The card should display lotteryinformation with similar format of OwnerLotteryCard. But the buttons should either be Cancel or Claim in the bottom instead

```kotlin
/**
 * This file contains the lottery card used in currentLotteryScreen
 *
 * AI USE: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-27_UserCurrentLotteryCard.md
 */
package com.example.campsitereservationapp.ui.entrant.CurrentLotteries

import androidx.compose.runtime.Composable

/**
 * Lottery Card used in UserCurrentLotteries,
 * It can either show cancel
 * @param profileID id of current profile
 * @param lotteryID id of lottery being shown
 * @param image first image in lottery
 * @param buttonLabel either "Won" or "onGoing"
 * @param onClick action when Cancel button is pressed
 */
@Composable
fun LotteryCard(
    profileID: String,
    lotteryID: String,
    image: String,
    buttonLabel: String,
    onClick:() -> Unit
){

}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Read Color.kt, Lottery.kt, OwnerLotteryListViewModel.kt and CampsiteCard.kt to get the colors, the lottery fields and the date formatter.)

Here is `UserLotteryCard` for `LotteryCard.kt`. Replace the whole file with this:

```kotlin
/**
 * This file contains the lottery card used in currentLotteryScreen
 *
 * AI USE: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-27_UserCurrentLotteryCard.md
 */
package com.example.campsitereservationapp.ui.entrant.CurrentLotteries

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.owner.Lotterylist.formatDateSpan
import com.example.campsitereservationapp.ui.theme.DeleteRed
import com.example.campsitereservationapp.ui.theme.commonButtonColour

/**
 * Lottery Card used in UserCurrentLotteries.
 * Shows the lottery's details in the same format as OwnerLotteryCard, with one button at the
 * bottom: a red "Cancel" button, or a "Claim" button in [commonButtonColour].
 * @param lottery lottery being shown
 * @param image first image in lottery, or "" for none
 * @param buttonLabel either "Cancel" or "Claim"
 * @param onClick action when the button is pressed
 */
@Composable
fun UserLotteryCard(
    lottery: Lottery,
    image: String,
    buttonLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserLotteryCardHeader(lottery, image)
            UserLotteryDetails(lottery)
            EntrantsJoinedRow(
                entrantCount = lottery.waitlist.size + lottery.selectedlist.size,
                waitlistCap = lottery.waitlistCapacity
            )
            CancelOrClaimButton(buttonLabel, onClick)
        }
    }
}

/** Lottery image (or a calendar icon when there is none) and the title. */
@Composable
private fun UserLotteryCardHeader(lottery: Lottery, image: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            if (image.isNotEmpty()) {
                AsyncImage(
                    model = image,
                    contentDescription = lottery.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            lottery.title.ifBlank { "Untitled lottery" },
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

/** Registration window and booking stay, in a light gray box. */
@Composable
private fun UserLotteryDetails(lottery: Lottery) {
    val stayStart = lottery.dates.minOrNull()
    val stayEnd = lottery.dates.maxOrNull()
    val stay = if (stayStart != null && stayEnd != null) {
        formatDateSpan(stayStart, stayEnd)
    } else {
        "No dates set"
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailRow("Registration Window:", formatDateSpan(lottery.start, lottery.end))
            DetailRow("Booking Stay:", stay)
        }
    }
}

/** A gray label on the left and its value on the right. */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

/** "Entrants Joined" with the number joined, out of the cap when there is one. */
@Composable
private fun EntrantsJoinedRow(entrantCount: Int, waitlistCap: Int?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.Group,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Entrants Joined", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(
            if (waitlistCap != null) "$entrantCount / $waitlistCap" else "$entrantCount",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Full-width button: red for "Cancel", [commonButtonColour] for "Claim". */
@Composable
private fun CancelOrClaimButton(buttonLabel: String, onClick: () -> Unit) {
    val containerColor = if (buttonLabel == "Cancel") DeleteRed else commonButtonColour
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(buttonLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}
```

**What I changed from your stub, and why:**

- **`lotteryID` → `lottery: Lottery`.** The card needs the title, dates and waitlist, and an ID alone doesn't have them.
- **Removed `profileID`.** `onClick` takes no arguments, so the card never used it. Pass it inside the lambda instead: `onClick = { onToLeave(lottery, profileId) }`.
- **Added `modifier: Modifier = Modifier`**, the same as `OwnerLotteryCard`.
- **Red** is `DeleteRed` from `Color.kt`. **Claim** uses `commonButtonColour` (`Gray900`).
- **No campsite name row.** `Lottery` only stores `campsiteIDs`, not names.
- `formatDateSpan` is `internal` in `OwnerLotteryCard.kt`, so it's imported. `DetailRow` and `EntrantsJoinedRow` are `private` there, so they're copied here.

I have not compiled this or run it in the app.

---

## Prompt 4

<div align="right">

**USER**

Its important that it replaces WonLotteryCard and OngoingLotteryCard without alteration. Read UserCurrentLotteriesScreen and UserCurrentLotteriesViewModel for refrence.

```kotlin
if(showWon){
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(vm.wonLotteries.toList()){ lottery ->
                    WonLotteryCard(
                        lottery = lottery,
                        imageURL = vm.getLotteryImage(lottery),
                        profileId = profileId,
                        onToBooking = onToBooking
                    )
                }
            }
        }
        if(showOngoing){
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(vm.ongoingLotteries.toList()){ lottery ->
                    OngoingLotteryCard(
                        lottery = lottery,
                        imageURL = vm.getLotteryImage(lottery),
                        profileId = profileId,
                        onToLeave = vm::leaveLottery
                        )
                }
            }
        }
    }
}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Read UserCurrentLotteriesScreen.kt and UserCurrentLotteriesViewModel.kt.)

I changed `UserLotteryCard` so it takes the same inputs as the two old cards and does the same thing when the button is pressed:

- **Same parameters:** `lottery`, `imageURL`, `profileId`, plus `buttonLabel`.
- **Same click behavior:** `onClick` is `(Lottery, String) -> Unit`, and the card calls `onClick(lottery, profileId)`, the same as `OngoingLotteryCard`. So `vm::leaveLottery` passes in unchanged. Claim still calls `onToBooking(lottery.id)`.
- **Same width in the list:** `fillMaxWidth(0.9f)`, like the old cards, so the `LazyColumn` looks the same.

**1. `LotteryCard.kt`, which replaces the whole file:**

```kotlin
/**
 * This file contains the lottery card used in currentLotteryScreen
 *
 * AI USE: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-27_UserCurrentLotteryCard.md
 */
package com.example.campsitereservationapp.ui.entrant.CurrentLotteries

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.owner.Lotterylist.formatDateSpan
import com.example.campsitereservationapp.ui.theme.DeleteRed
import com.example.campsitereservationapp.ui.theme.commonButtonColour

/**
 * Lottery Card used in UserCurrentLotteries. Replaces WonLotteryCard and OngoingLotteryCard.
 * Shows the lottery's details in the same format as OwnerLotteryCard, with one button at the
 * bottom: a red "Cancel" button, or a "Claim" button in [commonButtonColour].
 * @param lottery lottery being shown
 * @param imageURL first image in lottery, or "" for none
 * @param profileId document id of the entrant's profile, passed to [onClick]
 * @param buttonLabel either "Cancel" or "Claim"
 * @param onClick action when the button is pressed, given [lottery] and [profileId]
 */
@Composable
fun UserLotteryCard(
    lottery: Lottery,
    imageURL: String,
    profileId: String,
    buttonLabel: String,
    onClick: (Lottery, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(fraction = 0.9f),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserLotteryCardHeader(lottery, imageURL)
            UserLotteryDetails(lottery)
            EntrantsJoinedRow(
                entrantCount = lottery.waitlist.size + lottery.selectedlist.size,
                waitlistCap = lottery.waitlistCapacity
            )
            CancelOrClaimButton(buttonLabel, onClick = { onClick(lottery, profileId) })
        }
    }
}

/** Lottery image (or a calendar icon when there is none) and the title. */
@Composable
private fun UserLotteryCardHeader(lottery: Lottery, imageURL: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            if (imageURL.isNotEmpty()) {
                AsyncImage(
                    model = imageURL,
                    contentDescription = lottery.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            lottery.title.ifBlank { "Untitled lottery" },
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

/** Registration window and booking stay, in a light gray box. */
@Composable
private fun UserLotteryDetails(lottery: Lottery) {
    val stayStart = lottery.dates.minOrNull()
    val stayEnd = lottery.dates.maxOrNull()
    val stay = if (stayStart != null && stayEnd != null) {
        formatDateSpan(stayStart, stayEnd)
    } else {
        "No dates set"
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailRow("Registration Window:", formatDateSpan(lottery.start, lottery.end))
            DetailRow("Booking Stay:", stay)
        }
    }
}

/** A gray label on the left and its value on the right. */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

/** "Entrants Joined" with the number joined, out of the cap when there is one. */
@Composable
private fun EntrantsJoinedRow(entrantCount: Int, waitlistCap: Int?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.Group,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Entrants Joined", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(
            if (waitlistCap != null) "$entrantCount / $waitlistCap" else "$entrantCount",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Full-width button: red for "Cancel", [commonButtonColour] for "Claim". */
@Composable
private fun CancelOrClaimButton(buttonLabel: String, onClick: () -> Unit) {
    val containerColor = if (buttonLabel == "Cancel") DeleteRed else commonButtonColour
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(buttonLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}
```

**2. In `UserCurrentLotteriesScreen.kt`, swap the two calls:**

```kotlin
        if(showWon){
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(vm.wonLotteries.toList()){ lottery ->
                    UserLotteryCard(
                        lottery = lottery,
                        imageURL = vm.getLotteryImage(lottery),
                        profileId = profileId,
                        buttonLabel = "Claim",
                        onClick = { wonLottery, _ -> onToBooking(wonLottery.id) }
                    )
                }
            }
        }
        if(showOngoing){
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(vm.ongoingLotteries.toList()){ lottery ->
                    UserLotteryCard(
                        lottery = lottery,
                        imageURL = vm.getLotteryImage(lottery),
                        profileId = profileId,
                        buttonLabel = "Cancel",
                        onClick = vm::leaveLottery
                    )
                }
            }
        }
```

After the swap, `WonLotteryCard` and `OngoingLotteryCard` aren't used anymore, so you can delete them from the bottom of `UserCurrentLotteriesScreen.kt`.

The one visible change: the ongoing button now says **Cancel** instead of **Leave**, because you asked for that label. It still calls `vm.leaveLottery`.

I have not compiled this or run it in the app.
