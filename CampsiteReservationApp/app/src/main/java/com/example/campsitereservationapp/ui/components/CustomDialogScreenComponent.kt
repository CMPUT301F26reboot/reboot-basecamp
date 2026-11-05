package com.example.campsitereservationapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


/** Dark navy estimated from the image. */
private val DoneButtonColor = Color(0xFF111827)

/** Reusable Dialog
 *@param onDismissRequest the event call after the Dialog is dismissed
 *@param title bolded title
 *@param description greyed out description of popup
 */
@Composable
fun CustomDialogScreenComponent(
    onDismissRequest: () -> Unit,
    title: String? = null,
    description: String? = null,

    ) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        CustomDialogContent(
            onDismissRequest = onDismissRequest,
            title = title,
            description = description,
        )
    }
}

/** The dialog's contents: the card, its text, and the Done button.
 * Note: Separated the content so it can be previewed (Dialog cannot be normally previewed) */
@Composable
private fun CustomDialogContent(
    onDismissRequest: () -> Unit,
    title: String? = null,
    description: String? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
            if (description != null) {
                if (title != null) {
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            if (title != null || description != null) {
                Spacer(Modifier.height(16.dp))
            }
            Button(
                onClick = onDismissRequest,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DoneButtonColor,
                    contentColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    }
}

//Preview of the dialog, using the text from the lottery created design
@Preview(showBackground = true, widthDp = 360)
@Composable
fun CustomDialogPreview(){
    CustomDialogContent(
        onDismissRequest = {},
        title = "Lottery created",
        description = "Your lottery for Bow Valley Riverside and Coastal Cliffside is live. Campers can enter until May 14, 2027.",
    )
}