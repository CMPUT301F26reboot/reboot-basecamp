package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.google.firebase.firestore.util.CustomClassMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests for [Profile], the account model created by the sign up screen. */
class ProfileTest {

    @Test
    fun newProfile_noArguments_hasEmptyFields() {
        val profile = Profile()

        assertEquals("", profile.name)
        assertEquals("", profile.deviceId)
        assertEquals("", profile.username)
        assertEquals("", profile.email)
        assertEquals("", profile.phoneNumber)
        assertTrue(profile.notificationsEnabled)
    }

    @Test
    fun newProfile_withArguments_storesNameAndDeviceId() {
        val profile = Profile(name = "Alex Tremblay", deviceId = "device-123")

        assertEquals("Alex Tremblay", profile.name)
        assertEquals("device-123", profile.deviceId)
    }

    @Test
    fun firestoreFormat_containsEveryFieldExceptId() {
        val profile = Profile(name = "Alex Tremblay", deviceId = "device-123")
        profile.id = "device-123"
        profile.username = "alextremblay"
        profile.email = "alex@example.com"
        profile.phoneNumber = "+1 (403) 555-0199"

        val fields = CustomClassMapper.convertToPlainJavaTypes(profile) as Map<*, *>

        assertEquals("Alex Tremblay", fields["name"])
        assertEquals("device-123", fields["deviceId"])
        assertEquals("alextremblay", fields["username"])
        assertEquals("alex@example.com", fields["email"])
        assertEquals("+1 (403) 555-0199", fields["phoneNumber"])
        assertEquals(true, fields["notificationsEnabled"])
        assertFalse(fields.containsKey("id"))
    }
}
