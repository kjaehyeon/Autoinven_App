package org.cookandroid.autoinvenapp.data

data class ItemDetailData (
    val it_name : String,
    val buyer_name : String,
    val current_status : Int,
    val size : Int,
    val warehouse_name : String,
    val note : String,
    val created_datetime : String,
    val image_url : String
)
