package com.nextcont.mobilization.model

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.R
import com.nextcont.mobilization.network.response.LoginResponse
import com.nextcont.mobilization.store.Store
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String,
    val password: String,
    val fullName: String,
    val birthday: String,
    val idCard: String,
    val gender: Gender,
    val role: Role
) {

    enum class Gender {

        Male,
        Female;

        val code: Int
            get() {
                return when (this) {
                    Male -> 0
                    Female -> 1
                }
            }

        val string: String
            get() {
                return when (this) {
                    Male -> "男"
                    Female -> "女"
                }
            }

    }

    enum class Role {
        Sergeant,
        Soldier;

        val code: Int
            get() {
                return when (this) {
                    Sergeant -> 0
                    Soldier -> 1
                }
            }

        val string: String
            get() {
                return when (this) {
                    Sergeant -> "士官"
                    Soldier -> "士兵"
                }
            }

    }

    val avatar: Drawable
        get() {
            return ContextCompat.getDrawable(
                MobApp.Context, when (gender) {
                    Gender.Male -> {
                        R.mipmap.avatar_1
                    }
                    Gender.Female -> {
                        R.mipmap.avatar_0
                    }
                }
            )!!
        }

    companion object {

        private const val STORE_NAME = "User"

        fun convert(resp: LoginResponse): User {

            return User(
                resp.id,
                resp.username,
                resp.password,
                resp.fullName,
                resp.birthday,
                resp.idCard,
                Gender.valueOf(resp.gender),
                Role.valueOf(resp.role)
            )
        }

        fun load(): User? {
            val json = Store.sharedPreferences.getString(STORE_NAME, null) ?: return null
            val ms = Moshi.Builder().build()
            return ms.adapter(User::class.java).fromJson(json)
        }

    }

    fun save() {
        val editor = Store.sharedPreferences.edit()
        val ms = Moshi.Builder().build()
        val json = ms.adapter(User::class.java).toJson(this)
        editor.putString(STORE_NAME, json)
        editor.apply()
    }

    fun clear() {
        Store.sharedPreferences.edit().remove(STORE_NAME).apply()
    }

}