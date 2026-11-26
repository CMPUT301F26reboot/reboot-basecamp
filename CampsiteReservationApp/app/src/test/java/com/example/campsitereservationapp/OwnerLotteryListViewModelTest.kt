/**
 * Test for OwnerLotteryListViewModel
 *
 * Tests:
 * - filter: starts at All, selectFilter changes it, and each tab (LotteryFilter.matches)
 *   matches only its own status, while All matches every lottery
 *
 * - ownerLotteries: returns only lotteries on the owner's campsites, sorted active,
 *   completed, then canceled, and by registration end within each status
 *
 * - toOwnerLotteryItem: canceled wins over ended, completed after the end, active before it;
 *   campsite name (one name, "+N more", or "Unknown campsite"); blank title becomes
 *   "Untitled lottery"; stay dates, winners to draw, waitlist cap, entrant count, and winners
 *   drawn are copied, and are null when the lottery has no dates or limits
 *
 * - timeLeftLabel: "Opens in 2d" before registration starts, "3d left", "18h left", or
 *   "<1h left" while open, and empty after it ends
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.owner.Lotterylist.LotteryFilter
import com.example.campsitereservationapp.ui.owner.Lotterylist.LotteryStatus
import com.example.campsitereservationapp.ui.owner.Lotterylist.OwnerLotteryListViewModel
import com.example.campsitereservationapp.ui.owner.Lotterylist.timeLeftLabel
import com.example.campsitereservationapp.ui.owner.Lotterylist.toOwnerLotteryItem
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date
import java.util.concurrent.TimeUnit

class OwnerLotteryListViewModelTest {

    private val ownerId = "owner-device-7f3a91c2"

    private val twoJack = Listing(name = "Two Jack Lakeside", ownerID = ownerId).apply { id = "listing-two-jack" }
    private val tunnelMountain = Listing(name = "Tunnel Mountain Village II", ownerID = ownerId).apply { id = "listing-tunnel-mtn" }
    private val lakeLouise = Listing(name = "Lake Louise Hard-Sided", ownerID = "owner-device-22be0d14").apply { id = "listing-lake-louise" }
    private val ownerListings = mapOf(twoJack.id to twoJack, tunnelMountain.id to tunnelMountain)

    /** Fixed "current time" so status and countdown don't depend on when the test runs. */
    private val now = hoursAfter(day(2027, 5, 10), 12)

    private val firestore = MockFirestore()
    private lateinit var viewModel: OwnerLotteryListViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = OwnerLotteryListViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain, lakeLouise))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    private fun hoursAfter(date: Date, hours: Long) = Date(date.time + TimeUnit.HOURS.toMillis(hours))

    /** A lottery that is open by default: started 1 day ago, ends in 2 days. */
    private fun lottery(
        id: String,
        campsiteIDs: List<String>,
        start: Date = hoursAfter(now, -24),
        end: Date = hoursAfter(now, 48),
        cancelled: Boolean = false
    ) = Lottery(start = start, end = end, campsiteIDs = campsiteIDs).apply {
        this.id = id
        this.title = "Lottery $id"
        this.cancelled = cancelled
    }

    // ---------- selectFilter / LotteryFilter ----------

    @Test
    fun selectedFilter_startsAtAll() {
        assertEquals(LotteryFilter.ALL, viewModel.selectedFilter)
    }

    @Test
    fun selectFilter_changesSelectedFilter() {
        viewModel.selectFilter(LotteryFilter.COMPLETED)

        assertEquals(LotteryFilter.COMPLETED, viewModel.selectedFilter)
    }

    @Test
    fun lotteryFilter_matchesOnlyItsStatus_allMatchesEverything() {
        val active = toOwnerLotteryItem(lottery("a", listOf(twoJack.id)), ownerListings, now)
        val cancelled = toOwnerLotteryItem(lottery("c", listOf(twoJack.id), cancelled = true), ownerListings, now)

        assertTrue(LotteryFilter.ALL.matches(active))
        assertTrue(LotteryFilter.ALL.matches(cancelled))
        assertTrue(LotteryFilter.ACTIVE.matches(active))
        assertFalse(LotteryFilter.ACTIVE.matches(cancelled))
        assertTrue(LotteryFilter.CANCELLED.matches(cancelled))
        assertFalse(LotteryFilter.COMPLETED.matches(active))
    }

    // ---------- ownerLotteries ----------

    @Test
    fun ownerLotteries_onlyReturnsLotteriesOnOwnersCampsites() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(
            lottery("mine", listOf(twoJack.id)),
            lottery("shared", listOf(lakeLouise.id, tunnelMountain.id)), // 1 of the owner's campsites
            lottery("other", listOf(lakeLouise.id))                      // none of the owner's campsites
        ))

        val ids = viewModel.ownerLotteries(ownerId, now).map { it.id }

        assertEquals(setOf("mine", "shared"), ids.toSet())
    }

    @Test
    fun ownerLotteries_sortsActiveCompletedCancelled_thenByRegistrationEnd() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(
            lottery("cancelled", listOf(twoJack.id), cancelled = true),
            lottery("completed", listOf(twoJack.id), start = hoursAfter(now, -72), end = hoursAfter(now, -24)),
            lottery("activeLate", listOf(twoJack.id), end = hoursAfter(now, 72)),
            lottery("activeSoon", listOf(twoJack.id), end = hoursAfter(now, 24))
        ))

        val ids = viewModel.ownerLotteries(ownerId, now).map { it.id }

        assertEquals(listOf("activeSoon", "activeLate", "completed", "cancelled"), ids)
    }

    // ---------- toOwnerLotteryItem ----------

    @Test
    fun status_cancelledWinsEvenAfterEnd() {
        val ended = lottery("x", listOf(twoJack.id), start = hoursAfter(now, -72), end = hoursAfter(now, -24), cancelled = true)

        assertEquals(LotteryStatus.CANCELLED, toOwnerLotteryItem(ended, ownerListings, now).status)
    }

    @Test
    fun status_completedAfterEnd_activeBeforeEnd() {
        val ended = lottery("x", listOf(twoJack.id), start = hoursAfter(now, -72), end = hoursAfter(now, -24))
        val open = lottery("y", listOf(twoJack.id))

        assertEquals(LotteryStatus.COMPLETED, toOwnerLotteryItem(ended, ownerListings, now).status)
        assertEquals(LotteryStatus.ACTIVE, toOwnerLotteryItem(open, ownerListings, now).status)
    }

    @Test
    fun campsiteName_oneName_moreCount_orUnknown() {
        val one = lottery("one", listOf(twoJack.id))
        val two = lottery("two", listOf(twoJack.id, tunnelMountain.id))
        val unknown = lottery("unknown", listOf(lakeLouise.id))

        assertEquals("Two Jack Lakeside", toOwnerLotteryItem(one, ownerListings, now).campsiteName)
        assertEquals("Two Jack Lakeside +1 more", toOwnerLotteryItem(two, ownerListings, now).campsiteName)
        assertEquals("Unknown campsite", toOwnerLotteryItem(unknown, ownerListings, now).campsiteName)
    }

    @Test
    fun title_blank_becomesUntitledLottery() {
        val blank = lottery("x", listOf(twoJack.id)).apply { title = "  " }

        assertEquals("Untitled lottery", toOwnerLotteryItem(blank, ownerListings, now).title)
    }

    @Test
    fun copiesDatesLimitsAndCounts() {
        val full = lottery("x", listOf(twoJack.id)).apply {
            dates = listOf(day(2027, 7, 11), day(2027, 7, 9), day(2027, 7, 10))
            lotteryWinners = 10
            waitlistCapacity = 50
            waitlist = listOf("entrant-1", "entrant-2", "entrant-3")
            selectedlist = listOf("entrant-4")
        }

        val item = toOwnerLotteryItem(full, ownerListings, now)

        assertEquals("x", item.id)
        assertEquals(full.start, item.registrationStart)
        assertEquals(full.end, item.registrationEnd)
        assertEquals(day(2027, 7, 9), item.stayStart)   // earliest date
        assertEquals(day(2027, 7, 11), item.stayEnd)    // latest date
        assertEquals(10, item.winnersToDraw)
        assertEquals(50, item.waitlistCap)
        assertEquals(4, item.entrantCount)              // 3 waiting + 1 drawn
        assertEquals(1, item.winnersDrawn)
    }

    @Test
    fun noDatesOrLimits_givesNulls() {
        val item = toOwnerLotteryItem(lottery("x", listOf(twoJack.id)), ownerListings, now)

        assertNull(item.stayStart)
        assertNull(item.stayEnd)
        assertNull(item.winnersToDraw)
        assertNull(item.waitlistCap)
    }

    // ---------- timeLeftLabel ----------

    @Test
    fun timeLeftLabel_beforeStart_showsOpensIn() {
        assertEquals("Opens in 2d", timeLeftLabel(hoursAfter(now, 49), hoursAfter(now, 120), now))
    }

    @Test
    fun timeLeftLabel_open_showsDaysHoursOrUnderOneHour() {
        val started = hoursAfter(now, -24)

        assertEquals("3d left", timeLeftLabel(started, hoursAfter(now, 74), now))
        assertEquals("18h left", timeLeftLabel(started, hoursAfter(now, 18), now))
        assertEquals("<1h left", timeLeftLabel(started, Date(now.time + TimeUnit.MINUTES.toMillis(30)), now))
    }

    @Test
    fun timeLeftLabel_afterEnd_isEmpty() {
        assertEquals("", timeLeftLabel(hoursAfter(now, -72), hoursAfter(now, -1), now))
    }
}