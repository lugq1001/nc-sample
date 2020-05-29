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
                Training("5公里跑步", "上午5:00至10:00间", "民兵训练中心", "10人完成，2人进行中", R.mipmap.ic_trans_run),
                Training("球类活动", "下午1:00至3：00间", "民兵训练中心", "10人完成，2人进行中", R.mipmap.ic_trans_ball),
                Training("负重骑行", "下午1:00至3：00间", "民兵训练中心", "10人完成，2人进行中", R.mipmap.ic_trans_biker)

            )
        }

        fun addition(): List<Training> {
            return listOf(
                Training("自由搏击", "2020年6月", "民兵训练中心", "10人完成，2人进行中", R.mipmap.ic_trans_fight),
                Training("射击训练", "2020年7月", "民兵训练中心", "10人完成，2人进行中", R.mipmap.ic_trans_shot),
                Training("力量训练", "2020年7月", "民兵训练中心", "10人完成，2人进行中", R.mipmap.ic_trans_force)
            )
        }

    }

}