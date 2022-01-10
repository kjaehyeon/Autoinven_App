package org.cookandroid.autoinvenapp.api

import org.cookandroid.autoinvenapp.data.ItemDetailData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItemDetailAPI {
    @GET("/api/item")
    fun getItemDetail(
        @Query("qr") QR : String
    ): Call<ItemDetailData>
}