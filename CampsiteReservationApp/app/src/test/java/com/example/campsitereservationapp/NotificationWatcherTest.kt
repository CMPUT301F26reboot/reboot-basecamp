/**
 * This file tests the check NotificationWatcher uses to decide if a notification pops up on the phone.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.ui.entrant.Notifications.NotificationWatcher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [NotificationWatcher.shouldShowPhoneNotification], which stops phone notifications
 * when the entrant has opted out (US 01.10.01) or has already seen them.
 */
class NotificationWatcherTest {

    @Test
    fun shouldShowPhoneNotification_newNotificationShouldShow() {
        assertTrue(NotificationWatcher.shouldShowPhoneNotification(true, false, false))
    }

    @Test
    fun shouldShowPhoneNotification_notificationsOffShouldNotShow() {
        assertFalse(NotificationWatcher.shouldShowPhoneNotification(false, false, false))
    }

    @Test
    fun shouldShowPhoneNotification_alreadyShownShouldNotShowAgain() {
        assertFalse(NotificationWatcher.shouldShowPhoneNotification(true, true, false))
    }

    @Test
    fun shouldShowPhoneNotification_alreadyViewedShouldNotShow() {
        assertFalse(NotificationWatcher.shouldShowPhoneNotification(true, false, true))
    }
}
