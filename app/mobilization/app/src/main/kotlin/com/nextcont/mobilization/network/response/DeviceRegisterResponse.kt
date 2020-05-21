package com.nextcont.mobilization.network.response

import com.nextcont.mobilization.model.User

data class DeviceRegisterResponse(
        val deviceId: String,
        val user: User?,
        val expired: Boolean,
        val disabled: Boolean
)