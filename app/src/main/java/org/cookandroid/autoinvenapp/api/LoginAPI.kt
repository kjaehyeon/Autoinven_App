package org.cookandroid.autoinvenapp.api

import org.cookandroid.autoinvenapp.data.Request
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginAPI {
    @FormUrlEncoded
    @POST("/api/login")
    fun postLogin(
        @Field("id") id: String,
        @Field("pw") password: String,
    ): Call<Request>
}