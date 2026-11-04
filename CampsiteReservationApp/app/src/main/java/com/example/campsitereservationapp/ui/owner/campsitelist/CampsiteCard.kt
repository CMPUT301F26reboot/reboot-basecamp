package com.example.campsitereservationapp.ui.owner.campsitelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing

@Composable
fun CampsiteCard(listing: Listing, onDetailsClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            CampsiteThumbnail(photoUrl = listing.photoUrls.firstOrNull())
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    listing.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Up to ${listing.maxOccupancy} Guests", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(2.dp))
                RatingRow(rating = listing.rating, numOfReviews = listing.numOfReviews)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onDetailsClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Details")
                }
            }
        }
    }
}

@Composable
fun CampsiteThumbnail(photoUrl: String?) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/** Star rating, or "Not yet rated" when the listing has no reviews. */
@Composable
fun RatingRow(rating: Double, numOfReviews: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (numOfReviews > 0) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(rating.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.width(4.dp))
            Text(
                "($numOfReviews reviews)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Not yet rated",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
