package com.example.campsitereservationapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

/** Text with no spaces, one @, and a dot in the domain, e.g. alex@example.com */
private val EMAIL_FORMAT = Regex("""^[^\s@]+@[^\s@]+\.[^\s@]+$""")

/** Digits with optional +, spaces, dashes, dots and brackets, e.g. +1 (403) 555-0199 */
private val PHONE_FORMAT = Regex("""^\+?(?:[\s().-]*\d){10,15}[\s().-]*$""")

/**
 * Returns the ID used to recognize this device. It is also the ID of the device's profile.
 *
 * @param context used to read the device ID.
 */
@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String =
    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

/**
 * Returns true if [email] looks like a valid email address.
 *
 * @param email the text to check. Spaces around it are ignored.
 */
fun isValidEmail(email: String): Boolean = EMAIL_FORMAT.matches(email.trim())

/**
 * Returns true if [phoneNumber] looks like a valid phone number.
 *
 * @param phoneNumber the text to check. Spaces around it are ignored.
 */
fun isValidPhoneNumber(phoneNumber: String): Boolean = PHONE_FORMAT.matches(phoneNumber.trim())
