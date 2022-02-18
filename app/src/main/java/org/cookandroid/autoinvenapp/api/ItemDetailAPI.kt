package org.cookandroid.autoinvenapp.api

import org.cookandroid.autoinvenapp.data.ItemDetailData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemDetailAPI {
    @GET("/api/item/{item_id}")
    fun getItemDetail(
        @Path("item_id") item_id: String
    ): Call<ItemDetailData>
}