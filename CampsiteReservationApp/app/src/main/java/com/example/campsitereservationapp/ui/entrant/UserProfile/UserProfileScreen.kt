package com.example.campsitereservationapp.ui.entrant.UserProfile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import com.example.campsitereservationapp.utils.decodeBase64Image

private val TitleColor = Color(0xFF111827)
private val LabelColor = Color(0xFF6B7280)
private val IconColor = Color(0xFF9CA3AF)
private val SurfaceGray = Color(0xFFF3F4F6)
private val SwitchGreen = Color(0xFF10B981)
private val DeleteRed = Color(0xFFEF4444)
private val LinkGreen = Color(0xFF1A3A2C)

/**
 * Shows the user's profile and lets them change their details (US 01.07.03), delete their profile
 * (US 01.08.01), turn notifications on or off (US 01.10.01), and view the policies (US 01.11.01).
 *
 * @param profileId the Firestore document ID of the user's profile, which is their device ID.
 * @param onBack called when the user taps the back button.
 * @param onPolicies called when the user taps "Policies & Guidelines".
 * @param onDeleted called once the profile has been deleted.
 * @param viewModel holds the profile and makes changes to it.
 */
@Composable
fun UserProfileScreen(
    profileId: String,
    onBack: () -> Unit,
    onPolicies: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: UserProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) viewModel.updateProfilePicture(context, uri)
    }

    LaunchedEffect(profileId) {
        viewModel.load(profileId)
    }

    LaunchedEffect(viewModel.isDeleted) {
        if (viewModel.isDeleted) onDeleted()
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    if (viewModel.isLoading || viewModel.isDeleted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LinkGreen)
        }
        return
    }

    UserProfileContent(
        valueOf = viewModel::valueOf,
        profilePicture = viewModel.profilePicture,
        notificationsEnabled = viewModel.notificationsEnabled,
        editingField = viewModel.editingField,
        draft = viewModel.draft,
        onDraftChange = { viewModel.draft = it },
        fieldError = viewModel.fieldError,
        isSaving = viewModel.isSaving,
        onEdit = viewModel::startEditing,
        onSaveEdit = viewModel::saveEdit,
        onNotificationsChange = viewModel::updateNotifications,
        onEditPicture = {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onDelete = viewModel::deleteProfile,
        onBack = onBack,
        onPolicies = onPolicies,
        snackbarHostState = snackbarHostState
    )
}

/**
 * The UI for the profile screen. Everything it shows comes from its parameters, and every tap goes
 * back out through a callback.
 *
 * @param valueOf returns the saved value of a field.
 * @param profilePicture the user's photo as Base64 text, or empty if they haven't added one.
 * @param notificationsEnabled whether the notifications toggle is on.
 * @param editingField the field being edited, or null if there isn't one.
 * @param draft the text typed into [editingField].
 * @param onDraftChange called when the user types in [editingField].
 * @param fieldError the message to show under [editingField], or null if there isn't one.
 * @param isSaving true while a change is being written, which disables the buttons.
 * @param onEdit called with a field when the user taps its pencil.
 * @param onSaveEdit called when the user confirms their edit.
 * @param onNotificationsChange called with the new setting when the user taps the toggle.
 * @param onEditPicture called when the user taps "Edit Profile Picture".
 * @param onDelete called once the user has confirmed that they want to delete their profile.
 * @param onBack called when the user taps the back button.
 * @param onPolicies called when the user taps "Policies & Guidelines".
 * @param snackbarHostState shows confirmation and error messages.
 */
@Composable
internal fun UserProfileContent(
    valueOf: (ProfileField) -> String,
    profilePicture: String,
    notificationsEnabled: Boolean,
    editingField: ProfileField?,
    draft: String,
    onDraftChange: (String) -> Unit,
    fieldError: String?,
    isSaving: Boolean,
    onEdit: (ProfileField) -> Unit,
    onSaveEdit: () -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onEditPicture: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    onPolicies: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteProfileDialog(
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            BackButtonAndHeaderScreenComponent(onBack = onBack)
            Spacer(modifier = Modifier.height(16.dp))

            ProfilePictureRow(profilePicture, isSaving, onEditPicture)
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Profile Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TitleColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileField.entries.forEach { field ->
                ProfileFieldRow(
                    field = field,
                    value = valueOf(field),
                    isEditing = field == editingField,
                    draft = draft,
                    onDraftChange = onDraftChange,
                    error = if (field == editingField) fieldError else null,
                    isSaving = isSaving,
                    onEdit = { onEdit(field) },
                    onSave = onSaveEdit
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Notifications", fontSize = 15.sp, color = LabelColor)
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsChange,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = SwitchGreen,
                        checkedThumbColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { showDeleteDialog = true },
                enabled = !isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeleteRed,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = "Delete Profile", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Policies & Guidelines",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = LinkGreen,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(onClick = onPolicies)
            )
        }
    }
}

/**
 * The profile picture, or a placeholder if there isn't one, and the button that changes it.
 *
 * @param profilePicture the user's photo as Base64 text, or empty if they haven't added one.
 * @param isSaving true while a change is being written, which disables the button.
 * @param onEditPicture called when the user taps "Edit Profile Picture".
 */
@Composable
private fun ProfilePictureRow(profilePicture: String, isSaving: Boolean, onEditPicture: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SurfaceGray),
            contentAlignment = Alignment.Center
        ) {
            if (profilePicture.isEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "No profile picture",
                    tint = IconColor,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                AsyncImage(
                    model = decodeBase64Image(profilePicture),
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = onEditPicture,
            enabled = !isSaving,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceGray,
                contentColor = TitleColor
            )
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Edit Profile Picture", fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * One field of the profile. Tapping the pencil turns its value into a text box.
 *
 * @param field the field this row shows.
 * @param value the saved value of the field.
 * @param isEditing true if this field is being edited.
 * @param draft the text typed into the text box.
 * @param onDraftChange called when the user types in the text box.
 * @param error the message to show under the field, or null if there isn't one.
 * @param isSaving true while a change is being written, which disables the buttons.
 * @param onEdit called when the user taps the pencil.
 * @param onSave called when the user confirms their edit.
 */
@Composable
private fun ProfileFieldRow(
    field: ProfileField,
    value: String,
    isEditing: Boolean,
    draft: String,
    onDraftChange: (String) -> Unit,
    error: String?,
    isSaving: Boolean,
    onEdit: () -> Unit,
    onSave: () -> Unit
) {
    val valueStyle = TextStyle(fontSize = 15.sp, color = TitleColor, textAlign = TextAlign.End)
    val focusRequester = remember { FocusRequester() }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = field.label, fontSize = 15.sp, color = LabelColor)
            Spacer(modifier = Modifier.width(16.dp))

            if (isEditing) {
                Column(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = draft,
                        onValueChange = onDraftChange,
                        singleLine = true,
                        textStyle = valueStyle,
                        cursorBrush = SolidColor(LinkGreen),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardTypeOf(field),
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onSave() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                    HorizontalDivider(color = LinkGreen)
                }
                IconButton(onClick = onSave, enabled = !isSaving) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Save ${field.label}",
                        tint = LinkGreen
                    )
                }
                LaunchedEffect(Unit) { focusRequester.requestFocus() }
            } else {
                Text(
                    text = value,
                    style = valueStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit, enabled = !isSaving) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit ${field.label}",
                        tint = IconColor
                    )
                }
            }
        }
        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 48.dp)
            )
        }
    }
}

/**
 * Asks the user to confirm before their profile is deleted.
 *
 * @param onConfirm called when the user taps "Delete".
 * @param onDismiss called when the user taps "Cancel" or closes the dialog.
 */
@Composable
private fun DeleteProfileDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Profile?") },
        text = { Text("This deletes your profile and removes you from any waitlists you joined.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Delete", color = DeleteRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/** Returns the keyboard to show when editing [field]. */
private fun keyboardTypeOf(field: ProfileField): KeyboardType = when (field) {
    ProfileField.EMAIL -> KeyboardType.Email
    ProfileField.PHONE_NUMBER -> KeyboardType.Phone
    else -> KeyboardType.Text
}
