package com.nextcont.mobilization.model

import android.content.Context
import com.google.gson.Gson
import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.util.Util

class Account {

    var login = false
    var disableTime = 0L
    var networkType: NetworkType? = null

    companion object {

        private const val STORE_NAME = "Account"

        var deviceId = ""

        var user: User? = null


        fun load(): Account {
            val json = MobApp.sp.getString(STORE_NAME, null) ?: return Account()
            return Gson().fromJson(json, Account::class.java)
        }

        fun currentNetwork(context: Context): NetworkType {
            if (Util.isConnectedMobile(context)) {
                return NetworkType.Mobile
            }
            if (Util.isConnectedWifi(context)) {
                return NetworkType.Wifi
            }
            if (Util.isAirplaneModeOn(context)) {
                return NetworkType.Airplane
            }
            return NetworkType.Mobile
        }
    }

    fun save() {
        val editor = MobApp.sp.edit()
        val json = Gson().toJson(this)
        editor.putString(STORE_NAME, json)
        editor.apply()
    }

    enum class NetworkType {
        Wifi,
        Mobile,
        Airplane;
    }
}