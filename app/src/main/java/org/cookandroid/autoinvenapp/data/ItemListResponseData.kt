package org.cookandroid.autoinvenapp.data

data class ItemListResponseData (
    val item_id : Int,
    val name : String,
    val i_state_id : Int,
    val createdAt : String,
    val user_email : String,
    val ItemImages : List<String>
)
