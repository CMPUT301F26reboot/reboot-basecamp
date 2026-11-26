/**
 * Test for OwnerWaitListViewModel
 *
 * AI Usage Reference: AI Use Logs - Jacob/ Claude-Chat-Log_2026-11-24_OwnerWaitList.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.ui.owner.WaitList.OwnerWaitlistViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import java.util.Locale

/**
 * Mock tests for [OwnerWaitlistViewModel], simply tests each method and make sure the correct
 * output is returned
 */
class OwnerWaitlistViewModelTest {

    private val firestore = MockFirestore()
    private lateinit var viewModel: OwnerWaitlistViewModel
    private lateinit var defaultLocale: Locale

    //create a mock firebase with 5 entrants in waitlist and 5 entrants in selected list
    private val selectedProfiles = listOf(
        Profile(name = "Alex Morgan", deviceId = "device-alex").apply { id = "device-alex" },
        Profile(name = "Jamie Parker", deviceId = "device-jamie").apply { id = "device-jamie" },
        Profile(name = "Taylor Brooks", deviceId = "device-taylor").apply { id = "device-taylor" },
        Profile(name = "Jordan Ellis", deviceId = "device-jordan").apply { id = "device-jordan" },
        Profile(name = "Casey Bennett", deviceId = "device-casey").apply { id = "device-casey" }
    )
    private val waitlistProfiles = listOf(
        Profile(name = "Riley Chen", deviceId = "device-riley").apply { id = "device-riley" },
        Profile(name = "Morgan Lee", deviceId = "device-morgan").apply { id = "device-morgan" },
        Profile(name = "Sam Patel", deviceId = "device-sam").apply { id = "device-sam" },
        Profile(name = "Drew Wilson", deviceId = "device-drew").apply { id = "device-drew" },
        Profile(name = "", deviceId = "device-blank").apply { id = "device-blank" } // no name set
    )

    /** Registration May 1 to May 31, 2027. Stay July 9 to July 11, 2027. */
    private val julyLottery = Lottery(
        dates = listOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11)),
        start = day(2027, 5, 1),
        end = day(2027, 5, 31)
    ).apply {
        id = "lottery-july"
        title = "July Long Weekend - Banff"
        selectedlist = selectedProfiles.map { it.id }
        waitlist = waitlistProfiles.map { it.id }
    }

    /** A second lottery, so getLottery has to pick the right one. */
    private val augustLottery = Lottery(
        dates = listOf(day(2027, 8, 1)),
        start = day(2027, 6, 1),
        end = day(2027, 6, 30)
    ).apply {
        id = "lottery-august"
        title = "August Long Weekend - Jasper"
    }

    @Before
    fun setUp() {
        // formatDateRange uses the default locale, so fix it for the expected strings
        defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)

        firestore.install()
        viewModel = OwnerWaitlistViewModel()
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery, augustLottery))
        firestore.deliver("Profiles", Profile::class.java, selectedProfiles + waitlistProfiles)
    }

    @After
    fun tearDown() {
        firestore.uninstall()
        Locale.setDefault(defaultLocale)
    }

    //test getLottery
    @Test
    fun getLottery_existingID_returnsThatLottery() {
        assertSame(julyLottery, viewModel.getLottery("lottery-july"))
        assertSame(augustLottery, viewModel.getLottery("lottery-august"))
    }

    @Test
    fun getLottery_existingID_hasFiveSelectedAndFiveWaiting() {
        val lottery = viewModel.getLottery("lottery-july")!!

        assertEquals(5, lottery.selectedlist.size)
        assertEquals(5, lottery.waitlist.size)
    }

    @Test
    fun getLottery_unknownID_returnsNull() {
        assertNull(viewModel.getLottery("lottery-missing"))
    }

    //test getFormatedRegistrationWindowDates
    @Test
    fun getFormatedRegistrationWindowDates_returnsStartToEnd() {
        val result = viewModel.getFormatedRegistrationWindowDates(julyLottery)

        assertEquals("May 1, 2027 – May 31, 2027", result)
    }

    @Test
    fun getFormatedRegistrationWindowDates_nullLottery_returnsNull() {
        assertNull(viewModel.getFormatedRegistrationWindowDates(null))
    }

    //test getFormatedBookingStayDatesDates
    @Test
    fun getFormatedBookingStayDatesDates_returnsFirstToLastDay() {
        val result = viewModel.getFormatedBookingStayDatesDates(julyLottery)

        assertEquals("Jul 9, 2027 – Jul 11, 2027", result)
    }

    @Test
    fun getFormatedBookingStayDatesDates_singleDay_returnsOneDate() {
        val result = viewModel.getFormatedBookingStayDatesDates(augustLottery)

        assertEquals("Aug 1, 2027", result)
    }

    @Test
    fun getFormatedBookingStayDatesDates_nullLottery_returnsNull() {
        assertNull(viewModel.getFormatedBookingStayDatesDates(null))
    }

    //test getEntrantName
    @Test
    fun getEntrantName_selectedListEntrants_returnNames() {
        val names = julyLottery.selectedlist.map { viewModel.getEntrantName(it) }

        assertEquals(
            listOf("Alex Morgan", "Jamie Parker", "Taylor Brooks", "Jordan Ellis", "Casey Bennett"),
            names
        )
    }

    @Test
    fun getEntrantName_waitlistEntrants_returnNames() {
        val names = julyLottery.waitlist.map { viewModel.getEntrantName(it) }

        assertEquals(
            listOf("Riley Chen", "Morgan Lee", "Sam Patel", "Drew Wilson", "device-blank"),
            names
        )
    }

    @Test
    fun getEntrantName_blankName_returnsID() {
        assertEquals("device-blank", viewModel.getEntrantName("device-blank"))
    }

    @Test
    fun getEntrantName_noProfile_returnsID() {
        assertEquals("device-unknown", viewModel.getEntrantName("device-unknown"))
    }
}