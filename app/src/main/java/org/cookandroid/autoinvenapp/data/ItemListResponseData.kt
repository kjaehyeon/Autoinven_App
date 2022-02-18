package org.cookandroid.autoinvenapp.data

data class ItemListResponseData (
    val item_id : Int,
    val name : String,
    val state : Int,
    val date : String,
    val owner_name : String,
    val image : String?
)
