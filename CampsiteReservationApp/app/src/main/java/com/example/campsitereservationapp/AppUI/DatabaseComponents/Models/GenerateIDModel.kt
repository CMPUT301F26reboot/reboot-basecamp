package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models


class GenerateIDModel(): DatabaseModel() {
    val ID_length = 20 //length of ID fields in firebase


    //copied from https://stackoverflow.com/questions/46943860/idiomatic-way-to-generate-a-random-alphanumeric-string-in-kotlin
    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..ID_length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}