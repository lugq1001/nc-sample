package com.nextcont.mobilization.store

import android.content.Context
import android.content.SharedPreferences
import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.model.User
import io.realm.Realm
import io.realm.RealmConfiguration

object Store {

    private const val SP_KEY: String = "mob:sp:0"
    private const val DB_VER: Int = 0
    private val module = ChatRealmModule()


    private val realmConfiguration: RealmConfiguration
        get() {
            val user = User.load()?.username ?: ""
            return RealmConfiguration.Builder().name("mob:$user:$DB_VER.db")
                .deleteRealmIfMigrationNeeded().modules(module).build()
        }

    val sharedPreferences: SharedPreferences
        get() = MobApp.Shared.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)

    val openRealm: Realm
        get() {
            return Realm.getInstance(realmConfiguration)
        }

    fun closeRealm(realm: Realm?) {
        val rm = realm ?: return
        if (!rm.isClosed) {
            rm.close()
        }
    }

}