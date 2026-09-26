package com.hajmola.up.data

data class AuthRequest(
    val username: String,
    val password: String
)

data class RegistrationRequest(
    val name: String,
    val username: String,
    val password: String
)
