package com.hajmola.up.data.auth

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("user_id") val userId: String,
    val name: String?,
    val username: String
)