package com.example.campsitereservationapp.Map

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import ovh.plrapps.mapcompose.api.addMarker
import ovh.plrapps.mapcompose.api.hasMarker
import ovh.plrapps.mapcompose.api.moveMarker
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.state.MapState
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import ovh.plrapps.mapcompose.api.onTap
import kotlin.math.atan
import kotlin.math.sinh

/** Backend of ViewModel, calls [makeTileStreamProvider] for Tile Map information, converts latitude
 * and longitude double to match TilePyramid dimensions defined in MapCompose
 *
 */
class MapViewModel : ViewModel() {
    companion object {
        //Marker nametag (MapCompose find markers by string), (used when handling multiple markers)
        private const val LOCATION_MARKER_ID = "location"
    }
    private val initialZoomLVL = 12// initial zoom level, range: (4-13) (Global-Neighborhood)
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
        scale(1 / 2.0.pow(maxLevel - initialZoomLVL))
    }.apply {
        addLayer(tileStreamProvider)
    }
    /** (latitude, longitude) of the last dropped pin, or null if none dropped yet. */
    var droppedPin: Pair<Double, Double>? by mutableStateOf(null)
        private set

    /** True while waiting for the user to tap where the pin should go. */
    var isPinDropMode by mutableStateOf(false)
        private set

    init {
        //EVENT 2: when in pin-drop mode, the next tap drops the pin
        state.onTap { x, y ->
            if (isPinDropMode) {
                placeMarker(x, y)
                droppedPin = normalizedToLatLon(x, y)
                isPinDropMode = false
            }
        }
    }

    /** EVENT 2 trigger: the next tap on the map drops a pin. */
    fun startPinDrop() {
        isPinDropMode = true
    }

    /** EVENT 1: moves the map to the device's current GPS position.
     * Only call after location permission is granted (see MapScreenComponent). */
    @SuppressLint("MissingPermission")
    fun moveToCurrentLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationManager.GPS_PROVIDER
        } else {
            LocationManager.NETWORK_PROVIDER
        }
        LocationManagerCompat.getCurrentLocation(
            locationManager,
            provider,
            CancellationSignal(),
            ContextCompat.getMainExecutor(context) //result arrives on main thread, as MapCompose needs
        ) { location ->
            if (location != null) { //null when no fix (e.g. location turned off)
                moveTo(location.latitude, location.longitude)
            }
        }
    }


    //moves map center to new location and pins a marker there
    fun moveTo(latitude: Double, longitude: Double) {
        val loc = latLonToNormalized(latitude, longitude)
        placeMarker(loc.first, loc.second)
        viewModelScope.launch { //Background Coroutine,
            state.scrollTo(
                loc.first, //sets x position
                loc.second, //sets y position
                state.scale) // keeps current zoom scale intact while moving
        }
    }

    //adds the marker the first time, then moves it on later calls
    private fun placeMarker(x: Double, y: Double) {
        if (state.hasMarker(LOCATION_MARKER_ID)) {
            state.moveMarker(LOCATION_MARKER_ID, x, y)
        } else {
            state.addMarker(LOCATION_MARKER_ID, x, y) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Campsite location",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFD32F2F)
                )
            }
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
        connection.setRequestProperty("User-Agent", "MapComposeTestApp/1.0 (templado@ualberta.ca)")
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

/** Reverse of latLonToNormalized: normalized (2D tile) to lat/lon */
private fun normalizedToLatLon(x: Double, y: Double): Pair<Double, Double> {
    val longitude = x * 360.0 - 180.0
    val latitude = Math.toDegrees(atan(sinh(PI * (1.0 - 2.0 * y))))
    return Pair(latitude, longitude)
}
