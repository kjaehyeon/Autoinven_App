package org.cookandroid.autoinvenapp.data

import com.google.gson.annotations.SerializedName;

data class Request (
    @SerializedName("token")
    val token: String?,
)

data class LoginInfo (
    val email: String,
    val password: String,
)

