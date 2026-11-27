/**
 * Shows a screen for the Admin to check messages between Owners and Entrants
 */

package com.example.campsitereservationapp.ui.Admin.AdminMessage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.ui.yourMessage
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import com.example.campsitereservationapp.ui.theme.*

@Composable
fun AdminMessageLogScreen(
    onBack: () -> Unit,
    vm: AdminMessageLogViewModel = viewModel()
){
    LaunchedEffect(Unit) {
        vm.reloadMessages()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        Row(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically){
            FilledTonalIconButton(
                onClick = onBack,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            FilledTonalIconButton(
                onClick = {vm.reloadMessages()}
            ){
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Text(text = "Message Logs", fontSize = 40.sp)
        }


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
            items(vm.messages){ message ->
                messageCard(message, vm)
            }
        }
    }
}

@Composable
fun messageCard(
    message: Message,
    vm: AdminMessageLogViewModel
){
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        border = BorderStroke(2.dp, Gray500)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        )
        {
            Row(
                Modifier.fillMaxWidth().padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)){
                Text(vm.getName(message.senderID))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "To Arrow")
                Text(vm.getName(message.receiverID))
            }

            Text(modifier = Modifier.fillMaxWidth().padding(5.dp), text = message.timestamp.toDate().toString())

            yourMessage(message)

            Spacer(Modifier.padding(5.dp))
        }
    }
}