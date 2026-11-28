/**
 * This file contains the Owner Lottery List View Model
 */
package com.example.campsitereservationapp.ui.owner.Lotterylist

import com.example.campsitereservationapp.utils.toDayOnly
import com.example.campsitereservationapp.utils.endOfDay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.owner.enrolledCount
import com.google.firebase.firestore.FieldValue
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Supplies the owner's lotteries to [OwnerLotteryListScreen] and remembers which filter tab is
 * selected. A lottery belongs to the owner when they created it, or when it runs on at least one
 * of their campsites (a lottery they joined from another owner's invitation). Older lotteries
 * have no [Lottery.ownerID], so any owner with a campsite in them counts as their creator.
 *
 * Also lists the invitations waiting for this owner, and lets them accept or decline an
 * invitation, or withdraw their campsites from a lottery they joined. Invitations are shown in
 * the app (a banner on the lottery list and [OwnerLotteryInvitationsScreen]) rather than sent as
 * notifications.
 */
class OwnerLotteryListViewModel : DatabaseModel() {

    /** The filter tab currently selected. */
    var selectedFilter by mutableStateOf(LotteryFilter.ALL)
        private set

    /** Selects [filter] so only lotteries matching it are shown. */
    fun selectFilter(filter: LotteryFilter) {
        selectedFilter = filter
    }

    /**
     * Returns the lotteries on [ownerId]'s campsites as [OwnerLotteryItem]s, active ones first.
     * Reads the live Firestore lists, so a screen calling it updates when the data changes.
     *
     * @param now the current time, used to decide whether each lottery is still open.
     */
    fun ownerLotteries(ownerId: String, now: Date = Date()): List<OwnerLotteryItem> {
        val listings = _listings.associateBy { it.id }
        return _lotteries
            .filter { lottery ->
                lottery.ownerID == ownerId ||
                    lottery.campsiteIDs.any { listings[it]?.ownerID == ownerId }
            }
            .map { toOwnerLotteryItem(it, listings, now, ownerId) }
            .sortedWith(compareBy({ it.status.ordinal }, { it.registrationEnd }))
    }

    /**
     * Invitations waiting for [ownerId]: one for each of their campsites another owner invited
     * to a lottery that isn't cancelled and is still taking entrants.
     *
     * @param now the current time; invitations to lotteries whose registration has ended are hidden.
     */
    fun invitations(ownerId: String, now: Date = Date()): List<LotteryInvite> {
        val listings = _listings.associateBy { it.id }
        return _lotteries
            .filter { !it.cancelled && !now.after(it.end.endOfDay()) }
            .flatMap { lottery ->
                lottery.pendingCampsiteIDs
                    .mapNotNull { listings[it] }
                    .filter { it.ownerID == ownerId }
                    .map { campsite -> toLotteryInvite(lottery, campsite, listings) }
            }
            .sortedBy { it.registrationEnd }
    }

    /**
     * Accepts the invitation of campsite [campsiteId] to lottery [lotteryId]. The campsite moves
     * from the pending list into the lottery in one update, so entrants can see it straight away.
     */
    fun acceptInvite(lotteryId: String, campsiteId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val changes = mapOf(
            "pendingCampsiteIDs" to FieldValue.arrayRemove(campsiteId),
            "campsiteIDs" to FieldValue.arrayUnion(campsiteId)
        )
        lotteriesRef.document(lotteryId).update(changes)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    /** Declines the invitation of campsite [campsiteId], so it never joins lottery [lotteryId]. */
    fun declineInvite(lotteryId: String, campsiteId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        lotteriesRef.document(lotteryId).update("pendingCampsiteIDs", FieldValue.arrayRemove(campsiteId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    /**
     * Removes [campsiteIds] (the participant's own campsites) from lottery [lotteryId]. Entrants
     * already on the waitlist stay on it, since they joined the lottery, not one campsite.
     * The screen only offers this while registration is open, before the draw.
     */
    fun withdrawCampsites(lotteryId: String, campsiteIds: List<String>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        lotteriesRef.document(lotteryId).update("campsiteIDs", FieldValue.arrayRemove(*campsiteIds.toTypedArray()))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}

/**
 * One invitation for the owner to add their campsite to another owner's lottery.
 *
 * @property campsiteId the owner's campsite that was invited.
 * @property hostName name of the owner who created the lottery.
 */
data class LotteryInvite(
    val lotteryId: String,
    val title: String,
    val hostName: String,
    val campsiteId: String,
    val campsiteName: String,
    val registrationStart: Date,
    val registrationEnd: Date,
    val stayStart: Date?,
    val stayEnd: Date?
)

/** Builds the invitation card data for [campsite] being invited to [lottery]. */
internal fun toLotteryInvite(lottery: Lottery, campsite: Listing, listings: Map<String, Listing>) =
    LotteryInvite(
        lotteryId = lottery.id,
        title = lottery.title.ifBlank { "Untitled lottery" },
        hostName = hostName(lottery, listings),
        campsiteId = campsite.id,
        campsiteName = campsite.name,
        registrationStart = lottery.start,
        registrationEnd = lottery.end,
        stayStart = lottery.dates.minOrNull(),
        stayEnd = lottery.dates.maxOrNull()
    )

/**
 * Name of the owner who created [lottery], taken from one of their listings, since only listings
 * store owner names. Falls back to "Another owner" when it can't be found.
 */
internal fun hostName(lottery: Lottery, listings: Map<String, Listing>): String =
    listings.values.firstOrNull { it.ownerID == lottery.ownerID && lottery.ownerID.isNotBlank() }
        ?.ownerName?.ifBlank { null } ?: "Another owner"

/** Where a lottery is in its cycle, The order is the order cards are listed in. */
enum class LotteryStatus { ACTIVE, COMPLETED, CANCELLED }

/**
 * The filter tabs at the top of the screen.
 *
 * @property label text shown on the tab, before the count.
 * @property status the status this tab shows, or null to show every lottery.
 */
enum class LotteryFilter(val label: String, val status: LotteryStatus?) {
    ALL("All", null),
    ACTIVE("Active", LotteryStatus.ACTIVE),
    COMPLETED("Completed", LotteryStatus.COMPLETED),
    CANCELLED("Cancelled", LotteryStatus.CANCELLED);

    /** True when [item] belongs under this tab. */
    fun matches(item: OwnerLotteryItem): Boolean = status == null || item.status == status
}

/**
 * Everything one lottery card shows, worked out from a [Lottery] and the owner's data.
 *
 * @property campsiteName the lottery's campsite, or the first campsite and how many more.
 * @property stayStart first night of the booking stay, or null if the lottery has no dates.
 * @property stayEnd last night of the booking stay, or null if the lottery has no dates.
 * @property winnersToDraw how many winners the owner asked for, or null if not set.
 * @property waitlistCap the most entrants that can join, or null for no limit.
 * @property entrantCount entrants still waiting plus entrants already drawn.
 * @property winnersDrawn entrants picked in the draw so far.
 * @property timeLeftLabel short countdown for an active lottery, such as "18h left".
 */
data class OwnerLotteryItem(
    val id: String,
    val title: String,
    val campsiteName: String,
    val status: LotteryStatus,
    val registrationStart: Date,
    val registrationEnd: Date,
    val stayStart: Date?,
    val stayEnd: Date?,
    val winnersToDraw: Int?,
    val waitlistCap: Int?,
    val entrantCount: Int,
    val winnersDrawn: Int,
    val timeLeftLabel: String,
    val isCreator: Boolean = true,
    val hostName: String = "",
    val myCampsiteIDs: List<String> = emptyList()
)

/**
 * Builds the card data for [lottery].
 *
 * @param listings every campsite, keyed by ID, used for campsite names and who owns them.
 * @param now the current time, used for the status and countdown.
 * @param ownerId the owner viewing the list, used to tell whether they created the lottery.
 */
internal fun toOwnerLotteryItem(
    lottery: Lottery,
    listings: Map<String, Listing>,
    now: Date,
    ownerId: String = ""
): OwnerLotteryItem {
    val status = when {
        lottery.cancelled -> LotteryStatus.CANCELLED
        now.after(lottery.end.endOfDay()) -> LotteryStatus.COMPLETED
        else -> LotteryStatus.ACTIVE
    }
    val campsiteNames = lottery.campsiteIDs.mapNotNull { listings[it]?.name }
    val isCreator = lottery.ownerID.isBlank() || lottery.ownerID == ownerId

    return OwnerLotteryItem(
        id = lottery.id,
        title = lottery.title.ifBlank { "Untitled lottery" },
        campsiteName = when (campsiteNames.size) {
            0 -> "Unknown campsite"
            1 -> campsiteNames.first()
            else -> "${campsiteNames.first()} +${campsiteNames.size - 1} more"
        },
        status = status,
        registrationStart = lottery.start,
        registrationEnd = lottery.end,
        stayStart = lottery.dates.minOrNull(),
        stayEnd = lottery.dates.maxOrNull(),
        winnersToDraw = lottery.lotteryWinners,
        waitlistCap = lottery.waitlistCapacity,
        entrantCount = enrolledCount(lottery),
        winnersDrawn = lottery.selectedlist.size,
        timeLeftLabel = timeLeftLabel(lottery.start.toDayOnly(), lottery.end.endOfDay(), now),
        isCreator = isCreator,
        hostName = if (isCreator) "" else hostName(lottery, listings),
        myCampsiteIDs = lottery.campsiteIDs.filter { listings[it]?.ownerID == ownerId }
    )
}

/**
 * Short countdown for a lottery's registration: "Opens in 2d" before it starts, then "3d left", Empty once registration has closed.
 */
internal fun timeLeftLabel(start: Date, end: Date, now: Date): String {
    if (now.after(end)) return ""
    val opening = now.before(start)
    val millis = (if (opening) start.time else end.time) - now.time
    val days = TimeUnit.MILLISECONDS.toDays(millis)
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val amount = when {
        days >= 1 -> "${days}d"
        hours >= 1 -> "${hours}h"
        else -> "<1h"
    }
    return if (opening) "Opens in $amount" else "$amount left"
}
