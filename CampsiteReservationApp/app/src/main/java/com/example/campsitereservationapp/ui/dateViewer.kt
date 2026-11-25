package com.example.campsitereservationapp.ui.Owner.CampsiteDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel

@Composable
fun dateViewer(
    vm: DatabaseModel = viewModel()
){
    LazyColumn() {
        items(vm._reservations){ reservation ->
            Text(reservation.entrantID, fontSize = 20.sp)
            Column(){
                for(date in reservation.dates){
                    Text(date.toString())
                }
            }
        }
    }

//    LazyColumn() {
//        items(vm._lotteries){ lottery ->
//            Text(lottery.title, fontSize = 20.sp)
//            Column(){
//                for(date in lottery.dates){
//                    Text(date.toString())
//                }
//            }
//        }
//    }
}