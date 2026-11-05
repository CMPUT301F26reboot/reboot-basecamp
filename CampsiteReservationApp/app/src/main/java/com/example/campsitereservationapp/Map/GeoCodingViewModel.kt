package com.example.campsitereservationapp.Map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Generated with Claude, log under "AI Use Logs\Jacob\Claude-Chat-Log_2026-11-05_GeoCodingForMap.md"
 * ViewModel that geocodes addresses with the Nominatim API.
 *
 * ## Credit
 * Geocoding is provided by [Nominatim](https://nominatim.org), the search engine for
 * OpenStreetMap data, through the public server run by the OpenStreetMap Foundation
 * at `https://nominatim.openstreetmap.org`.
 *
 * ## License and attribution
 * Results contain OpenStreetMap data, © OpenStreetMap contributors, licensed under the
 * Open Database License (ODbL) 1.0. See https://www.openstreetmap.org/copyright.
 * The ODbL requires the app to show visible attribution wherever this data is displayed:
 * "© OpenStreetMap contributors", linked to https://www.openstreetmap.org/copyright.
 *
 * ## Usage policy
 * The public server's usage policy (https://operations.osmfoundation.org/policies/nominatim/)
 * requires that apps:
 * - Send **no more than 1 request per second** in total. This is an absolute maximum.
 * - Identify the app with a custom `User-Agent` header. Default library user agents are blocked.
 * - Cache results and never send the same query twice.
 * - Not implement client-side autocomplete (search-as-you-type).
 * - Not send bulk or systematic queries.
 *
 * Breaking the policy can get the app's requests blocked without warning.
 */
class GeocodingViewModel : ViewModel() {

    /** One Nominatim search result: its full address and coordinates. */
    private data class Place(val name: String, val lat: Double, val lon: Double)

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())

    /** Up to 5 address strings for the dropdown menu. Empty when there are no matches. */
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _coordinates = MutableStateFlow<Pair<Double, Double>?>(null)

    /** `(latitude, longitude)` of the last looked-up address, or `null` if none was found. */
    val coordinates: StateFlow<Pair<Double, Double>?> = _coordinates.asStateFlow()

    /** Address to `(lat, lon)` results already received, as the usage policy requires. */
    private val cache = mutableMapOf<String, Pair<Double, Double>>()

    private var searchJob: Job? = null

    /**
     * Updates [suggestions] for the text the user has typed.
     *
     * Waits 1 second after the last call before it sends a request, so typing never
     * sends more than 1 request per second. Queries shorter than 3 characters clear
     * the list and send no request.
     *
     * Note: Nominatim's usage policy forbids autocomplete on the public server.
     * The debounce and cache keep the request rate low, but this is still a form of
     * autocomplete.
     *
     * @param query the text currently in the search field.
     */
    fun onQueryChange(query: String) {
        searchJob?.cancel()
        if (query.length < 3) {
            _suggestions.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(1000) // wait until typing pauses; keeps requests at 1 per second or fewer
            val places = search(query, limit = 5)
            places.forEach { cache[it.name] = it.lat to it.lon }
            _suggestions.value = places.map { it.name }
        }
    }

    /**
     * Updates [coordinates] with the latitude and longitude of [address].
     *
     * Uses the cached result when the address came from [suggestions], so no
     * request is sent. Otherwise sends 1 request to Nominatim.
     *
     * @param address the address to look up, such as the dropdown item the user picked.
     */
    fun onAddressSelected(address: String) {
        viewModelScope.launch {
            _coordinates.value = cache[address]
                ?: search(address, limit = 1).firstOrNull()?.let { it.lat to it.lon }
        }
    }

    /**
     * Sends 1 forward-geocoding request to the Nominatim `/search` endpoint.
     *
     * Runs on [Dispatchers.IO]. Sends a custom `User-Agent`, as the usage policy requires.
     *
     * @param query the address text to search for.
     * @param limit the maximum number of results to return.
     * @return matching places, or an empty list on no match, network error, or bad response.
     */
    private suspend fun search(query: String, limit: Int): List<Place> =
        withContext(Dispatchers.IO) {
            val q = URLEncoder.encode(query, "UTF-8")
            val url = URL("https://nominatim.openstreetmap.org/search?q=$q&format=jsonv2&limit=$limit")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("User-Agent", "CampsiteReservation/1.0 (templado@ualberta.ca)")
            try {
                val results = JSONArray(conn.inputStream.bufferedReader().use { it.readText() })
                List(results.length()) { i ->
                    val obj = results.getJSONObject(i)
                    Place(
                        obj.getString("display_name"),
                        obj.getString("lat").toDouble(),
                        obj.getString("lon").toDouble()
                    )
                }
            } catch (e: IOException) {
                emptyList()
            } catch (e: JSONException) {
                emptyList()
            } finally {
                conn.disconnect()
            }
        }
}