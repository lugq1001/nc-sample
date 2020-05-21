package com.nextcont.mobilization

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

class MobApp: Application() {

    companion object {

        lateinit var Shared: MobApp private set
        lateinit var Context: Context private set

        val sp: SharedPreferences
            get() = Context.getSharedPreferences("mob_data_3", MODE_PRIVATE)

    }


    override fun onCreate() {
        super.onCreate()
        Shared = this
        Context = applicationContext
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }


}