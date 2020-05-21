package com.nextcont.sample.mobilization.handler

import com.nextcont.sample.md5
import com.nextcont.sample.mobilization.model.Account
import com.squareup.moshi.JsonClass
import io.javalin.http.Context
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 注册
 */
class RegisterHandler: Handler() {

    private var logger: Logger = LoggerFactory.getLogger(RegisterHandler::class.java)

    override fun response(ctx: Context): Response {

        val body = ctx.body()

        val adapter = json.adapter(Params::class.java).nonNull()
        val params = adapter.fromJson(body) ?: return commonError("无效的数据")


        if (params.username.length < 6 || params.username.length > 16) {
            return commonError("请输入6-20位用户名")
        }
        if (params.password.length < 6 || params.password.length > 16) {
            return commonError("请输入6-12位密码")
        }
        if (params.idCard.length != 15 && params.idCard.length != 18) {
            return commonError("身份证不合法")
        }

        Account.find(params.username)?.let {
            return commonError("用户名已存在")
        }

        val account = Account()
        account.id = UUID.randomUUID().toString().md5
        account.username = params.username
        account.password = params.password
        account.fullName = params.fullName
        account.gender = Account.Gender.from(params.gender)
        account.age = params.age
        account.idCard = params.idCard
        account.role = Account.Role.from(params.role)
        logger.info("用户注册: $params")
        account.saveOrUpdate()
        return Response(0, "")
    }

    @JsonClass(generateAdapter = true)
    data class Params(
        val username: String,
        val password: String,
        val fullName: String,
        val gender: Int,
        val age: Int,
        val idCard: String,
        val role: Int
    )

}