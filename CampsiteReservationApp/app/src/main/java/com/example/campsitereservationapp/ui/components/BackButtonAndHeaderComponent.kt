package com.example.campsitereservationapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


/**
 * Reusable Component for the backbutton, title, and header
 * @param modifier modifier from parent compose
 * @param title optional title string for header
 * @param subHeader optional subheader string
 * @param onBack onclick event, leads to previous screen
 */
@Composable
fun BackButtonAndHeaderComponent(
    modifier: Modifier = Modifier,
    title: String? = null,
    subHeader: String? = null,
    onBack: () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedIconButton(
            onClick = onBack,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        if (title != null) {

            Spacer(Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (subHeader != null) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = subHeader,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


//preview with optional inputs
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF // ARGB Long (Green background)
)
@Composable
fun BackButtonAndHeaderPreview(){
    BackButtonAndHeaderComponent(
        modifier = Modifier.fillMaxSize(),
        title = "Title",
        subHeader = "Subheader",
        onBack = {}
    )
}
