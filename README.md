# Map Implementation Documentation

> **Library:** [https://github.com/p-lr/MapCompose](https://github.com/p-lr/MapCompose)

## Table of Contents

1. [Context](#1-context)
2. [General Breakdown of MapScreen.kt](#2-general-breakdown-of-mapscreenkt)
3. [MapViewModel()](#3-mapviewmodel)
4. [Dependencies](#4-dependencies)
5. [Integration with Our Project (OtherScreen.kt)](#5-integration-with-our-project-otherscreenkt)

---

## 1. Context

### Tiles in Digital Mapping

Small squares (usually 256 x 256 pixels) that get rendered on demand from a data source.

In this implementation there are 17 levels, indexed 0-16. (The implementation automatically subtracts 1 from its input.)

### Pyramid

Pyramid: referring to Tile Pyramid/image pyramid.

![Tile pyramid][image1]

Each level of the pyramid has a different level of resolution.

Every time you move down one level in the pyramid:

1. The resolution of the map doubles in both width and height.
2. The number of tiles quadruples (2<sup>level</sup> × 2<sup>level</sup>).

| Level | Description |
|---|---|
| **Level 0** (Top of Pyramid) | The entire map fits onto 1 single tile (2⁰ × 2⁰ = 1). You see continents, but no street details. |
| **Level 1** | The map is split into a 2×2 grid of 4 tiles (2¹ × 2¹ = 4). |
| **Level 2** | The map is split into a 4×4 grid of 16 tiles (2² × 2² = 16). |
| **Level 16** (Base of Pyramid) | The map is split into a 65,536 × 65,536 grid (over 4 billion tiles). Here you can see individual streets and buildings. |

---

## 2. General Breakdown of MapScreen.kt

### `makeTileStreamProvider()`

- Fetches the tiles from [openstreetmap.org](http://openstreetmap.org) with the connection is handled by `java.net.URL`.
- Never learned networking, or how caching works, all I really know is that dynamically gets tiles from [https://tile.openstreetmap.org/$zoomLvl/$col/$row](https://tile.openstreetmap.org/$zoomLvl/$col/$row) when we define what tile coordinate to get.

### What We Use from MapCompose

#### View from MapCompose

```kotlin
MapUI(
   modifier = Modifier.fillMaxSize(),
   state = viewModel.state
)
```

#### The State MapUI Needs

```kotlin
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
```

> The state is handled in the `MapViewModel()`.

#### Parameters of `MapState()`

| Parameter | Description |
|---|---|
| `levelCount` | Zoom level on pyramid. |
| `fullWidth`, `fullHeight` | Defines the full width and height of the map canvas at maximum resolution (256 px × 2^16 = 16,777,216 px). |
| `workerCount` | Number background threads to concurrently fetch, decode, and render map tiles so panning and zooming stay smooth. |

> **Gemini explanation**
>
> What `MapState` uses `fullWidth` and `fullHeight` for:
>
> 1. **Normalized Coordinate Mapping:** MapCompose maps relative coordinates (x and y between 0.0 and 1.0) to actual pixel offsets by multiplying by `fullWidth` and `fullHeight`.
> 2. **Tile Grid Calculations:** When you pan or zoom, MapCompose uses `fullWidth` and `fullHeight` to compute exactly which tile row and col indices are currently visible on screen to fetch from `TileStreamProvider`.
> 3. **Support for Non-Square Maps:** If you ever use MapCompose for non-geographic maps (like a 1920 × 1080 floor plan or a 3:2 wide panoramic photo), `fullWidth` and `fullHeight` allow `MapState` to handle non-square aspect ratios cleanly.

#### Lambda: `{}`

Defines how the viewport starts when first rendered:

- `scroll(x, y)` ← initial center of the map
- `scale()` ← sets initial zoom scale

#### `.addLayer()`

- Attaches tile loader to map so it updates as it's being used.

---

## 3. MapViewModel()

### Static Variables

- **`tileSize = 256`** → used in `mapSize` calculation (size of tile map in pixels).
- **`maxLevel = 16`** (maximum amount user can zoom out, see [Context](#1-context) for what levels are).
- **`mapSize`** =

  $$mapSize = tileSize \times 2^{maxLevel}$$

  Determines pixel size `MapState()` width and height parameters.

### Dynamic State

- `state = MapState()` — see [above](#the-state-mapui-needs).

### Methods

| Method | Description |
|---|---|
| `moveto(lat, lon)` | Takes new coordinates and moves map center to it. |
| `zoomIn()` | Called on button action. |
| `zoomOut()` | Called on button action. |

---

## 4. Dependencies

### MapCompose Library

```groovy
dependencies {
   // MapCompose library
   implementation 'ovh.plrapps:mapcompose:<version>'
}
```

### Permission to Use Internet to Fetch Map

In app XML file add:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Example of where it should go in XML file:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

   <uses-permission android:name="android.permission.INTERNET" />

   <application
       android:allowBackup="true"
       android:icon="@mipmap/ic_launcher"
       android:label="@string/app_name"
       android:roundIcon="@mipmap/ic_launcher_round"
       android:supportsRtl="true"
       android:theme="@style/Theme.MyApplication">
       <activity
           android:name=".MainActivity"
           android:exported="true"
           android:windowSoftInputMode="adjustResize"
           android:theme="@style/Theme.MyApplication">
           <intent-filter>
               <action android:name="android.intent.action.MAIN" />

               <category android:name="android.intent.category.LAUNCHER" />
           </intent-filter>
       </activity>
   </application>

</manifest>
```

---

## 5. Integration with Our Project (OtherScreen.kt)

### `OtherScreenViewModel()`

- Must set default values for lat and long as doubles because we have to pass some value down to the `OtherScreen` to start.
- Must have update coordinate functionality.

### `OtherScreen()`

- Simply call `MapScreen()` with lat and long from `OtherScreenViewModel()`.

