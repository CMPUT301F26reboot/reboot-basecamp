package com.example.campsitereservationapp.ui.entrant

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import java.time.YearMonth

@Composable
fun tester(vm: UserCampsiteSearchViewModel = viewModel()){

    var s: List<Date> = vm.get365Dates().toList()
    var month = YearMonth.now()
    LazyColumn() {
        items(s){ date ->
            Text(date.toString())
        }
    }
}