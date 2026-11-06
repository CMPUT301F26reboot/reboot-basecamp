package com.example.campsitereservationapp.ui.owner.CampsiteRegister

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Lets an owner register a new campsite listing (US 02.01.01). Stateful wrapper that wires
 * [OwnerCampDetailsViewModel] to [OwnerCampDetailsContent]; navigates back via [onPublished] once
 * the listing has been written to Firestore.
 *
 * @param ownerId the current owner. Comes from device identification once that exists.
 */
@Composable
fun OwnerCampDetailsScreen(
    ownerId: String,
    onBack: () -> Unit,
    onPublished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OwnerCampDetailsViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel.isPublished) {
        if (viewModel.isPublished) onPublished()
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    OwnerCampDetailsContent(
        photos = viewModel.photos,
        onAddPhotos = viewModel::addPhotos,
        onRemovePhoto = viewModel::removePhoto,
        siteName = viewModel.siteName,
        onSiteNameChange = { viewModel.siteName = it },
        location = viewModel.location,
        onLocationChange = { viewModel.location = it },
        maxOccupancy = viewModel.maxOccupancy,
        onIncrementOccupancy = viewModel::incrementOccupancy,
        onDecrementOccupancy = viewModel::decrementOccupancy,
        overview = viewModel.overview,
        onOverviewChange = { viewModel.overview = it },
        rules = viewModel.rules,
        onRulesChange = { viewModel.rules = it },
        rulesAcceptanceRequired = viewModel.rulesAcceptanceRequired,
        onRulesAcceptanceRequiredChange = { viewModel.rulesAcceptanceRequired = it },
        tags = viewModel.tags,
        onAddTag = viewModel::addTag,
        onRemoveTag = viewModel::removeTag,
        canPublish = viewModel.canPublish,
        isPublishing = viewModel.isPublishing,
        onPublish = { viewModel.publish(ownerId, context) },
        onBack = onBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

/**
 * Pure UI for the registration form. Stateless so it can be driven entirely by its parameters in
 * a Compose Preview, with no database involved. Assembles the sections defined in this package's
 * other files (PhotosSection.kt, BasicInformationSection.kt, etc.) in mockup order.
 */
@Composable
fun OwnerCampDetailsContent(
    photos: List<Uri>,
    onAddPhotos: (List<Uri>) -> Unit,
    onRemovePhoto: (Uri) -> Unit,
    siteName: String,
    onSiteNameChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    maxOccupancy: Int,
    onIncrementOccupancy: () -> Unit,
    onDecrementOccupancy: () -> Unit,
    overview: String,
    onOverviewChange: (String) -> Unit,
    rules: String,
    onRulesChange: (String) -> Unit,
    rulesAcceptanceRequired: Boolean,
    onRulesAcceptanceRequiredChange: (Boolean) -> Unit,
    tags: List<String>,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    canPublish: Boolean,
    isPublishing: Boolean,
    onPublish: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier,
        topBar = { RegisterCampsiteTopBar(onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PhotosSection(photos = photos, onAddPhotos = onAddPhotos, onRemovePhoto = onRemovePhoto)
            BasicInformationSection(
                siteName = siteName,
                onSiteNameChange = onSiteNameChange,
                location = location,
                onLocationChange = onLocationChange,
                maxOccupancy = maxOccupancy,
                onIncrement = onIncrementOccupancy,
                onDecrement = onDecrementOccupancy
            )
            DescriptionSection(overview = overview, onOverviewChange = onOverviewChange)
            RulesSection(
                rules = rules,
                onRulesChange = onRulesChange,
                acceptanceRequired = rulesAcceptanceRequired,
                onAcceptanceRequiredChange = onRulesAcceptanceRequiredChange
            )
            TagsSection(tags = tags, onAddTag = onAddTag, onRemoveTag = onRemoveTag)
            MapPlaceholderSection()
            PublishButton(enabled = canPublish && !isPublishing, isPublishing = isPublishing, onClick = onPublish)
        }
    }
}

/**
 * Top bar with a back arrow, the screen title, and a profile icon (not yet wired to anything).
 */
@Composable
private fun RegisterCampsiteTopBar(onBack: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Register Campsite",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(end = 12.dp)) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
