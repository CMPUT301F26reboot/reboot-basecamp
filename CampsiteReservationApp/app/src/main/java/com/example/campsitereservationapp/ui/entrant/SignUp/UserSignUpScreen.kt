package com.example.campsitereservationapp.ui.entrant.SignUp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.R

private val RebootGreen = Color(0xFF1A3A2C)
private val LabelGray = Color(0xFF404040)
private val TextGray = Color(0xFF3F3F46)
private val HintGray = Color(0xFF6B7280)
private val BorderGray = Color(0xFFE5E7EB)
private val InputBorder = Color(0xFF757575)

/**
 * Shown when the device is not recognized (US 01.09.01). A device that already has a profile is
 * signed in automatically; otherwise the user fills in their details to create a profile tied to
 * this device. Links to the owner sign up and to the policies (US 01.11.01).
 *
 * @param onSignedUp called once this device has a profile, either from before or just created.
 * @param onOwnerSignUp called when the user taps "here" to create a camp owner account.
 * @param onPolicies called when the user taps "Policies & Guidelines".
 * @param viewModel holds the form and creates the profile.
 */
@Composable
fun UserSignUpScreen(
    onSignedUp: () -> Unit,
    onOwnerSignUp: () -> Unit,
    onPolicies: () -> Unit,
    viewModel: UserSignUpViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.checkDevice(viewModel.readDeviceId(context))
    }

    LaunchedEffect(viewModel.isSignedUp) {
        if (viewModel.isSignedUp) onSignedUp()
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (viewModel.isCheckingDevice || viewModel.isSignedUp) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = RebootGreen)
        }
        return
    }

    UserSignUpContent(
        name = viewModel.name,
        onNameChange = { viewModel.name = it },
        nameError = viewModel.nameError,
        username = viewModel.username,
        onUsernameChange = { viewModel.username = it },
        usernameError = viewModel.usernameError,
        email = viewModel.email,
        onEmailChange = { viewModel.email = it },
        emailError = viewModel.emailError,
        phoneNumber = viewModel.phoneNumber,
        onPhoneNumberChange = { viewModel.phoneNumber = it },
        phoneNumberError = viewModel.phoneNumberError,
        isSaving = viewModel.isSaving,
        onCreateAccount = { viewModel.signUp(viewModel.readDeviceId(context)) },
        onOwnerSignUp = onOwnerSignUp,
        onPolicies = onPolicies,
        snackbarHostState = snackbarHostState
    )
}

/** The UI for the sign up form. */
@Composable
internal fun UserSignUpContent(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
    username: String,
    onUsernameChange: (String) -> Unit,
    usernameError: String?,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    phoneNumberError: String?,
    isSaving: Boolean,
    onCreateAccount: () -> Unit,
    onOwnerSignUp: () -> Unit,
    onPolicies: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.reboot_basecamp_logo),
                contentDescription = "Reboot Campsite Lottery logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderGray),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SignUpField("Name", name, onNameChange, "Alex Tremblay", nameError)
                    Spacer(modifier = Modifier.height(10.dp))
                    SignUpField("Username", username, onUsernameChange, "alextremblay", usernameError)
                    Spacer(modifier = Modifier.height(10.dp))
                    SignUpField(
                        "Email Address", email, onEmailChange, "alex@example.com", emailError,
                        KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SignUpField(
                        "Phone Number", phoneNumber, onPhoneNumberChange, "+1 (403) 555-0199",
                        phoneNumberError, KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onCreateAccount,
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = RebootGreen),
                        modifier = Modifier
                            .widthIn(min = 155.dp)
                            .height(44.dp)
                    ) {
                        Text(
                            text = if (isSaving) "Creating Account..." else "Create Account",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = BorderGray)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Looking to create a camp owner account? Create ",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        SignUpLink("here", 12, onOwnerSignUp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    SignUpLink("Policies & Guidelines", 14, onPolicies)
                }
            }
        }
    }
}

/**
 * A labelled text box that shows [error] underneath it when there is one. Built on
 * [BasicTextField] because OutlinedTextField can't be shorter than 56dp, and the mockup is 42dp.
 */
@Composable
private fun SignUpField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = InputBorder,
        focusedBorderColor = RebootGreen
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = LabelGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 15.sp, color = Color.Black),
            cursorBrush = SolidColor(RebootGreen),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
        ) { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                isError = error != null,
                placeholder = { Text(text = placeholder, fontSize = 15.sp, color = HintGray) },
                colors = colors,
                contentPadding = PaddingValues(horizontal = 12.dp),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = error != null,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = RoundedCornerShape(4.dp),
                        focusedBorderThickness = 1.dp,
                        unfocusedBorderThickness = 1.dp
                    )
                }
            )
        }
        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

/** Underlined text that acts as a link. */
@Composable
private fun SignUpLink(text: String, fontSize: Int, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.SemiBold,
        color = RebootGreen,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable(onClick = onClick)
    )
}