/*
 * CampsiteQrCode.kt
 *
 * Owner side of the campsite QR code feature (US 02.02.01). Generates a listing's promotional QR
 * code with qrcode-kotlin, shows it in a dialog from the owner's campsite list, and lets the
 * owner download it.
 */
package com.example.campsitereservationapp.qrcode

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import qrcode.QRCode

/**
 * Makes a campsite's promotional QR code (US 02.02.01) with qrcode-kotlin and shows it to the
 * owner in a dialog, with a Download button that saves it as a PNG in the phone's Pictures folder
 * so the owner can print or post it. The code holds [CampsiteQrLink.build] for the listing, so
 * scanning it opens that campsite's details on the entrant side (US 01.12.01).
 */
@Composable
fun CampsiteQrDialog(
    campsiteName: String,
    listingId: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val qr = remember(listingId) { campsiteQrBitmap(listingId) }
    val image = remember(qr) { qr.asImageBitmap() }
    var isSaving by remember { mutableStateOf(false) }

    val download = {
        isSaving = true
        saveQrAndReport(context, scope, qr, campsiteName) { isSaving = false }
    }
    val storagePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) download()
        else Toast.makeText(context, "Storage permission is needed to save the QR code.", Toast.LENGTH_SHORT).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(campsiteName, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(bitmap = image, contentDescription = "QR code for $campsiteName", modifier = Modifier.size(240.dp))
                DownloadButton(
                    enabled = !isSaving,
                    onClick = {
                        if (needsStoragePermission(context)) {
                            storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            download()
                        }
                    }
                )
                Text(
                    "Entrants can scan this code to open the campsite's details.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

/** Dark full-width "Download PNG" button shown under the QR code. */
@Composable
private fun DownloadButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Filled.Download, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Download PNG", fontWeight = FontWeight.Bold)
    }
}

/**
 * Renders the QR code for [listingId] with qrcode-kotlin. On Android its native image is a
 * [Bitmap], which is shown in the dialog and saved by [saveQrToPictures].
 */
fun campsiteQrBitmap(listingId: String): Bitmap =
    QRCode.ofSquares()
        .withSize(20)
        .build(CampsiteQrLink.build(listingId))
        .render()
        .nativeImage() as Bitmap

/**
 * Saves [qr] off the main thread, shows a toast saying whether it worked, then calls [onDone].
 */
private fun saveQrAndReport(
    context: Context,
    scope: CoroutineScope,
    qr: Bitmap,
    campsiteName: String,
    onDone: () -> Unit
) {
    scope.launch {
        val saved = withContext(Dispatchers.IO) { saveQrToPictures(context, qr, campsiteName) }
        val message = if (saved) "QR code saved to Pictures/Basecamp." else "Couldn't save the QR code."
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        onDone()
    }
}

/**
 * True on Android 9 and below when the app hasn't been granted storage access yet. Android 10 and
 * up let the app add images to Pictures without any permission.
 */
private fun needsStoragePermission(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED

/**
 * Writes [qr] as a PNG into Pictures/Basecamp through MediaStore, so it appears in the owner's
 * gallery. The file is named after [campsiteName]. Returns true when the image was written.
 */
fun saveQrToPictures(context: Context, qr: Bitmap, campsiteName: String): Boolean {
    val safeName = campsiteName.replace(Regex("[^A-Za-z0-9]+"), "-").trim('-').ifEmpty { "campsite" }
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$safeName-qr-${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Basecamp")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false

    val written = runCatching {
        resolver.openOutputStream(uri)?.use { qr.compress(Bitmap.CompressFormat.PNG, 100, it) } ?: false
    }.getOrDefault(false)

    if (!written) {
        resolver.delete(uri, null, null)
        return false
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    }
    return true
}
