package com.nextcont.mobilization.model


data class User(
    val id: String,
    val username: String,
    val password: String,
    val fullName: String,
    val age: Int,
    val job: String,
    val avatar: String,
    val isAdmin: Boolean
)