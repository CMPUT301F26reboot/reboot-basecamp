package com.example.campsitereservationapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Converts campsite photos to Base64 text so they can be stored directly in Firestore
 * documents, instead of Firebase Storage.
 */

// MAX_PHOTOS is 4, leaving room to raise these without
// risking Firestore's 1 MiB document limit.
private const val MAX_DIMENSION = 640
private const val JPEG_QUALITY = 80

/** Resizes the photo at [uri] to fit within [MAX_DIMENSION]x[MAX_DIMENSION], compresses it, and Base64-encodes it. Returns null if the photo can't be read. */
fun encodePhotoToBase64(context: Context, uri: Uri): String? {
    val bitmap = decodeScaledBitmap(context, uri) ?: return null
    val bytes = ByteArrayOutputStream().use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
        stream.toByteArray()
    }
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

/** Decodes a Base64 string back into raw image bytes, or null if [value] isn't valid Base64. */
fun decodeBase64Image(value: String): ByteArray? =
    runCatching { Base64.decode(value, Base64.DEFAULT) }.getOrNull()

/** Loads the image at [uri], downsampled so neither side is larger than [MAX_DIMENSION]. */
private fun decodeScaledBitmap(context: Context, uri: Uri): Bitmap? = runCatching {
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream -> BitmapFactory.decodeStream(stream, null, options) }
    options.inSampleSize = calculateInSampleSize(options, MAX_DIMENSION, MAX_DIMENSION)
    options.inJustDecodeBounds = false
    val sampled = context.contentResolver.openInputStream(uri)
        ?.use { stream -> BitmapFactory.decodeStream(stream, null, options) }
        ?: return@runCatching null

    val longestSide = maxOf(sampled.width, sampled.height)
    if (longestSide <= MAX_DIMENSION) {
        sampled
    } else {
        val scale = MAX_DIMENSION.toFloat() / longestSide
        Bitmap.createScaledBitmap(sampled, (sampled.width * scale).toInt(), (sampled.height * scale).toInt(), true)
    }
}.getOrNull()

/** Largest power-of-two downsample factor that still keeps the image at least [reqWidth]x[reqHeight]. */
private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
