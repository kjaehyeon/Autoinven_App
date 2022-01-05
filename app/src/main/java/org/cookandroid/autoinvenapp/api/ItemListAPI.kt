package org.cookandroid.autoinvenapp.api

import org.cookandroid.autoinvenapp.data.ItemListResponseData
import org.cookandroid.autoinvenapp.data.WareHouseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ItemListAPI {
    @GET("/api/itemlist")
    fun getItemList(
        @Query("wid") wid : String?
    ): Call<List<ItemListResponseData>>
}