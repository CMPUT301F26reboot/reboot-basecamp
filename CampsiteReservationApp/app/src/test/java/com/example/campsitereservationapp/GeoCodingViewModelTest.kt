/**
 * Test file for GeoCodingViewModel, compares if lat/long is the same when searching on openstreetmap.org
 *
 *  AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.Map.GeocodingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GeoCodingViewModelTest {

    //get lat/lon from OpenStreetMap manually
    val banffCoor = Pair(51.17515, -115.57212)
    val UofACoor = Pair(53.526948, -113.524899)
    val ETLCCoor = Pair(53.527379, -113.529153)

    //allowable variance (manual variable may not be exact)
    val variance = 0.0005

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GeocodingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)   // gives viewModelScope a Main dispatcher
        viewModel = GeocodingViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun check_if_returnCorrectCoordinateUofA() = runTest(testDispatcher) {
        viewModel.onQueryChange("University of Alberta")
        // waits until the search finishes; the 1 second delay is skipped by the test dispatcher
        val address = viewModel.suggestions.first { it.isNotEmpty() }[0]

        viewModel.onAddressSelected(address)
        // waits until coordinates are set
        val coord = viewModel.coordinates.filterNotNull().first()

        assertEquals(UofACoor.first, coord.first, variance)   // latitude
        assertEquals(UofACoor.second, coord.second, variance) // longitude
    }

    @Test
    fun check_if_returnCorrectCoordinateBanff() = runTest(testDispatcher) {
        viewModel.onQueryChange("Banff")
        // waits until the search finishes; the 1 second delay is skipped by the test dispatcher
        val address = viewModel.suggestions.first { it.isNotEmpty() }[0]

        viewModel.onAddressSelected(address)
        // waits until coordinates are set
        val coord = viewModel.coordinates.filterNotNull().first()

        assertEquals(banffCoor.first, coord.first, variance)   // latitude
        assertEquals(banffCoor.second, coord.second, variance) // longitude
    }

    @Test
    fun check_if_returnCorrectCoordinateETLC() = runTest(testDispatcher) {
        viewModel.onQueryChange("ETLC")
        // waits until the search finishes; the 1 second delay is skipped by the test dispatcher
        val address = viewModel.suggestions.first { it.isNotEmpty() }[0]

        viewModel.onAddressSelected(address)
        // waits until coordinates are set
        val coord = viewModel.coordinates.filterNotNull().first()

        assertEquals(ETLCCoor.first, coord.first, variance)   // latitude
        assertEquals(ETLCCoor.second, coord.second, variance) // longitude
    }
}