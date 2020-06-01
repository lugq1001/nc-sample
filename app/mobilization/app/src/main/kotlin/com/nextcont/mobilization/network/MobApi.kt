package com.nextcont.mobilization.network

import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.nextcont.mobilization.network.request.LoginRequest
import com.nextcont.mobilization.network.request.RegisterRequest
import com.nextcont.mobilization.network.response.BaseResponse
import com.nextcont.mobilization.network.response.LoginResponse
import com.squareup.moshi.Moshi
import io.reactivex.rxjava3.core.Single

object MobApi {

    private const val BaseUrl = "http://192.168.15.60:7000/mob"
    //private const val BaseUrl = "http://192.168.50.122:7000/mob"
    //private const val BaseUrl = "https://ztserver.inecm.cn/mob"


    private var json = Moshi.Builder().build()

    /**
     * 注册
     */
    fun register(request: RegisterRequest): Single<Boolean> {
        return Single.create { emitter ->
            try {
                val reqApt = json.adapter(RegisterRequest::class.java)
                val body = reqApt.toJson(request)
                val response = "$BaseUrl/register"
                    .httpPost()
                    .jsonBody(body)
                    .responseString()

                parseResponse(response)
                emitter.onSuccess(true)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    /**
     * 登录
     */
    fun login(request: LoginRequest): Single<LoginResponse> {
        return Single.create { emitter ->
            try {
                val reqApt = json.adapter(LoginRequest::class.java)
                val body = reqApt.toJson(request)
                val response = "$BaseUrl/login"
                    .httpPost()
                    .jsonBody(body)
                    .responseString()

                val respString = parseResponse(response)
                val respApt = json.adapter(LoginResponse::class.java)
                val resp = respApt.fromJson(respString)
                emitter.onSuccess(resp)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }


    private fun parseResponse(response: ResponseResultOf<String>): String {
        when (val result = response.third) {
            is Result.Failure -> {
                throw Exception("${response.second.statusCode}.${result.error.message}")
            }
            is Result.Success -> {
                val data = result.get()
                val adapter = json.adapter(BaseResponse::class.java)
                val resp = adapter.fromJson(data)!!
                when (resp.code) {
                    0 -> {
                        return resp.data!!
                    }
                    else -> throw Exception(resp.message)
                }
            }
        }
    }

}