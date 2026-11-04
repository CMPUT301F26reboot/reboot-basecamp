package com.example.campsitereservationapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.util.Date


/**
 * A reusable input composable for getting a range of dates from the user
 *
 * @param Title OPTIONAL
 * @param Subheader OPTIONAL
 * @param InfoDescription OPTIONAl - description underneath input
 * @param dates: MutableList<Date> - variable composable modifies
 */

@Composable
fun InputDateComponent(
    Title: String? = null,
    Subheader: String? = null,
    InfoDescription: String? = null,
    dates: MutableList<Date>,
    modifier: Modifier,

    ){

}


//Preview of where each variable string goes
@Preview(showBackground = true)
@Composable
fun InputDateComonentPreview(){
    val dates = mutableListOf<Date>()
    InputDateComponent(
        Title = "Title",
        Subheader = "Subheader",
        InfoDescription = "Info Description",
        dates = dates,
        modifier = Modifier.fillMaxWidth()
    )

}


//example of no use of optional input
@Preview(showBackground = true)
@Composable
fun InputDataPreview_NoText(){
    val dates = mutableListOf<Date>()
    InputDateComponent(
        dates = dates,
        modifier = Modifier.fillMaxWidth()
    )
}