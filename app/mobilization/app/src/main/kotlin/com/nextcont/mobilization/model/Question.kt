package com.nextcont.mobilization.model

data class Question(
    val title: String,
    val items: List<String>,
    val answer: Int
) {

    companion object {

        fun data(): List<Question> {
            return listOf(
                Question("国防法，于（ ）由八届人大五次会议通过，共12章70条。", listOf("1998年12月29日", "1997年3月14日", "2001年4月28日", "2002年4月1日"), 1),
                Question("兵役法规定，年满（ ）周岁的男性公民应当被征集服现役。 ", listOf("17", "18", "19", "20"), 1),
                Question("20世纪90年代，（ ）爆发标志着战争进入高技术局部战争的历史时期。", listOf("马岛战争", "两伊战争", "海湾战争", "伊拉克战争"), 2),
                Question("中国人民解放军是中国共产党缔造和领导的人民军队，是中国武装力量的主体，由现役部队和（ ）组成。", listOf("武装警察部队", "民兵", "第二炮兵", "预备役部队"), 3),
                Question("中国陆地边界线总长（ ）万千米，海岸线1.8万千米。", listOf("2.2", "960", "2.8", "1300"), 0)
            )
        }

    }

}