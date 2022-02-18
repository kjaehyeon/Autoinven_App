package org.cookandroid.autoinvenapp.data

data class ItemDetailData (
    val name : String,
    val user_email : String,
    val current_status : Int,
    val note : String,
    val createdAt : String,
    val ItemImages : List<String>
)
