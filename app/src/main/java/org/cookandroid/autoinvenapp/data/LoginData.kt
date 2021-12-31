package org.cookandroid.autoinvenapp.data

import com.google.gson.annotations.SerializedName;

data class Request (
    @SerializedName("token")
    val token: String?,
)

