package com.example.campsitereservationapp.qrcode

/**
 * The link stored inside a campsite's promotional QR code (US 02.02.01, US 01.12.01), for example
 * `basecamp://campsite/abc123`. The owner side builds it, and the same pattern is registered as a
 * deep link in AndroidManifest.xml and on the `userSignUp` route, so scanning it with the phone's
 * camera app opens the app on that campsite's details.
 */
object CampsiteQrLink {

    const val SCHEME = "basecamp"

    const val HOST = "campsite"

    const val URI_PATTERN = "$SCHEME://$HOST/{listingId}"

    private const val PREFIX = "$SCHEME://$HOST/"

    /** The link to put in the QR code for the campsite with [listingId]. */
    fun build(listingId: String): String = PREFIX + listingId
}
