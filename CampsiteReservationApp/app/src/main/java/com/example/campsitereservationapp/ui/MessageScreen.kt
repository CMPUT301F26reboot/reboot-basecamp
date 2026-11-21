package com.example.campsitereservationapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.ui.theme.Forest30
import com.example.campsitereservationapp.ui.theme.Gray50
import androidx.compose.runtime.*

@Composable
fun MessageScreen(
    yourID: String,
    otherID: String,
    onBack: () -> Unit,
    vm: MessageScreenViewModel = viewModel()
){
    vm.start(yourID, otherID)
    var nextText by remember {mutableStateOf("")}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilledTonalIconButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        LazyColumn(modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.8f)){
            items(vm.messages){ message ->
                if(message.senderID == yourID)
                    yourMessage(message)
                else
                    otherMessage(message)
            }
        }

        Row(modifier = Modifier.fillMaxSize()){
            TextField(
                value = nextText,
                onValueChange = {nextText = it},
                modifier = Modifier.fillMaxWidth(0.75f)
            )
            Button(onClick = {
                vm.sendMessage(yourID, otherID, nextText)
                nextText = ""
            },
                modifier = Modifier.fillMaxWidth()
                ){
                Text("Send")
            }
        }
    }
}

@Composable
fun yourMessage(
    message: Message
){
    var alignment = Alignment.CenterEnd

    Box(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(0.7f)
            .wrapContentSize()
            .widthIn(5.dp, 50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Forest30),
        contentAlignment = alignment,

    ){
        Text(message.text, color = Color.White, modifier = Modifier.padding(5.dp))
    }
}

@Composable
fun otherMessage(
    message: Message
){
    var alignment = Alignment.CenterEnd

    Box(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(0.7f)
            .wrapContentSize()
            .widthIn(5.dp, 50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Gray50),
        contentAlignment = alignment,

        ){
        Text(message.text, color = Color.Black, modifier = Modifier.padding(5.dp))
    }
}