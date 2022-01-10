package org.cookandroid.autoinvenapp.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PUT

interface ScanAPI {
    @FormUrlEncoded
    @PUT("/api/item/in")
    fun itemIn(
        @Field("qr") qr : String
    ): Call<Response<Void>>

    @FormUrlEncoded
    @PUT("/api/item/out")
    fun itemOut(
        @Field("qr") qr : String
    ): Call<Response<Void>>
}