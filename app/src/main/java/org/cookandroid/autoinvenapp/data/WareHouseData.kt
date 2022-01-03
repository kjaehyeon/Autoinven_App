package org.cookandroid.autoinvenapp.data


data class WareHouseResponse(
    val wid : Int,
    val name : String,
    val address : String,
    val usage : Float,
    val image : List<String>,
    val description : String
)