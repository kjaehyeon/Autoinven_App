package org.cookandroid.autoinvenapp.data

data class ItemListResponseData (
    val it_id : Int,
    val name : String,
    val status : Int,
    val datetime : String,
    val buyer_name : String,
    val image : String?
)
