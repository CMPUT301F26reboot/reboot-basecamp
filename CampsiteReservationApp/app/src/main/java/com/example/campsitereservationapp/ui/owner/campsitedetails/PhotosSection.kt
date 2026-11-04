package com.example.campsitereservationapp.ui.owner.campsitedetails

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campsitereservationapp.ui.theme.AvailableGreenBackground
import com.example.campsitereservationapp.ui.theme.AvailableGreenContent

/**
 * Photo picker row: an "Add Photo" circle that opens the system photo picker, followed by
 * thumbnails of the picked photos. The first photo is always labeled as the cover photo.
 */
@Composable
internal fun PhotosSection(
    photos: List<Uri>,
    onAddPhotos: (List<Uri>) -> Unit,
    onRemovePhoto: (Uri) -> Unit
) {
    val pickPhotos = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) onAddPhotos(uris)
    }

    Column {
        SectionHeader(
            title = "Photos & Media (Optional)",
            subtitle = "Upload high quality photos showing campsite amenities",
            trailing = {
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Text(
                        "${photos.size} / $MAX_PHOTOS Uploaded",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                AddPhotoButton(enabled = photos.size < MAX_PHOTOS) {
                    pickPhotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
            items(photos, key = { it.toString() }) { uri ->
                PhotoThumbnail(uri = uri, isCover = uri == photos.first(), onRemove = { onRemovePhoto(uri) })
            }
        }
    }
}

/** Circular button that launches the system photo picker. */
@Composable
private fun AddPhotoButton(enabled: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = AvailableGreenBackground,
            modifier = Modifier
                .size(72.dp)
                .clickable(enabled = enabled, onClick = onClick)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Add, contentDescription = "Add photo", tint = AvailableGreenContent)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text("Add Photo", style = MaterialTheme.typography.labelSmall)
    }
}

/** One picked photo: thumbnail, a "Cover Photo" badge on the first photo, and a remove button. */
@Composable
private fun PhotoThumbnail(uri: Uri, isCover: Boolean, onRemove: () -> Unit) {
    val displayName = rememberDisplayName(uri)
    Column {
        Box(modifier = Modifier.size(112.dp, 92.dp)) {
            AsyncImage(
                model = uri,
                contentDescription = displayName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
            if (isCover) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(6.dp)
                ) {
                    Text(
                        "★ Cover Photo",
                        color = MaterialTheme.colorScheme.surface,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .padding(4.dp)
            ) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface) {
                    Icon(Icons.Filled.Close, contentDescription = "Remove photo", modifier = Modifier.padding(2.dp))
                }
            }
        }
        if (displayName != null) {
            Text(
                displayName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                modifier = Modifier.width(112.dp)
            )
        }
    }
}

/** Resolves the display filename for a content [uri], or null while it's loading or unknown. */
@Composable
private fun rememberDisplayName(uri: Uri): String? {
    val context = LocalContext.current
    val state = produceState<String?>(initialValue = null, uri) {
        value = runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
            }
        }.getOrNull()
    }
    return state.value
}

private const val MAX_PHOTOS = 6
