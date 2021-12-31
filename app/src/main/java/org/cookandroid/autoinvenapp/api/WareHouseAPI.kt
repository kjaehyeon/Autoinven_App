package org.cookandroid.autoinvenapp.api

import org.cookandroid.autoinvenapp.data.WareHouseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface WareHouseAPI {
    @GET("/api/warehouse")
    fun getWareHouseList(
        @Header("token") token: String?
    ): Call<List<WareHouseResponse>>
}