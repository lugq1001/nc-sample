package com.nextcont.mobilization.model

import com.nextcont.mobilization.MobApp
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class CheckIn(
    val address: String,
    val time: Long
) {

    companion object {

        private val STORE_KEY: String
            get() {
                return "${User.load()?.id}:CheckIn"
            }

        fun load(): List<CheckIn> {
            MobApp.sp.getString(STORE_KEY, null)?.let { string ->
                val json = Moshi.Builder().build()
                val type: Type = Types.newParameterizedType(List::class.java, CheckIn::class.java)
                val adapter: JsonAdapter<List<CheckIn>> = json.adapter(type)
                return adapter.fromJson(string) ?: listOf()
            } ?: kotlin.run {
                return listOf()
            }
        }

        val hasCheckIn: Boolean
            get() {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = sdf.format(System.currentTimeMillis())
                val records = load()
                records.forEach { r ->
                    val rDate = sdf.format(r.time)
                    if (today == rDate) {
                        return true
                    }
                }
                return false
            }
    }

    val displayTime: String
        get() {
            return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(time)
        }


    fun save() {
        val records = load().toMutableList()
        records.add(this)

        val json = Moshi.Builder().build()
        val type: Type = Types.newParameterizedType(List::class.java, CheckIn::class.java)
        val adapter: JsonAdapter<List<CheckIn>> = json.adapter(type)
        val jString = adapter.toJson(records)
        MobApp.sp.edit().putString(STORE_KEY, jString).apply()
    }

}