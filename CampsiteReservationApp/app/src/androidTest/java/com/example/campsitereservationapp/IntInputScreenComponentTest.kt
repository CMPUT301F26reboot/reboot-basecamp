/**
 * Test for IntInputScreenComponent:
 * plus and minus buttons, typed input filtering, and the optional title and subheader
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.campsitereservationapp.ui.components.IntInputScreenComponent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntInputScreenComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val minus = "\u2212" // the "−" sign on the minus button (not a hyphen)

    // the caller's number, updated by onNumChange like a real screen would
    private var num by mutableIntStateOf(0)

    private fun show(start: Int) {
        num = start
        composeRule.setContent {
            IntInputScreenComponent(
                title = "Guests",
                subHeader = "Max 6",
                num = num,
                onNumChange = { num = it }
            )
        }
    }

    private fun field() = composeRule.onNode(hasSetTextAction())

    @Test
    fun showsTitleAndSubHeader() {
        show(2)

        composeRule.onNodeWithText("Guests").assertIsDisplayed()
        composeRule.onNodeWithText("Max 6").assertIsDisplayed()
    }

    @Test
    fun plus_increasesByOne() {
        show(2)

        composeRule.onNodeWithText("+").performClick()

        assertEquals(3, num)
        field().assertTextEquals("3")
    }

    @Test
    fun minus_decreasesByOne() {
        show(2)

        composeRule.onNodeWithText(minus).performClick()

        assertEquals(1, num)
        field().assertTextEquals("1")
    }

    @Test
    fun minus_disabledAtZero() {
        show(0)

        composeRule.onNodeWithText(minus).assertIsNotEnabled()
    }

    @Test
    fun typing_keepsOnlyDigits() {
        show(0)

        field().performTextReplacement("1a2b")

        assertEquals(12, num)
        field().assertTextEquals("12")
    }

    @Test
    fun typing_dropsLeadingZeros() {
        show(0)

        field().performTextReplacement("007")

        assertEquals(7, num)
        field().assertTextEquals("7")
    }

    @Test
    fun typing_limitsToNineDigits() {
        show(0)

        field().performTextReplacement("12345678901")

        assertEquals(123456789, num)
    }

    @Test
    fun clearing_reportsZeroAndLeavesFieldEmpty() {
        show(5)

        field().performTextClearance()

        assertEquals(0, num)
        field().assertTextEquals("")
    }
}