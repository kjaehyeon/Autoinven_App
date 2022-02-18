package org.cookandroid.autoinvenapp.api

import org.cookandroid.autoinvenapp.data.ItemListResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ItemListAPI {
    @GET("/api/warehouse/{warehouse_id}/items")
    fun getItemList(
        @Path("warehouse_id") warehouse_id : Int
    ): Call<List<ItemListResponseData>>
}