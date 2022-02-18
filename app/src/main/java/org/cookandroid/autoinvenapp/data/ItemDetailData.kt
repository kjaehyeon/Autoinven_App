package org.cookandroid.autoinvenapp.data

data class ItemDetailData (
    val item_id : Int,
    val name : String,
    val user_email : String,
    val i_state_id : Int,
    val note : String,
    val createdAt : String,
    val User : User,
    val ItemImages : List<ImageUrl>?
)

data class User(
    val name : String
)