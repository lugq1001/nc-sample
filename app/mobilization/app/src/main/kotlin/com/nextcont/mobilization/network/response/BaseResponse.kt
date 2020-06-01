package com.nextcont.mobilization.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class BaseResponse(val code: Int, val message: String, val data: String? = null)