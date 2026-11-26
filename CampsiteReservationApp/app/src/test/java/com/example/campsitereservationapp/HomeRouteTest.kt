/**
 * Tests for homeRouteFor, which picks the screen the app opens on after sign in.
 * Test:
 *  -a scanned campsite QR code always opens that campsite's details
 *  -an admin opens Admin Home, whatever their profile role
 *  -an owner opens Owner Home
 *  -an entrant opens User Home
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeRouteTest {

    //QR code scanned -> campsite details, even for an admin or owner
    @Test
    fun scannedListing_opensCampsiteDetails() {
        assertEquals("campsiteDetails/abc123", homeRouteFor(false, Profile.ROLE_ENTRANT, "abc123"))
        assertEquals("campsiteDetails/abc123", homeRouteFor(true, Profile.ROLE_OWNER, "abc123"))
    }

    //device in Admins -> admin home
    @Test
    fun admin_opensAdminHome() {
        assertEquals("adminHome", homeRouteFor(true, Profile.ROLE_ENTRANT, null))
        assertEquals("adminHome", homeRouteFor(true, Profile.ROLE_OWNER, null))
    }

    //owner profile -> owner home
    @Test
    fun owner_opensOwnerHome() {
        assertEquals("ownerHome", homeRouteFor(false, Profile.ROLE_OWNER, null))
    }

    //entrant profile -> user home
    @Test
    fun entrant_opensUserHome() {
        assertEquals("userHome", homeRouteFor(false, Profile.ROLE_ENTRANT, null))
    }
}
