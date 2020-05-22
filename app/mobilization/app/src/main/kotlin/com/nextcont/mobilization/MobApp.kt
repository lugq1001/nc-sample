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



//    不透明 100% FF
//    95% F2
//    90% E6
//    85% D9
//    80% CC
//    75% BF
//    70% B3
//    65% A6
//    60% 99
//    55% 8C
//    半透明 50% 80
//    45% 73
//    40% 66
//    35% 59
//    30% 4D
//    25% 40
//    20% 33
//    15% 26
//    10% 1A
//    5% 0D
//    全透明 0% 00
}