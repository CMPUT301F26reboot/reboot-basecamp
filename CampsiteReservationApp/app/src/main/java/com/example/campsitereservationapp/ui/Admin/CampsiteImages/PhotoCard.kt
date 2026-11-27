package com.example.campsitereservationapp.ui.Admin.CampsiteImages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.campsitereservationapp.ui.theme.DeleteRed
import com.example.campsitereservationapp.utils.decodeBase64Image

/**
 * Cards for each photo
 * @param photo [String] image being shown, byte64 representation of image
 * @param onDelete action after clicking delete button
 */
@Composable
fun PhotoCard(
    photo: String,
    onDelete: () -> Unit
){
    Column()
    {
        AsyncImage(
            model = decodeBase64Image(photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        //delete button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDelete,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = DeleteRed,
                contentColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text("Delete")
        }
    }

}