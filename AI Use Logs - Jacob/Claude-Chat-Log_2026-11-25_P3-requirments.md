# Claude Chat Log

- **Model:** Claude Opus 5.5 (claude-opus-5-5)
- **Date created:** 2026-11-25
- **Time created:** 2:23 PM (UTC-06:00)
- **Marketplace plugin used:** i-have-adhd 0.4.1

## Information uploaded by user

| Prompt | Item | Type | Notes |
|---|---|---|---|
| 2 | AGENT.md | File | Chat log instructions for Claude |
| 3 | CampsiteReservationApp | Folder reference | Project location |
| 5 | onQueryChange function | Pasted text | Kotlin ViewModel search function using StateFlow |
| 7 | GeoCodingViewModelTest | Pasted text | JUnit test class for GeocodingViewModel |
| 8 | GeoCodingViewModelTest | Pasted text | Updated test class after the assertEquals fix |
| 10 | MapViewModelTest | Pasted text | Empty test class with a list of tests to write |
| 11 | DatabaseModelTest | Pasted text | Empty test class for DatabaseModel |
| 12 | ListingViewModelTest | Pasted text | Empty test class for ListingViewModel |
| 13 | ProfileCreatingViewModelTest | Pasted text | Empty test class for ProfileCreatingViewModel |
| 14 | InputDateScreenComponent | File | Named in the prompt; read from the project |
| 15 | IntInputScreenComponent | File | Named in the prompt; read from the project |
| 16 | OwnerLotteryListViewModel | Pasted text | Owner lottery list ViewModel, filters, and card helpers |
| 18 | MessageScreenViewModel | Pasted text | ViewModel for the messages screen |

**Layout:** USER is on the right. CLAUDE is on the left.

---

## Prompt 1

<div align="right">

**USER**

/i-have-adhd:i-have-adhd

</div>

**CLAUDE**

ADHD mode is on and stays on for the rest of this session. Say "stop adhd mode" or "normal mode" to turn it off.

Next: tell me what you want to work on.

---

## Prompt 2

<div align="right">

**USER**

@"AGENT.md"

</div>

**CLAUDE**

Two answers needed before I start the chat log:

1. **`givenName`**: what name goes in the log file name? It will be saved as `Claude-Chat-Log_2026-11-25_givenName.md`.
2. **Project location**: what is the path of the project folder?

Next: reply with both, for example `basecamp, C:\path\to\project`.

---

## Prompt 3

<div align="right">

**USER**

"CampsiteReservationApp"

</div>

**CLAUDE**

(Actions: Moved the session's working folder to the project folder.)

Still needed: the **`givenName`** for the log file. It will be saved as `Claude-Chat-Log_2026-11-25_givenName.md` in the AI Use Logs folder.

The project folder is set: `CampsiteReservationApp`. The session moves there at the end of this turn.

The log isn't created yet because the file name needs the `givenName`. Once you send it, the log will be created with the whole chat so far.

Next: reply with the `givenName`, for example `basecamp`.

---

## Prompt 4

<div align="right">

**USER**

Name it P3-requirments

</div>

**CLAUDE**

(Actions: Checked the current time and the log folder, then created this log with prompts 1 to 4.)

Log created: `Claude-Chat-Log_2026-11-25_P3-requirments.md` in the AI Use Logs folder. It holds prompts 1 to 4.

Set up is done:

1. Log name: `P3-requirments`
2. Project: `CampsiteReservationApp`
3. Rules on: no edits to your files without asking, code given as snippets in replies

Next: tell me what you need for P3 requirements.

---

## Prompt 5

<div align="right">

**USER**

consider this function

```kotlin
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
```

(Attachment: pasted code, shown above.)

[Request interrupted by user]

can stateflow<List<string>> be treated a  normal list with indexing?

</div>

**CLAUDE**

No. A `StateFlow<List<String>>` is not a `List`, so you can't write `suggestions[0]` on it. You can index its `.value`, which is the current list:

```kotlin
val first = _suggestions.value[0]            // throws if the list is empty
val firstOrNull = _suggestions.value.getOrNull(0)  // null if the list is empty
```

`.value` is a snapshot. It does not update when the flow changes later.

To use it in the UI and get updates, collect it first. Then the result is a normal list:

```kotlin
// Compose
val suggestions by viewModel.suggestions.collectAsState()
Text(suggestions[0])
```

Next: tell me where you need to index it (ViewModel, Compose, or an XML Activity/Fragment) and I'll give the exact line.

---

## Prompt 6

<div align="right">

**USER**

How do I create a JUnit assertion with a given variance

</div>

**CLAUDE**

Pass the allowed difference as the third argument to `assertEquals`. JUnit calls it `delta`:

```kotlin
import org.junit.Assert.assertEquals   // JUnit 4
// import org.junit.jupiter.api.Assertions.assertEquals   // JUnit 5

assertEquals(53.5461, actualLat, 0.0001)
```

The test passes when `|expected - actual| <= delta`. Here, any `actualLat` from 53.5460 to 53.5462 passes.

The order is the same in JUnit 4 and JUnit 5: `expected`, `actual`, `delta`. It works for `Double` and `Float`.

Next: check which import your test files already use (`org.junit.Assert` or `org.junit.jupiter.api.Assertions`) and use that one.

---

## Prompt 7

<div align="right">

**USER**

consider the following. Why is assertEquals giving an None of the following candidates is applicable: error? 

```kotlin
/**
 * Test for GeoCodingViewModel
 *
 * AI Refrence: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.Map.GeocodingViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class GeoCodingViewModelTest {

    //get lat/lon from OpenStreetMap manually
    val banffCoor = Pair(51.17515,-115.57212)
    val UofACoor = Pair(53.526948,-113.524899)
    val ETLCCoor = Pair(53.527379, -113.529153)


    //allowable variance (manual variable may not be exact)
    val variance = 0.0005


    private lateinit var viewModel: GeocodingViewModel

    //conduct a search query



    @Test
    fun check_if_returnCorrectCoordinate() {
        //get suggested addresses (saves in internal variable)
        viewModel.onQueryChange("University of Alberta")
        //search input should be close enough to actual that it's the first one suggested
        val address = viewModel.suggestions.value.getOrNull(0)

        if (address == null){
            assertFalse(address == null)
        } else{
            val coord = viewModel.onAddressSelected(address)
            assertEquals(UofACoor,coord,variance)
        }



    }



}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Searched GeoCodingViewModel.kt for `onAddressSelected` and `coordinates`.)

Two type mismatches, so no `assertEquals` overload fits:

1. `UofACoor` is a `Pair<Double, Double>`. The delta version of `assertEquals` only takes `Double` or `Float`.
2. `onAddressSelected` returns `Unit`, not a coordinate. It stores the result in `viewModel.coordinates` (`StateFlow<Pair<Double, Double>?>`).

Fix: replace the `else` block with this. It reads `coordinates.value` and compares lat and lon separately:

```kotlin
} else {
    viewModel.onAddressSelected(address)
    val coord = viewModel.coordinates.value
    assertNotNull(coord)
    assertEquals(UofACoor.first, coord!!.first, variance)   // latitude
    assertEquals(UofACoor.second, coord.second, variance)   // longitude
}
```

Add the import:

```kotlin
import org.junit.Assert.assertNotNull
```

That clears the compile error.

Separately: the test will still fail when it runs. `viewModel` is never assigned, and `onQueryChange` / `onAddressSelected` run in a coroutine with `delay(1000)`, so `suggestions` and `coordinates` are still empty when the test reads them. Want the fix for that next?

---

## Prompt 8

<div align="right">

**USER**

why is the test not working 

```kotlin
/**
 * Test for GeoCodingViewModel
 *
 * AI Refrence: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.Map.GeocodingViewModel
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class GeoCodingViewModelTest {

    //get lat/lon from OpenStreetMap manually
    val banffCoor = Pair(51.17515,-115.57212)
    val UofACoor = Pair(53.526948,-113.524899)
    val ETLCCoor = Pair(53.527379, -113.529153)


    //allowable variance (manual variable may not be exact)
    val variance = 0.0005



    private lateinit var viewModel: GeocodingViewModel

    //conduct a search query



    @Test
    fun check_if_returnCorrectCoordinate() {
        //get suggested addresses (saves in internal variable)
        viewModel.onQueryChange("University of Alberta")
        //search input should be close enough to actual that it's the first one suggested
        val address = viewModel.suggestions.value.getOrNull(0)

        //case, no address is given
        if (address == null){ assertFalse(true) }
        else {
            //check if address's coordinates is within variance
            viewModel.onAddressSelected(address)
            val coord = viewModel.coordinates.value
            assertNotNull(coord)
            assertEquals(UofACoor.first, coord!!.first, variance)   // latitude
            assertEquals(UofACoor.second, coord.second, variance)   // longitude
    }


    }



}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Read GeoCodingViewModel.kt and searched the Gradle files for test dependencies.)

Fix: replace the test class with the version below, then add 1 line to `app/build.gradle.kts`. About 5 minutes.

The test hits 4 problems, in this order:

1. **`viewModel` is never created.** `lateinit` with no assignment throws `UninitializedPropertyAccessException` on the first line.
2. **No Main dispatcher.** `viewModelScope` runs on `Dispatchers.Main`, which does not exist in a local unit test (`src/test`). It throws `IllegalStateException: Module with the Main dispatcher had failed to initialize`.
3. **Results are read too early.** `onQueryChange` waits `delay(1000)`, then makes a network call. The test reads `suggestions.value` right away, so it is always empty.
4. **`org.json` is a stub in unit tests.** `JSONArray` in `search()` comes from the Android stub jar, so it throws `Method ... not mocked`. The search never returns results.

Step 1. Add this line to the `dependencies` block in `app/build.gradle.kts`, next to the other `testImplementation` lines, then sync Gradle:

```kotlin
testImplementation("org.json:json:20240303")
```

Step 2. Replace the test class with this:

```kotlin
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
    fun check_if_returnCorrectCoordinate() = runTest(testDispatcher) {
        viewModel.onQueryChange("University of Alberta")
        // waits until the search finishes; the 1 second delay is skipped by the test dispatcher
        val address = viewModel.suggestions.first { it.isNotEmpty() }[0]

        viewModel.onAddressSelected(address)
        // waits until coordinates are set
        val coord = viewModel.coordinates.filterNotNull().first()

        assertEquals(UofACoor.first, coord.first, variance)   // latitude
        assertEquals(UofACoor.second, coord.second, variance) // longitude
    }
}
```

The `if (address == null)` check is gone. If the search returns nothing, `first { }` waits until `runTest` times out (60 seconds), and the test fails.

The test makes a real request to Nominatim, so the computer needs internet.

Next: run the test. If it fails on a number, paste the failure line. That means Nominatim's top result is more than 0.0005 from `UofACoor`.

---

## Prompt 9

<div align="right">

**USER**

what is another method without adding another dependency

</div>

**CLAUDE**

(Actions: Checked AndroidManifest.xml for the INTERNET permission and listed the androidTest folder.)

Make it an instrumented test: move it from `src/test` to `src/androidTest`. On an emulator or device, the real `org.json` and the real Main dispatcher exist, so you need no new dependency and no `setMain`. About 5 minutes, plus emulator start-up.

1. Move `GeoCodingViewModelTest.kt` from `app/src/test/java/com/example/campsitereservationapp/` to `app/src/androidTest/java/com/example/campsitereservationapp/` (the folder with `ExampleInstrumentedTest.kt`).
2. Replace its contents with this:

```kotlin
package com.example.campsitereservationapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.campsitereservationapp.Map.GeocodingViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeoCodingViewModelTest {

    //get lat/lon from OpenStreetMap manually
    val banffCoor = Pair(51.17515, -115.57212)
    val UofACoor = Pair(53.526948, -113.524899)
    val ETLCCoor = Pair(53.527379, -113.529153)

    //allowable variance (manual variable may not be exact)
    val variance = 0.0005

    private lateinit var viewModel: GeocodingViewModel

    @Before
    fun setUp() {
        viewModel = GeocodingViewModel()
    }

    @Test
    fun check_if_returnCorrectCoordinate() = runBlocking {
        viewModel.onQueryChange("University of Alberta")
        // waits for the real 1 second delay and the network call; fails after 15 seconds
        val address = withTimeout(15_000) {
            viewModel.suggestions.first { it.isNotEmpty() }
        }[0]

        viewModel.onAddressSelected(address)
        val coord = withTimeout(15_000) {
            viewModel.coordinates.filterNotNull().first()
        }

        assertEquals(UofACoor.first, coord.first, variance)   // latitude
        assertEquals(UofACoor.second, coord.second, variance) // longitude
    }
}
```

3. Start an emulator, then click the green arrow next to the class name to run it.

Why this works with what you have:

- `runBlocking` and `withTimeout` come from `kotlinx-coroutines-core`, which the app already uses.
- `AndroidJUnit4` comes from `androidx.junit`, which is already an `androidTestImplementation`.
- The manifest already has the `INTERNET` permission.

Trade-off: it needs a running emulator or device, and it is slower than a local unit test.

Next: move the file (step 1).

---

## Prompt 10

<div align="right">

**USER**

create the test with the methods given in the following 

```kotlin
/**
 * Test file for MapViewModel
 *
 * Tests:
 * - test normalization by using lat/long -> noramlized -> lat/long, values should be the same
 * - test moveTo by asserting if mapstate location is at the new coordinates
 * - test tile stream provider and make sure it returns a tile
 */
package com.example.campsitereservationapp

class MapViewModelTest {
}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Read MapViewModel.kt and searched app/build.gradle.kts for the MapCompose and test dependencies.)

Not run yet. About 10 minutes to apply, plus emulator start-up.

1. In `MapViewModel.kt`, change `private` to `internal` on the 3 top-level functions at the bottom of the file. Tests can't call `private` functions; `internal` keeps them hidden outside the app module.

```kotlin
internal fun makeTileStreamProvider() = TileStreamProvider { row, col, zoomLvl ->
```
```kotlin
internal fun latLonToNormalized(latitude: Double, longitude: Double): Pair<Double, Double> {
```
```kotlin
internal fun normalizedToLatLon(x: Double, y: Double): Pair<Double, Double> {
```

2. Delete `MapViewModelTest.kt` from `app/src/test/...` and create it in `app/src/androidTest/java/com/example/campsitereservationapp/`. It must be an instrumented test: `MapState` needs the Android main thread, and `scrollTo` only moves the map after the map is drawn on screen.
3. Paste this into the new file:

```kotlin
/**
 * Test file for MapViewModel
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
```

4. Start an emulator, then click the green arrow next to `MapViewModelTest` to run all 3.

No new dependencies: `createComposeRule`, `AndroidJUnit4`, and MapCompose are already in `app/build.gradle.kts`.

Next: make the `internal` change in `MapViewModel.kt` (step 1).

---

## Prompt 11

<div align="right">

**USER**

create the tests for every collection in the database. Use mockfirestore 

```kotlin
/**
 * Test for database model, adds a collection of every type to the database,
 * then retrieves them, makes sure what was added is the same as what it got back
 */
package com.example.campsitereservationapp

class DatabaseModelTest {
}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Read DatabaseModel.kt, MockFirestore.kt, the 7 document classes, and 2 existing tests that use MockFirestore.)

Not run yet. About 10 minutes to apply. 2 steps:

1. In `MockFirestore.kt`, add `"Messages"` to the list in `install()` (line 40). `DatabaseModel` listens to `Messages`, but the mock doesn't capture that listener yet, so `deliver("Messages", ...)` would throw. Other tests are not affected.

```kotlin
listOf("Listings", "Lotteries", "Profiles", "Reservations", "LogEntries", "Chats", "Messages").forEach { name ->
```

2. Replace `DatabaseModelTest.kt` (in `app/src/test/...`) with this:

```kotlin
/**
 * Test for database model, adds a collection of every type to the database,
 * then retrieves them, makes sure what was added is the same as what it got back
 *
 * AI Refrence: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.LogEntry
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class DatabaseModelTest {

    /** Exposes DatabaseModel's protected local copies so the test can read them. */
    private class TestDatabaseModel : DatabaseModel() {
        val listings: List<Listing> get() = _listings
        val lotteries: List<Lottery> get() = _lotteries
        val profiles: List<Profile> get() = _profiles
        val logEntries: List<LogEntry> get() = _logentries
        val chats: List<Chat> get() = _chats
        val messages: List<Message> get() = _messages
    }

    private val firestore = MockFirestore()
    private lateinit var model: TestDatabaseModel

    @Before
    fun setUp() {
        firestore.install()
        model = TestDatabaseModel()
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun listings_addedListingIsRetrieved() {
        val listing = Listing(
            name = "Two Jack Lakeside",
            address = "Lake Minnewanka Scenic Dr, Banff, AB T1L 1K2",
            maxOccupancy = 6,
            ownerID = "owner-device-7f3a91c2",
            geoLocation = GeoPoint(51.2290, -115.5040)
        ).apply { id = "listing-two-jack" }
        val saved = slot<Listing>()

        // add: DatabaseModel sends the listing to the Listings collection
        model.addListing(listing)
        verify { firestore.collection("Listings").add(capture(saved)) }
        assertSame(listing, saved.captured)

        // retrieve: Firestore sends the saved listing back through the listener
        firestore.deliver("Listings", Listing::class.java, listOf(saved.captured))
        assertEquals(listOf(listing), model.listings)
    }

    @Test
    fun lotteries_addedLotteryIsRetrieved() {
        val lottery = Lottery(
            dates = listOf(day(2027, 7, 9), day(2027, 7, 10)),
            start = day(2027, 5, 1),
            end = day(2027, 5, 31),
            campsiteIDs = listOf("listing-two-jack")
        ).apply {
            id = "lottery-july"
            title = "July Long Weekend - Banff"
        }
        val saved = slot<Lottery>()

        model.addLottery(lottery)
        verify { firestore.collection("Lotteries").add(capture(saved)) }
        assertSame(lottery, saved.captured)

        firestore.deliver("Lotteries", Lottery::class.java, listOf(saved.captured))
        assertEquals(listOf(lottery), model.lotteries)
    }

    @Test
    fun profiles_deliveredProfilesAreRetrieved() {
        val profile = Profile(name = "Alex Chen", deviceId = "device-7f3a91c2").apply {
            id = "device-7f3a91c2"
            email = "alex@example.com"
            role = "Entrant"
        }

        firestore.deliver("Profiles", Profile::class.java, listOf(profile))

        assertEquals(listOf(profile), model.profiles)
    }

    @Test
    fun reservations_deliveredReservationsAreRetrieved() {
        val reservation = Reservation(
            dates = listOf(day(2027, 7, 9)),
            entrantID = "device-7f3a91c2",
            campsiteID = "listing-two-jack"
        ).apply { id = "reservation-1" }

        firestore.deliver("Reservations", Reservation::class.java, listOf(reservation))

        assertEquals(listOf(reservation), model.reservations)
    }

    @Test
    fun logEntries_deliveredLogEntriesAreRetrieved() {
        val logEntry = LogEntry()

        firestore.deliver("LogEntries", LogEntry::class.java, listOf(logEntry))

        assertEquals(listOf(logEntry), model.logEntries)
    }

    @Test
    fun chats_deliveredChatsAreRetrieved() {
        val chat = Chat(ownerID = "owner-device-7f3a91c2", entrantID = "device-7f3a91c2").apply {
            id = "chat-1"
            messageIDList = listOf("message-1")
        }

        firestore.deliver("Chats", Chat::class.java, listOf(chat))

        assertEquals(listOf(chat), model.chats)
    }

    @Test
    fun messages_deliveredMessagesAreRetrieved() {
        val message = Message(
            text = "Check-in is after 2 PM.",
            senderID = "owner-device-7f3a91c2",
            receiverID = "device-7f3a91c2",
            timestamp = Timestamp(1_800_000_000, 0)
        ).apply { id = "message-1" }

        firestore.deliver("Messages", Message::class.java, listOf(message))

        assertEquals(listOf(message), model.messages)
    }
}
```

What each test checks:

- **Listings, Lotteries:** full add → retrieve round trip. `DatabaseModel` only has `addListing` and `addLottery`.
- **Profiles, Reservations, LogEntries, Chats, Messages:** retrieve only. `DatabaseModel` has no add function for these, so the test sends them in through the mocked listener and checks the local copy.
- **Admins:** not tested. `DatabaseModel` has `adminsRef` but no listener and no local list, so nothing can be retrieved.

The document classes are not data classes, so "the same" means the same object (`assertSame`, and list equality by reference).

Next: add `"Messages"` to `MockFirestore.kt` (step 1).

---

## Prompt 12

<div align="right">

**USER**

```kotlin
/**
 * Test for ListingViewModel,
 * Creates Listings of dates, add to a lottery, get listing dates and compare if correct
 *
 * AI Refrence: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

class ListingViewModelTest {
}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Read ListingViewModel.kt.)

Replace `ListingViewModelTest.kt` (in `app/src/test/...`) with this. Not run yet. About 5 minutes. It uses `MockFirestore` and `day()` from `MockFirestore.kt`, so no other file changes are needed.

```kotlin
/**
 * Test for ListingViewModel,
 * Creates Listings of dates, add to a lottery, get listing dates and compare if correct
 *
 * AI Refrence: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ListingViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ListingViewModelTest {

    private val twoJack = Listing(name = "Two Jack Lakeside").apply { id = "listing-two-jack" }
    private val tunnelMountain = Listing(name = "Tunnel Mountain Village II").apply { id = "listing-tunnel-mtn" }

    // July lottery: both campsites
    private val julyLottery = Lottery(
        dates = listOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11)),
        campsiteIDs = listOf("listing-two-jack", "listing-tunnel-mtn")
    ).apply { id = "lottery-july" }

    // August lottery: Two Jack only, shares 0 dates with July
    private val augustLottery = Lottery(
        dates = listOf(day(2027, 8, 1), day(2027, 8, 2)),
        campsiteIDs = listOf("listing-two-jack")
    ).apply { id = "lottery-august" }

    // Overlap lottery: Two Jack only, repeats July 10 to check duplicates are removed
    private val overlapLottery = Lottery(
        dates = listOf(day(2027, 7, 10)),
        campsiteIDs = listOf("listing-two-jack")
    ).apply { id = "lottery-overlap" }

    private val firestore = MockFirestore()
    private lateinit var viewModel: ListingViewModel

    @Before
    fun setUp() {
        firestore.install()
        viewModel = ListingViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun getAvailability_returnsDatesOfEveryLotteryWithTheListing() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery, augustLottery))

        val expected = setOf(
            day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11),
            day(2027, 8, 1), day(2027, 8, 2)
        )
        assertEquals(expected, viewModel.getAvailability(twoJack))
    }

    @Test
    fun getAvailability_skipsLotteriesWithoutTheListing() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery, augustLottery))

        // Tunnel Mountain is only in the July lottery
        val expected = setOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11))
        assertEquals(expected, viewModel.getAvailability(tunnelMountain))
    }

    @Test
    fun getAvailability_removesDuplicateDates() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(julyLottery, overlapLottery))

        // July 10 is in both lotteries but appears once
        val expected = setOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11))
        assertEquals(expected, viewModel.getAvailability(twoJack))
    }

    @Test
    fun getAvailability_noLotteries_returnsEmpty() {
        firestore.deliver("Lotteries", Lottery::class.java, emptyList())

        assertTrue(viewModel.getAvailability(twoJack).isEmpty())
    }

    @Test
    fun listings_returnsDeliveredListings() {
        assertEquals(listOf(twoJack, tunnelMountain), viewModel.listings)
    }
}
```

What it covers (`ListingViewModel` has 2 public members, `getAvailability` and `listings`):

1. Dates from every lottery that includes the listing are combined.
2. Lotteries that don't include the listing are skipped.
3. A date in 2 lotteries appears once (`getAvailability` returns a `Set`).
4. No lotteries returns an empty set.
5. `listings` returns what the database sent.

Next: paste it in and run `ListingViewModelTest`.

---

## Prompt 13

<div align="right">

**USER**

```kotlin
/**
 * Test file for ProfileCreatingViewModel
 * 
 * Gets device ID, create profile, add to db, retrieves profile and make sure all information
 * is the same
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

class ProfileCreatingViewModelTest {
}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Read ProfileCreatingViewModel.kt, getDeviceId in ProfileUtils.kt, both ProfileCreatingViewModelTest.kt files, and the mocking setup in UserSignUpViewModelTest.kt.)

Not run yet. About 5 minutes.

1. Put the test in `app/src/test/.../ProfileCreatingViewModelTest.kt` (the empty one). It must be in `src/test` because it uses `MockFirestore` and MockK, which only exist there.
2. Delete `app/src/androidTest/.../ProfileCreatingViewModelTest.kt`. It is an empty copy with the same class name.
3. Paste this in:

```kotlin
/**
 * Test file for ProfileCreatingViewModel
 *
 * Gets device ID, create profile, add to db, retrieves profile and make sure all information
 * is the same
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import android.content.Context
import android.provider.Settings
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ProfileCreatingViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileCreatingViewModelTest {

    /** Exposes the protected functions and the local Profiles copy so the test can use them. */
    private class TestProfileCreatingViewModel : ProfileCreatingViewModel() {
        val profiles: List<Profile> get() = _profiles
        fun save(profile: Profile, onSuccess: () -> Unit, onFailure: () -> Unit) =
            saveProfile(profile, onSuccess, onFailure)
        fun exists(deviceId: String, onResult: (Boolean) -> Unit) =
            profileExists(deviceId, onResult)
    }

    private val deviceId = "device-7f3a91c2"
    private val name = "Alex Chen"
    private val email = "alex@example.com"
    private val phoneNumber = "780-555-0123"
    private val profilePicture = "aGVsbG8="
    private val role = "Entrant"

    private val firestore = MockFirestore()
    private val document = mockk<DocumentReference>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private lateinit var viewModel: TestProfileCreatingViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Profiles").document(deviceId) } returns document
        // the device ID comes from Settings.Secure, which is a stub in unit tests
        mockkStatic(Settings.Secure::class)
        every { Settings.Secure.getString(any(), Settings.Secure.ANDROID_ID) } returns deviceId
        viewModel = TestProfileCreatingViewModel()
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun readDeviceId_returnsAndroidId() {
        assertEquals(deviceId, viewModel.readDeviceId(context))
    }

    @Test
    fun saveProfile_thenRetrieve_allInformationIsTheSame() {
        // create the profile with the device ID
        val profile = Profile(name = name, deviceId = viewModel.readDeviceId(context)).apply {
            email = this@ProfileCreatingViewModelTest.email
            phoneNumber = this@ProfileCreatingViewModelTest.phoneNumber
            profilePicture = this@ProfileCreatingViewModelTest.profilePicture
            notificationsEnabled = false
            role = this@ProfileCreatingViewModelTest.role
        }
        val write = MockTask<Void>()
        val written = slot<Profile>()
        every { document.set(capture(written)) } returns write.task
        var saved = false

        // add to db: written to the document named after the device ID
        viewModel.save(profile, onSuccess = { saved = true }, onFailure = {})
        write.succeed(null)
        assertTrue(saved)

        // retrieve: Firestore sets id to the document name when it reads the profile back
        written.captured.id = deviceId
        firestore.deliver("Profiles", Profile::class.java, listOf(written.captured))
        val retrieved = viewModel.profiles.single()

        assertEquals(deviceId, retrieved.id)
        assertEquals(deviceId, retrieved.deviceId)
        assertEquals(name, retrieved.name)
        assertEquals(email, retrieved.email)
        assertEquals(phoneNumber, retrieved.phoneNumber)
        assertEquals(profilePicture, retrieved.profilePicture)
        assertFalse(retrieved.notificationsEnabled)
        assertEquals(role, retrieved.role)
    }

    @Test
    fun profileExists_savedDevice_returnsTrue() {
        val read = MockTask<DocumentSnapshot>()
        every { document.get() } returns read.task
        var found: Boolean? = null

        viewModel.exists(deviceId) { found = it }
        read.succeed(mockk { every { exists() } returns true })

        assertEquals(true, found)
    }
}
```

What each test checks:

1. **Device ID:** `readDeviceId` returns the Android ID.
2. **Round trip:** the profile is written to the document named after the device ID, then read back through the Profiles listener with every field unchanged.
3. **Exists check:** `profileExists` reports true when the device's document exists.

`saveProfile` and `profileExists` are `protected`, so the test uses a small subclass to call them. No changes to `ProfileCreatingViewModel.kt` are needed.

Next: delete the empty `androidTest` copy (step 2).

---

## Prompt 14

<div align="right">

**USER**

Read InputDateScreenComponent., create a test file for the controll class

</div>

**CLAUDE**

(Actions: Read InputDateScreenComponent.kt and the `toDayOnly` helper in UserWonBookingViewModel.kt; checked that no test file for it exists yet.)

Create `app/src/test/java/com/example/campsitereservationapp/InputDateScreenComponentTest.kt` with the code below. Not run yet. About 5 minutes.

The file has no class. Its control logic is 3 `internal` functions: `nextDateRange`, `datesBetween`, and `formatDateRange`. They are plain Kotlin, so this is a local unit test with no mocks. `day()` comes from `MockFirestore.kt`.

```kotlin
/**
 * Test for the control functions in InputDateScreenComponent:
 * nextDateRange, datesBetween, and formatDateRange
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.ui.components.datesBetween
import com.example.campsitereservationapp.ui.components.formatDateRange
import com.example.campsitereservationapp.ui.components.nextDateRange
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date
import java.util.Locale

class InputDateScreenComponentTest {

    private lateinit var originalLocale: Locale

    @Before
    fun setUp() {
        // formatDateRange uses the default locale, so fix it to get "May 1, 2027"
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    /** [date] plus [hours], to check that times are dropped. */
    private fun atHour(date: Date, hours: Int) = Date(date.time + hours * 60 * 60 * 1000L)

    // ---------- nextDateRange ----------

    @Test
    fun nextDateRange_noDatesChosen_startsNewRangeAtMidnight() {
        val result = nextDateRange(emptyList(), atHour(day(2027, 5, 1), 15))

        assertEquals(listOf(day(2027, 5, 1)), result)
    }

    @Test
    fun nextDateRange_oneDateChosen_tapLater_fillsRange() {
        val result = nextDateRange(listOf(day(2027, 5, 1)), day(2027, 5, 4))

        assertEquals(
            listOf(day(2027, 5, 1), day(2027, 5, 2), day(2027, 5, 3), day(2027, 5, 4)),
            result
        )
    }

    @Test
    fun nextDateRange_oneDateChosen_tapEarlier_fillsRangeInOrder() {
        val result = nextDateRange(listOf(day(2027, 5, 4)), day(2027, 5, 2))

        assertEquals(listOf(day(2027, 5, 2), day(2027, 5, 3), day(2027, 5, 4)), result)
    }

    @Test
    fun nextDateRange_oneDateChosen_tapSameDay_returnsOneDay() {
        val result = nextDateRange(listOf(day(2027, 5, 1)), day(2027, 5, 1))

        assertEquals(listOf(day(2027, 5, 1)), result)
    }

    @Test
    fun nextDateRange_rangeAlreadyChosen_startsNewRange() {
        val range = listOf(day(2027, 5, 1), day(2027, 5, 2), day(2027, 5, 3))

        val result = nextDateRange(range, day(2027, 6, 10))

        assertEquals(listOf(day(2027, 6, 10)), result)
    }

    // ---------- datesBetween ----------

    @Test
    fun datesBetween_includesBothEndsAcrossMonthEnd() {
        val result = datesBetween(day(2027, 4, 29), day(2027, 5, 2))

        assertEquals(
            listOf(day(2027, 4, 29), day(2027, 4, 30), day(2027, 5, 1), day(2027, 5, 2)),
            result
        )
    }

    @Test
    fun datesBetween_sameDay_returnsOneDay() {
        val result = datesBetween(atHour(day(2027, 5, 1), 9), atHour(day(2027, 5, 1), 20))

        assertEquals(listOf(day(2027, 5, 1)), result)
    }

    @Test
    fun datesBetween_startAfterEnd_returnsEmpty() {
        assertTrue(datesBetween(day(2027, 5, 5), day(2027, 5, 1)).isEmpty())
    }

    // ---------- formatDateRange ----------

    @Test
    fun formatDateRange_noDates_returnsEmptyText() {
        assertEquals("", formatDateRange(emptyList()))
    }

    @Test
    fun formatDateRange_oneDay_returnsOneDate() {
        assertEquals("May 1, 2027", formatDateRange(listOf(day(2027, 5, 1))))
    }

    @Test
    fun formatDateRange_range_returnsFirstToLastInAnyOrder() {
        val dates = listOf(day(2027, 5, 14), day(2027, 5, 1), day(2027, 5, 7))

        assertEquals("May 1, 2027 – May 14, 2027", formatDateRange(dates))
    }
}
```

What it covers:

1. **`nextDateRange`:** first tap, second tap later or earlier, same day twice, and a new tap after a full range.
2. **`datesBetween`:** both ends included across a month end, same day, and start after end.
3. **`formatDateRange`:** no dates, one day, and a range given out of order.

Not covered: `selectableDays`, `formatDate`, and `toYearMonth` are `private`, and the composables need a UI test.

Next: create the file and run `InputDateScreenComponentTest`.

---

## Prompt 15

<div align="right">

**USER**

create another for IntInputScreenComponent

</div>

**CLAUDE**

(Actions: Read IntInputScreenComponent.kt.)

Create `app/src/androidTest/java/com/example/campsitereservationapp/IntInputScreenComponentTest.kt` with the code below. Not run yet. About 5 minutes, plus emulator start-up.

This one is a UI test, not a unit test. All of the control logic (plus, minus, digit filtering) is inside the composable, with no separate function to call. A Compose UI test runs it as-is, so `IntInputScreenComponent.kt` needs no changes. `createComposeRule` is already an `androidTestImplementation`.

```kotlin
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

    private val minus = "−" // the "−" sign on the minus button (not a hyphen)

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
```

What it covers:

1. **Buttons:** plus adds 1, minus subtracts 1, minus is disabled at 0.
2. **Typing:** non-digits are removed, leading zeros are dropped, input stops at 9 digits, and clearing reports 0 while the field stays empty.
3. **Text:** the optional title and subheader are shown.

Not covered: the field changing back to "0" when it loses focus while empty. That needs a second focusable node on screen to move focus to.

Next: create the file, start an emulator, and run `IntInputScreenComponentTest`.

---

## Prompt 16

<div align="right">

**USER**

consider this viewmodel, create a test class for it 

```kotlin
/**
 * This file contains the Owner Lottery List View Model
 */
package com.example.campsitereservationapp.ui.owner.Lotterylist

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
        now.after(lottery.end) -> LotteryStatus.COMPLETED
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
        timeLeftLabel = timeLeftLabel(lottery.start, lottery.end, now)
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
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Read `enrolledCount` in LotteryEditComponents.kt and checked that no test file exists yet for this ViewModel.)

Create `app/src/test/java/com/example/campsitereservationapp/OwnerLotteryListViewModelTest.kt` with the code below. Not run yet. About 5 minutes. It uses `MockFirestore` and `day()` from `MockFirestore.kt`, so no other files change.

```kotlin
/**
 * Test for OwnerLotteryListViewModel:
 * filter tabs, which lotteries belong to the owner and their order, the card data built
 * by toOwnerLotteryItem, and the countdown from timeLeftLabel
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
```

What it covers:

1. **Filter:** starts at All, `selectFilter` changes it, and each tab matches only its status.
2. **`ownerLotteries`:** keeps only lotteries on the owner's campsites, sorted active, completed, cancelled, then by registration end.
3. **`toOwnerLotteryItem`:** status rules, campsite name, blank title, dates, limits, and counts.
4. **`timeLeftLabel`:** "Opens in", days, hours, under 1 hour, and empty after the end.

Next: create the file and run `OwnerLotteryListViewModelTest`.

---

## Prompt 17

<div align="right">

**USER**

add to the topline comment on what the tests cover

</div>

**CLAUDE**

Replace the comment at the top of `OwnerLotteryListViewModelTest.kt` (above `package`) with this:

```kotlin
/**
 * Test for OwnerLotteryListViewModel
 *
 * Tests:
 * - filter: starts at All, selectFilter changes it, and each tab (LotteryFilter.matches)
 *   matches only its own status, while All matches every lottery
 * - ownerLotteries: returns only lotteries on the owner's campsites, sorted active,
 *   completed, then cancelled, and by registration end within each status
 * - toOwnerLotteryItem: cancelled wins over ended, completed after the end, active before it;
 *   campsite name (one name, "+N more", or "Unknown campsite"); blank title becomes
 *   "Untitled lottery"; stay dates, winners to draw, waitlist cap, entrant count, and winners
 *   drawn are copied, and are null when the lottery has no dates or limits
 * - timeLeftLabel: "Opens in 2d" before registration starts, "3d left", "18h left", or
 *   "<1h left" while open, and empty after it ends
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
```

It uses the same `Tests:` list style as the header in `MapViewModelTest.kt`.

Next: paste it over the old comment.

---

## Prompt 18

<div align="right">

**USER**

create a test for 

```kotlin
/**
 * This file contains the View Model functionality for the MessagesScreen
 */
package com.example.campsitereservationapp.ui

import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

/**
 * View Model for the message screen
 *
 * Finds the appropriate chat or creates one if one does not exist
 * Holds a list of messages is [messages]
 * Provides ability to send a message and find name of a chatter
 * Automatically updates [messages] when a message is sent
 */
class MessageScreenViewModel: DatabaseModel() {

    var myChat = chatsRef.document()
    var messages = mutableStateListOf<Message>()


    /**
     * sets up the viewmodel
     *
     * Finds if you are the owner or the other is
     * Sets myChat to the document reference of the chat between the owner and entrant if it exists,
     * otherwise create a new reference and add it to the database
     * Loads the messages in the chat into [messages]
     * Adds a listener on myChat to update [messages]
     */
    fun start(yourID: String, otherID: String){
        var ownerID = otherID
        var entrantID = yourID
        profilesRef                                     //checks if you are the owner or not
            .document(yourID)
            .get()
            .addOnSuccessListener { snapshot ->
                val profile = snapshot.toObject(Profile::class.java)
                if(profile != null && profile.role == "Owner"){
                    ownerID = yourID
                    entrantID = otherID
                }

                configureChat(ownerID, entrantID)       //gets the right chat or makes a new one
            }
    }

    /**
     * Sends a message
     *
     * Creates a message and saves it to the Messages collection,
     * then adds the id of that message to the messageIDList of the chat
     * triggering a recomposition
     *
     * @param [senderID] is the document id for the sender's profile document
     * @param [receiverID] is the document id for the receiver's profile document
     * @param [text] is the text of the message
     */
    fun sendMessage(senderID: String, receiverID: String, text: String){
        var myMessage = messagesRef.document()
        val newMessage = Message(text, senderID, receiverID, Timestamp.now())
        myMessage.set(newMessage)
        myChat.update("messageIDList", FieldValue.arrayUnion(myMessage.id))
    }

    /**
     * Gets the name of a profile based on a profile id
     *
     * @param [profileID] document id of the profile you want to get the name of
     */
    fun getName(profileID: String): String{
        for(profile in _profiles){
            if(profile.id == profileID){
                return profile.name
            }
        }
        return ""
    }


    private fun configureChat(ownerID: String, entrantID: String){
        chatsRef
            .whereEqualTo("ownerID", ownerID)
            .whereEqualTo("entrantID", entrantID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val temp = querySnapshot.documents.firstOrNull()?.reference
                if(temp != null){
                    myChat = temp
                }else{
                    var tempChat = Chat(ownerID = ownerID, entrantID = entrantID)
                    tempChat.id = myChat.id
                    myChat.set(tempChat)
                }

                addChatListener()           //adds a listener for changes in the chat

            }
    }

    private fun getMessage(messageID: String): Message{
        for(message in _messages){
            if(message.id == messageID){
                return message
            }
        }
        return Message()
    }


    private fun addChatListener(){
        myChat.addSnapshotListener { snapshot, error ->
            if(error != null){
                return@addSnapshotListener
            }

            messages.clear()
            var tempChat = snapshot?.toObject(Chat::class.java)
            if(tempChat != null){
                for(messageID in tempChat.messageIDList){
                    val message = getMessage(messageID)
                    if(message.id.isNotEmpty()){
                        messages.add(message)
                    }
                }
                messages.sortBy { it.timestamp }
            }
        }
    }

}
```

(Attachment: pasted code, shown above.)

</div>

**CLAUDE**

(Actions: Checked that no MessageScreenViewModel test exists yet and that MockFirestore.kt already has "Messages" in `install()`.)

Create `app/src/test/java/com/example/campsitereservationapp/MessageScreenViewModelTest.kt` with the code below. Not run yet. About 5 minutes. `MockFirestore.kt` already has `"Messages"`, so no other files change.

```kotlin
/**
 * Test for MessageScreenViewModel
 *
 * Tests:
 * - getName: returns the name of a profile by its ID, or empty text for an unknown ID
 * - sendMessage: saves a Message with the text, sender, and receiver to the Messages
 *   collection, then adds its ID to the chat's messageIDList
 * - start as entrant with no chat yet: looks up the chat with the other user as owner,
 *   then creates a new Chat with the right owner and entrant IDs
 * - start as owner with an existing chat: swaps the owner and entrant IDs, and uses the
 *   existing chat instead of creating one
 * - chat listener: fills messages from the chat's messageIDList, sorted by timestamp,
 *   and skips IDs with no matching message
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.ui.MessageScreenViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class MessageScreenViewModelTest {

    private val entrantId = "device-7f3a91c2"
    private val ownerId = "owner-device-22be0d14"

    private val firestore = MockFirestore()
    private val newChat = mockk<DocumentReference>(relaxed = true)     // from chatsRef.document()
    private val existingChat = mockk<DocumentReference>(relaxed = true) // found by the chat query
    private val profileDoc = mockk<DocumentReference>(relaxed = true)
    private val profileRead = MockTask<DocumentSnapshot>()
    private val chatQueryRead = MockTask<QuerySnapshot>()
    private lateinit var viewModel: MessageScreenViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { newChat.id } returns "chat-new"
        every { firestore.collection("Chats").document() } returns newChat
        every { firestore.collection("Profiles").document(any()) } returns profileDoc
        every { profileDoc.get() } returns profileRead.task
        viewModel = MessageScreenViewModel()
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    /** Makes the chat query answer only when it asks for this owner and entrant. */
    private fun stubChatQuery(owner: String, entrant: String) {
        val byOwner = mockk<Query>()
        val byBoth = mockk<Query>()
        every { firestore.collection("Chats").whereEqualTo("ownerID", owner) } returns byOwner
        every { byOwner.whereEqualTo("entrantID", entrant) } returns byBoth
        every { byBoth.get() } returns chatQueryRead.task
    }

    private fun profileSnapshot(role: String) = mockk<DocumentSnapshot> {
        every { toObject(Profile::class.java) } returns Profile(name = "Someone").apply { this.role = role }
    }

    private fun chatQueryResult(found: DocumentReference?) = mockk<QuerySnapshot> {
        every { documents } returns
            if (found == null) emptyList() else listOf(mockk { every { reference } returns found })
    }

    // ---------- getName ----------

    @Test
    fun getName_returnsProfileName_orEmptyForUnknownId() {
        firestore.deliver("Profiles", Profile::class.java, listOf(
            Profile(name = "Alex Chen", deviceId = entrantId).apply { id = entrantId },
            Profile(name = "Sam Owner", deviceId = ownerId).apply { id = ownerId }
        ))

        assertEquals("Alex Chen", viewModel.getName(entrantId))
        assertEquals("Sam Owner", viewModel.getName(ownerId))
        assertEquals("", viewModel.getName("device-unknown"))
    }

    // ---------- sendMessage ----------

    @Test
    fun sendMessage_savesMessageAndAddsItToChat() {
        val messageDoc = mockk<DocumentReference>(relaxed = true)
        every { messageDoc.id } returns "message-1"
        every { firestore.collection("Messages").document() } returns messageDoc
        val sent = slot<Message>()

        viewModel.sendMessage(entrantId, ownerId, "Is the fire pit open?")

        verify { messageDoc.set(capture(sent)) }
        assertEquals("Is the fire pit open?", sent.captured.text)
        assertEquals(entrantId, sent.captured.senderID)
        assertEquals(ownerId, sent.captured.receiverID)
        verify { newChat.update("messageIDList", any(), *anyVararg()) }
    }

    // ---------- start ----------

    @Test
    fun start_asEntrant_noChatYet_createsChat() {
        stubChatQuery(owner = ownerId, entrant = entrantId)
        val created = slot<Chat>()

        viewModel.start(yourID = entrantId, otherID = ownerId)
        profileRead.succeed(profileSnapshot(role = "Entrant"))
        chatQueryRead.succeed(chatQueryResult(found = null))

        verify { newChat.set(capture(created)) }
        assertEquals(ownerId, created.captured.ownerID)
        assertEquals(entrantId, created.captured.entrantID)
        assertEquals("chat-new", created.captured.id)
        assertSame(newChat, viewModel.myChat)
    }

    @Test
    fun start_asOwner_existingChat_usesIt() {
        // you are the owner, so the IDs are swapped compared to the entrant case
        stubChatQuery(owner = ownerId, entrant = entrantId)

        viewModel.start(yourID = ownerId, otherID = entrantId)
        profileRead.succeed(profileSnapshot(role = "Owner"))
        chatQueryRead.succeed(chatQueryResult(found = existingChat))

        assertSame(existingChat, viewModel.myChat)
        verify(exactly = 0) { newChat.set(any()) }
    }

    // ---------- chat listener ----------

    @Test
    fun chatListener_fillsMessagesSortedByTime_skipsMissing() {
        val earlier = Message("Hi!", entrantId, ownerId, Timestamp(1_800_000_000, 0)).apply { id = "message-earlier" }
        val later = Message("Hello", ownerId, entrantId, Timestamp(1_800_000_600, 0)).apply { id = "message-later" }
        firestore.deliver("Messages", Message::class.java, listOf(later, earlier))

        val chatListener = slot<EventListener<DocumentSnapshot>>()
        every { existingChat.addSnapshotListener(capture(chatListener)) } returns mockk(relaxed = true)
        stubChatQuery(owner = ownerId, entrant = entrantId)

        viewModel.start(yourID = entrantId, otherID = ownerId)
        profileRead.succeed(profileSnapshot(role = "Entrant"))
        chatQueryRead.succeed(chatQueryResult(found = existingChat))

        // the chat lists the newer message first and one ID with no message
        val chat = Chat(ownerID = ownerId, entrantID = entrantId).apply {
            messageIDList = listOf("message-later", "message-earlier", "message-deleted")
        }
        chatListener.captured.onEvent(mockk { every { toObject(Chat::class.java) } returns chat }, null)

        assertEquals(listOf(earlier, later), viewModel.messages)
    }
}
```

How the tests control Firestore:

1. `chatsRef.document()` returns `newChat`, so the test can check what is written to a new chat.
2. `stubChatQuery` only answers when the query asks for the expected owner and entrant. If `start` swaps the IDs the wrong way, the query never answers and the test fails.
3. `MockTask` decides when each read finishes, so the test steps through profile read, then chat query, then chat listener.

Next: create the file and run `MessageScreenViewModelTest`.

---

## Prompt 19

<div align="right">

**USER**

Now read all the files in the app. Check if every file has topline comment header and check if every viewmodel has a test class

</div>

**CLAUDE**

(Actions: Checked the first lines of all 105 `.kt` files in main, test, and androidTest; listed every class that extends ViewModel or DatabaseModel and matched each one to a test file.)

1 file is missing a header: `MainActivity.kt`. All 19 ViewModels are tested; 18 have their own test class, and 1 is tested through its subclass.

### Headers

104 of 105 files have a header comment above `package`.

Missing: `MainActivity.kt`. Add this above `package`:

```kotlin
/**
 * This file contains MainActivity, the app's entry point. It turns on edge-to-edge display and
 * shows CampsiteReservationApp (the app's navigation) inside CampsiteReservationTheme
 */
```

2 headers exist but don't describe the file well:

1. `ListingTest.kt`: the header is only the AI use note. It doesn't say what is tested.
2. `UserCampsiteDetailsTest.kt`: says "test for User Campsite Details ViewModel", but it tests `formatDateRange` and `parseRouteDate` from the screen file. `UserCampsiteDetailsViewModelTest.kt` is the actual ViewModel test.

### ViewModels and their tests

| ViewModel | Test class |
|---|---|
| DatabaseModel | DatabaseModelTest |
| ListingViewModel | ListingViewModelTest |
| ProfileCreatingViewModel | ProfileCreatingViewModelTest |
| GeocodingViewModel | GeoCodingViewModelTest |
| MapViewModel | MapViewModelTest (androidTest) |
| AdminAccessViewModel | AdminAccessViewModelTest |
| MessageScreenViewModel | MessageScreenViewModelTest |
| OwnerCampDetailsViewModel | OwnerCampDetailsViewModelTest |
| OwnerLotteryCreationViewModel | OwnerLotteryCreationViewModelTest |
| OwnerLotteryListViewModel | OwnerLotteryListViewModelTest |
| UserCampsiteDetailsViewModel | UserCampsiteDetailsViewModelTest |
| UserCampsiteSearchViewModel | UserCampsiteSearchViewModelTest |
| UserCurrentLotteriesViewModel | UserCurrentLotteriesViewModelTest |
| UserReservationsViewModel | UserReservationsViewModelTest |
| UserSignUpViewModel | UserSignUpViewModelTest |
| UserProfileViewModel | UserProfileViewModelTest |
| UserWonBookingViewModel | UserWonBookingViewModelTest |
| UserWonSelectionViewModel | UserWonSelectionViewModelTest |
| ReservationsViewModel | No class of its own |

`ReservationsViewModel` is in `UserReservationsDetailViewModel.kt`. `UserReservationsViewModel` extends it, and `UserReservationsViewModelTest` covers its 3 functions (`onFilterSelected` through the filter tests, `listingFor`, `cancelReservation`). Its header comment already says it tests both.

Note: the file name `UserReservationsDetailViewModel.kt` doesn't match the class name `ReservationsViewModel` inside it.

Next: paste the header into `MainActivity.kt`.
