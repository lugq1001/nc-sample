package com.nextcont.sample.mobilization.handler

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.javalin.http.Context
import org.slf4j.Logger
import org.slf4j.LoggerFactory


abstract class Handler {

    private var logger: Logger = LoggerFactory.getLogger(Handler::class.java)

    protected val json: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun makeResponse(ctx: Context) {
        val response = try {
            response(ctx)
        } catch (e: Exception) {
            Response(-1, e.localizedMessage ?: "Unknown Error")
        }

        val adapter = json.adapter(Response::class.java)
        val respJson = adapter.toJson(response)
        logger.info(respJson)
        ctx.result(respJson).contentType("application/json")
    }

    protected fun commonError(message: String): Response {
        return Response(-1, message)
    }


    protected abstract fun response(ctx: Context): Response

    @JsonClass(generateAdapter = true)
    data class Response(val code: Int, val message: String, val data: String? = null)

}

