package com.nextcont.sample

import com.nextcont.sample.mobilization.Mobilization
import io.javalin.Javalin

lateinit var App: Javalin

class Program {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            App = Javalin.create().start(7000)
            Mobilization.start()

        }

    }

}