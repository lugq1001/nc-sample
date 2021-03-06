package com.nextcont.mobilization.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val username: String,
    val password: String,
    val birthday: String,
    val fullName: String,
    val gender: String,
    val id: String,
    val idCard: String,
    val role: String
)
