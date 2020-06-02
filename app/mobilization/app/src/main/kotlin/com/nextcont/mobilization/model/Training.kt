package com.nextcont.mobilization.model

import androidx.annotation.DrawableRes
import com.nextcont.mobilization.R
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Training(
    val title: String,
    val time: String,
    val location: String,
    val remark: String,
    @DrawableRes val icon: Int
) {

    companion object {

        fun daily(): List<Training> {
            return listOf(
                Training("5公里跑步", "上午5:00至10:00间", "上海市民兵训练中心", "10人完成，2人进行中", R.mipmap.ic_trans_run)
            )
        }

        fun addition(): List<Training> {
            return listOf(
                Training("力量训练", "2020年6月", "任意地点", "3人完成，0人进行中", R.mipmap.ic_trans_fight)
            )
        }

    }

}