package org.cookandroid.autoinvenapp.data

import com.google.gson.annotations.SerializedName


data class WareHouseResponse(
    val wid : Int,
    val name : String,
    val address : String,
    val usage : Int,
    val images : List<String>,
    @SerializedName("info")
    val description : String
)