package com.example.campsitereservationapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.ui.theme.Forest30
import com.example.campsitereservationapp.ui.theme.Gray50


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
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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

            Text(vm.getName(otherID), fontSize = 24.sp)
        }


        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .weight(1f),
            verticalArrangement = Arrangement.Bottom
        ){
            items(vm.messages){ message ->
                if(message.senderID == yourID)
                    yourMessage(message)
                else
                    otherMessage(message)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f)){
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ){
        Box(
            modifier = Modifier
                .padding(5.dp)
                .wrapContentSize()
                .widthIn(5.dp, 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Forest30)
        ){
            Text(message.text, color = Color.White, modifier = Modifier.padding(10.dp))
        }
    }

}

@Composable
fun otherMessage(
    message: Message
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ){
        Box(
            modifier = Modifier
                .padding(10.dp)
                .wrapContentSize()
                .widthIn(5.dp, 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Gray50)
        ){
            Text(message.text, color = Color.Black, modifier = Modifier.padding(10.dp))
        }
    }
}