package com.nextcont.mobilization.network

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.nextcont.mobilization.network.response.LoginResponse
import com.squareup.moshi.Moshi
import io.reactivex.rxjava3.core.Single


object MobApi {

    private const val BaseUrl = "http://192.168.15.60:7000/mob"
    //private const val BaseUrl = "http://192.168.50.122:7000/mob"
    //private const val BaseUrl = "https://ztserver.inecm.cn/mob"


    var json = Moshi.Builder().build()

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
                    .jsonBody("")
                    .responseString()

                //val resp = LoginResponse(User())
                //Timber.d("用户登录: $resp")
                //emitter.onSuccess(resp)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }



//    private inline fun <reified T> parseResponse(response: ResponseResultOf<String>): T {
//        val jsonAdapter: JsonAdapter<ResponseResultOf<String>> = json.adapter(ResponseResultOf<String>::class.java)
//        return when (val result = response.third) {
//            is Result.Failure -> {
//                throw Exception("${response.second.statusCode}.${result.error.message}")
//            }
//            is Result.Success -> {
//                val data = result.get()
//                Timber.d(data)
//                val resp = jsonAdapter.fromJson(data)!!
//                when (resp.code) {
//                    0 -> {
//                        jsonAdapter.fromJson(resp.data, T::class.java)
//                    }
//                    else -> throw Exception(resp.message)
//                }
//            }
//        }
//    }

}