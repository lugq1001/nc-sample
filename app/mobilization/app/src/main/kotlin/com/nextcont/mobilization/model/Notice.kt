package com.nextcont.mobilization.model

data class Notice(
        val title: String,
        val content: String,
        val time: String
) {

    companion object {

        fun data(): List<Notice> {
            return listOf(
                    Notice("组织召开国防教育教研室会议", "会议时间：2019年11月5日10:20\n会议地点：国防教育教研室会议室（电子队）", "2020-06-03"),
                    Notice("5月补助已发放", "共计金额:753元整", "2020-06-01"),
                    Notice("4月补助已发放", "共计金额:532元整", "2020-05-01"),
                    Notice("3月补助已发放", "共计金额:264元整", "2020-04-01"),
                    Notice("2月补助已发放", "共计金额:175元整", "2020-03-01"),
                    Notice("1月补助已发放", "共计金额:323元整", "2020-02-01"),
                    Notice("12月补助已发放", "共计金额:486元整", "2020-01-01")
            )
        }

    }
}