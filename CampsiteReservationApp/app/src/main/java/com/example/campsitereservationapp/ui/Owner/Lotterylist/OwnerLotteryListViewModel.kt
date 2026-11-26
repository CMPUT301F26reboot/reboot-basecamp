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
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Supplies the owner's lotteries to [OwnerLotteryListScreen] and remembers which filter tab is
 * selected. A lottery belongs to the owner when it runs on at least one of the owner's campsites,
 * since [Lottery] has no owner ID of its own.
 *
 * Outstanding issues:
 *  - No claim count for completed lotteries yet; winners can't book from a lottery until that
 *    flow exists - since also the winners automatically selected also is not implemented
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
        val ownerListings = _listings.filter { it.ownerID == ownerId }.associateBy { it.id }
        return _lotteries
            .filter { lottery -> lottery.campsiteIDs.any { it in ownerListings } }
            .map { toOwnerLotteryItem(it, ownerListings, now) }
            .sortedWith(compareBy({ it.status.ordinal }, { it.registrationEnd }))
    }
}

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
    val timeLeftLabel: String
)

/**
 * Builds the card data for [lottery].
 *
 * @param ownerListings the owner's campsites, keyed by ID, used for the campsite name.
 * @param now the current time, used for the status and countdown.
 */
internal fun toOwnerLotteryItem(
    lottery: Lottery,
    ownerListings: Map<String, Listing>,
    now: Date
): OwnerLotteryItem {
    val status = when {
        lottery.cancelled -> LotteryStatus.CANCELLED
        now.after(lottery.end.endOfDay()) -> LotteryStatus.COMPLETED
        else -> LotteryStatus.ACTIVE
    }
    val campsiteNames = lottery.campsiteIDs.mapNotNull { ownerListings[it]?.name }

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
        timeLeftLabel = timeLeftLabel(lottery.start.toDayOnly(), lottery.end.endOfDay(), now)
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
