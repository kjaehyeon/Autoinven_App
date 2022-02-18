package org.cookandroid.autoinvenapp.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PUT
import retrofit2.http.Path

interface ScanAPI {
    @FormUrlEncoded
    @PUT("/api/item/{item_id}/in")
    fun itemIn(
        @Path("item_id") item_id : String
    ): Call<Response<Void>>

    @FormUrlEncoded
    @PUT("/api/item/{item_id}/out")
    fun itemOut(
        @Path("item_id") item_id : String
    ): Call<Response<Void>>
}