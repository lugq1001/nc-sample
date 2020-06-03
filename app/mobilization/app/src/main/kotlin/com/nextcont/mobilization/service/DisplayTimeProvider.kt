package com.nextcont.mobilization.service

import android.annotation.SuppressLint
import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 显示时间处理器
 */
@SuppressLint("ConstantLocale")
internal object DisplayTimeProvider {

    private val MMdd: SimpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    private val HHmm: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun conversationTime(microsecond: Long): String {
        if (microsecond == 0L) {
            return "-"
        }

        val minute = TimeUnit.MICROSECONDS.toMinutes(microsecond)

        val nowMinute = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())
        val diffMinute = nowMinute - minute

        return when (diffMinute) {
            in 0..5 -> {
                MobApp.Shared.getString(R.string.time_display_just_now)
            }
            in 5..60 * 24 -> {
                val millisecond = TimeUnit.MICROSECONDS.toMillis(microsecond)
                HHmm.format(Date(millisecond))
            }
            else -> {
                val millisecond = TimeUnit.MICROSECONDS.toMillis(microsecond)
                MMdd.format(Date(millisecond))
            }
        }
    }

    fun onlineTime(microsecond: Long): String {
        if (microsecond == 0L) {
            return MobApp.Shared.getString(R.string.time_display_long_time_age)
        }

        val minute = TimeUnit.MICROSECONDS.toMinutes(microsecond)

        val nowMinute = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())
        val diffMinute = nowMinute - minute

        val dayMinute = 60 * 24

        return when (diffMinute) {
            in 0..5 -> {
                MobApp.Shared.getString(R.string.time_display_just_now)
            }
            in 5..60 -> {
                "$diffMinute ${MobApp.Shared.getString(R.string.time_display_minute_ago)}"
            }
            in 60..dayMinute -> {
                "${diffMinute / 60} ${MobApp.Shared.getString(R.string.time_display_hour_ago)}"
            }
            in dayMinute..dayMinute * 5 -> {
                "${diffMinute / 60 / 24} ${MobApp.Shared.getString(R.string.time_display_day_ago)}"
            }
            else -> {
                val millisecond = TimeUnit.MICROSECONDS.toMillis(microsecond)
                MMdd.format(Date(millisecond))
            }
        }
    }

//
//    enum class Span {
//        Just, // 刚刚
//        Min,  // x分钟前
//        Hour, // x小时前
//        Day,  // x天前 最大7
//        Date  // 显示具体日期 yyyy-MM-dd
//    }

}