package com.hajmola.up.data.auth

import com.google.gson.annotations.SerializedName

data class ForgotPasswordData(
    @SerializedName("reset_link") val resetLink: String
)