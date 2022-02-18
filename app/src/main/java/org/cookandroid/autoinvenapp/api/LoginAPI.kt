package org.cookandroid.autoinvenapp.api

import org.cookandroid.autoinvenapp.data.LoginInfo
import org.cookandroid.autoinvenapp.data.Request
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginAPI {
    @POST("/api/auth/signin")
    fun postLogin(
       @Body loginInfo: LoginInfo
    ): Call<Request>
}