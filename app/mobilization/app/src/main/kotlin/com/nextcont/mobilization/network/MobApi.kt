package com.nextcont.mobilization.network

import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.nextcont.mobilization.network.response.*
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import java.lang.Exception

object MobApi {

    private const val BaseUrl = "http://192.168.15.60:7000/mob"
    //private const val BaseUrl = "http://192.168.50.122:7000/mob"
    //private const val BaseUrl = "https://ztserver.inecm.cn/mob"


    private val json: Gson = Gson()

    /**
     * 设备注册
     */
    fun deviceRegister(deviceName: String, fingerprint: String): Single<DeviceRegisterResponse> {
        return Single.create { emitter ->
            try {
                val params = mapOf(
                    Pair("deviceName", deviceName),
                    Pair("fingerprint", fingerprint)
                )

                val response = "$BaseUrl/device"
                    .httpPost()
                    .jsonBody(json.toJson(params))
                    .responseString()

                val resp = parseResponse<DeviceRegisterResponse>(response)
                Timber.d("注册设备: $resp")
                emitter.onSuccess(resp)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    /**
     * 登录
     */
    fun login(username: String, password: String, deviceId: String): Single<LoginResponse> {
        return Single.create { emitter ->
            try {
                val params = mapOf(
                    Pair("username", username),
                    Pair("password", password),
                    Pair("deviceId", deviceId)
                )

                val response = "$BaseUrl/login"
                    .httpPost()
                    .jsonBody(json.toJson(params))
                    .responseString()

                val resp = parseResponse<LoginResponse>(response)
                Timber.d("用户登录: $resp")
                emitter.onSuccess(resp)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun logout(deviceId: String): Single<LogoutResponse> {
        return Single.create { emitter ->
            try {
                val params = mapOf(
                    Pair("deviceId", deviceId)
                )

                val response = "$BaseUrl/logout"
                    .httpPost()
                    .jsonBody(json.toJson(params))
                    .responseString()

                val resp = parseResponse<LogoutResponse>(response)
                Timber.d("用户登出: $resp")
                emitter.onSuccess(resp)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun disable(deviceId: String): Single<LogoutResponse> {
        return Single.create { emitter ->
            try {
                val params = mapOf(
                    Pair("deviceId", deviceId)
                )

                val response = "$BaseUrl/disable"
                    .httpPost()
                    .jsonBody(json.toJson(params))
                    .responseString()

                val resp = parseResponse<LogoutResponse>(response)
                Timber.d("注销设备: $resp")
                emitter.onSuccess(resp)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    /**
     * 登录
     */
    fun evaluations(): Single<EvaluationsResponse> {
        return Single.create { emitter ->
            try {
                val response = "$BaseUrl/evaluations"
                    .httpGet()
                    .responseString()

                val resp = parseResponse<EvaluationsResponse>(response)
                Timber.d("获取训练列表: $resp")
                emitter.onSuccess(resp)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun users(): Single<UsersResponse> {
        return Single.create { emitter ->
            try {
                val response = "$BaseUrl/users"
                    .httpGet()
                    .responseString()

                val resp = parseResponse<UsersResponse>(response)
                Timber.d("获取用户: $resp")
                emitter.onSuccess(resp)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    private inline fun <reified T> parseResponse(response: ResponseResultOf<String>): T {
        return when (val result = response.third) {
            is Result.Failure -> {
                throw Exception("${response.second.statusCode}.${result.error.message}")
            }
            is Result.Success -> {
                val data = result.get()
                Timber.d(data)
                val resp = json.fromJson(data, BaseResponse::class.java)
                when (resp.code) {
                    0 -> {
                        json.fromJson(resp.data, T::class.java)
                    }
                    else -> throw Exception(resp.message)
                }
            }
        }
    }

}