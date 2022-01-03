package org.cookandroid.autoinvenapp.data

data class ItemListResponseData (
    val it_id : String,
    val name : String,
    val status : Int,
    val datetime : String,
    val buyer_name : String,
    val image : List<String>
)
