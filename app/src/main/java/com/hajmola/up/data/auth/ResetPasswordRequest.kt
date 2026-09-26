package com.hajmola.up.data.auth

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(val token: String, @SerializedName("new_password") val newPassword: String)
