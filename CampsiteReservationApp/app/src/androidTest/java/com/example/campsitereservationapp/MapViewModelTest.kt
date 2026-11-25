/**
 * Test file for MapViewModel, Needs to use emulator for internet access (getting the tiles),
 * and moving map
 *
 * Tests:
 * - test normalization by using lat/long -> noramlized -> lat/long, values should be the same
 * - test moveTo by asserting if mapstate location is at the new coordinates
 * - test tile stream provider and make sure it returns a tile
 *
 * AI Refrence: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.campsitereservationapp.Map.MapViewModel
import com.example.campsitereservationapp.Map.latLonToNormalized
import com.example.campsitereservationapp.Map.makeTileStreamProvider
import com.example.campsitereservationapp.Map.normalizedToLatLon
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ovh.plrapps.mapcompose.api.centroidX
import ovh.plrapps.mapcompose.api.centroidY
import ovh.plrapps.mapcompose.ui.MapUI
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class MapViewModelTest {

    @get:Rule
    val composeRule = createComposeRule()

    //get lat/lon from OpenStreetMap manually
    private val banffCoor = Pair(51.17515, -115.57212)
    private val UofACoor = Pair(53.526948, -113.524899)
    private val ETLCCoor = Pair(53.527379, -113.529153)

    @Test
    fun normalization_roundTrip_returnsSameLatLon() {
        for ((lat, lon) in listOf(banffCoor, UofACoor, ETLCCoor)) {
            val (x, y) = latLonToNormalized(lat, lon)
            val (lat2, lon2) = normalizedToLatLon(x, y)
            assertEquals(lat, lat2, 1e-9)
            assertEquals(lon, lon2, 1e-9)
        }
    }

    @Test
    fun moveTo_centersMapOnNewCoordinates() {
        val viewModel = composeRule.runOnUiThread { MapViewModel() }
        // MapState only scrolls after it is laid out, so put the map on screen
        composeRule.setContent {
            MapUI(Modifier.fillMaxSize(), state = viewModel.state)
        }
        composeRule.runOnUiThread { viewModel.moveTo(banffCoor.first, banffCoor.second) }

        val (x, y) = latLonToNormalized(banffCoor.first, banffCoor.second)
        // waits for the scroll animation; fails if the center is not there within 5 seconds
        composeRule.waitUntil(timeoutMillis = 5_000) {
            abs(viewModel.state.centroidX - x) < 1e-5 &&
                    abs(viewModel.state.centroidY - y) < 1e-5
        }
    }

    @Test
    fun tileStreamProvider_returnsTile() = runBlocking {
        // zoom 0, row 0, col 0 is the single tile that shows the whole world, so it always exists
        val stream = makeTileStreamProvider().getTileStream(0, 0, 0)
        assertNotNull(stream)
        val bytes = stream!!.use { it.readBytes() }
        assertTrue(bytes.isNotEmpty())
    }
}