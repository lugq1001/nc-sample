package com.nextcont.mobilization.network.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val username: String,
    val password: String,
    val fullName: String,
    val gender: Int,
    val birthday: String,
    val idCard: String,
    val role: Int

)
