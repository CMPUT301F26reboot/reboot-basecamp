/**
 * Tests for adminGateState, the decision behind AdminGate.
 * Test:
 *  -only the loading state is shown while isChecking is true, whatever isAdmin is
 *  -an admin is allowed once the check finishes
 *  -anyone else is denied once the check is complete
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.ui.Admin.AdminSetup.AdminGateState
import com.example.campsitereservationapp.ui.Admin.AdminSetup.adminGateState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AdminGateTest {

    //still checking and not an admin yet -> loading
    @Test
    fun checking_notAdmin_isLoading() {
        assertEquals(AdminGateState.Loading, adminGateState(isChecking = true, isAdmin = false))
    }

    //still checking even if isAdmin is already true -> loading, not the admin screen
    @Test
    fun checking_admin_isLoading() {
        assertEquals(AdminGateState.Loading, adminGateState(isChecking = true, isAdmin = true))
    }

    //check finished and device is an admin -> admin screen
    @Test
    fun doneChecking_admin_isAllowed() {
        assertEquals(AdminGateState.Allowed, adminGateState(isChecking = false, isAdmin = true))
    }

    //check finished and device is not an admin -> denied
    @Test
    fun doneChecking_notAdmin_isDenied() {
        assertEquals(AdminGateState.Denied, adminGateState(isChecking = false, isAdmin = false))
    }

    //loading only ever shows while checking
    @Test
    fun doneChecking_neverLoading() {
        assertNotEquals(AdminGateState.Loading, adminGateState(isChecking = false, isAdmin = true))
        assertNotEquals(AdminGateState.Loading, adminGateState(isChecking = false, isAdmin = false))
    }
}
