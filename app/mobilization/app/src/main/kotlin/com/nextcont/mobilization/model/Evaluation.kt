package com.nextcont.mobilization.model

data class Evaluation(
    val id: Int,
    val no: String,
    val title: String,
    val name: String,
    val age: Int,
    var score: String,
    val gender: Int,
    val createAt: String

) {

    val genderString: String
    get() = if(gender == 0) "男" else "女"

}