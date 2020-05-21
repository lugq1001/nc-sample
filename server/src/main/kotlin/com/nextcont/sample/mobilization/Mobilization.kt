package com.nextcont.sample.mobilization

import com.nextcont.sample.App
import com.nextcont.sample.mobilization.handler.LoginHandler
import com.nextcont.sample.mobilization.handler.RegisterHandler
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Mobilization {

    var logger: Logger = LoggerFactory.getLogger(Mobilization::class.java)

    private const val DB_VER = 0
    lateinit var Store: Nitrite

    fun start() {

        Store = nitrite {
            path = "mob${DB_VER}.db"
            compress = true
            autoCompact = false
        }

        App.ws("/websocket/:path") { ws ->
            ws.onConnect { ctx -> println("Connected") }
            ws.onMessage { ctx ->
                val user = ctx.message()
                val p = ctx.pathParam("path")
                ctx.send("${user} - ${p}"); // convert to json and send back
            }
            ws.onBinaryMessage { ctx -> println("Message") }
            ws.onClose { ctx -> println("Closed") }
            ws.onError { ctx -> println("Errored") }
        }

        val path = "mob"
        App.post("/${path}/register") { ctx -> RegisterHandler().makeResponse(ctx) }
        App.post("/${path}/login") { ctx -> LoginHandler().makeResponse(ctx) }
    }

}