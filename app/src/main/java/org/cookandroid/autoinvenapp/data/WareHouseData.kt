package org.cookandroid.autoinvenapp.data

import com.google.gson.annotations.SerializedName


data class WareHouseResponse(
    val warehouse_id : Int,
    val name_ko : String,
    val address1_ko : String,
    val WarehouseImages : List<String>?,
    val note_ko : String?
)