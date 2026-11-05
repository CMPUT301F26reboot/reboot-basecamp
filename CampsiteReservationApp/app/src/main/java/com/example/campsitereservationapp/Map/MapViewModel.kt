package com.example.campsitereservationapp.Map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.state.MapState

/** Backend of ViewModel, calls [makeTileStreamProvider] for Tile Map information, converts latitude
 * and longitude double to match TilePyramid dimensions defined in MapCompose
 *
 */
class MapViewModel : ViewModel() {

    private val tileStreamProvider = makeTileStreamProvider() //gets tiles from
    private val tileSize = 256 //needed for mapSize calculation
    private val maxLevel = 16 //max zoom out lvl
    private val mapSize = tileSize * 2.0.pow(maxLevel).toInt()
    private val location = latLonToNormalized(53.5232, -113.5263) // University of Alberta, <- default location
    private val x = location.first
    private val y = location.second

    val state = MapState(
        levelCount = maxLevel + 1,
        fullWidth = mapSize,
        fullHeight = mapSize,
        workerCount = 16
    ) {
        scroll(x, y)
        scale(1 / 2.0.pow(maxLevel - 7))
    }.apply {
        addLayer(tileStreamProvider)
    }
    //moves map center to new location
    fun moveTo(latitude: Double, longitude: Double) {
        val loc = latLonToNormalized(latitude, longitude)
        viewModelScope.launch { //Background Coroutine,
            state.scrollTo(
                loc.first, //sets x position
                loc.second, //sets y position
                state.scale) // keeps current zoom scale intact while moving
        }
    }

//    //zoom in/out buttons
//    fun zoomIn() {
//        viewModelScope.launch {
//            state.scrollTo(state.centroidX, state.centroidY, state.scale * 2.0)
//        }
//    }
//
//    fun zoomOut() {
//        viewModelScope.launch {
//            state.scrollTo(state.centroidX, state.centroidY, state.scale / 2.0)
//        }
//    }
}

/** Connects to https://www.openstreetmap.org/ and returns specified tiles needed for map
 *
 */
private fun makeTileStreamProvider() = TileStreamProvider { row, col, zoomLvl ->
    runCatching {
        // 1. Creates URL object in memory (NO network activity yet)
        val url = URL("https://tile.openstreetmap.org/$zoomLvl/$col/$row.png")
        // 2. Prepares the connection object
        val connection = url.openConnection() as HttpURLConnection
        // According to the official OpenStreetMap Tile Usage Policy,
        // production apps should use a unique User-Agent that identifies the application
        // (for example: "MapComposeTestApp/1.0 (contact@yourdomain.com)"
        //
        connection.setRequestProperty("User-Agent", "MapComposeTestApp/1.0 (contact@yourdomain.com)")
        connection.doInput = true // flag to allow reading data from the server's response stream
        // 3. ACTUAL NETWORK REQUEST HAPPENS HERE (connects over socket to internet)
        connection.connect()
        BufferedInputStream(connection.inputStream) //add a memory buffer in RAM
    }.getOrNull() //incase something goes wrong, just return null not crash
}

/** Normalization function for viewmodel
 *
 */
private fun latLonToNormalized(latitude: Double, longitude: Double): Pair<Double, Double> {
    //converts lat/lon (3D earth sphere) to normalized (2D tile)
    val x = (longitude + 180.0) / 360.0
    val latRad = Math.toRadians(latitude)
    val y = (1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0
    return Pair(x, y)
}