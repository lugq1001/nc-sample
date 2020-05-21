package com.nextcont.sample.mobilization.handler

import com.nextcont.sample.mobilization.model.Account
import com.squareup.moshi.JsonClass
import io.javalin.http.Context
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 登录
 */
class LoginHandler: Handler() {

    private var logger: Logger = LoggerFactory.getLogger(LoginHandler::class.java)

    override fun response(ctx: Context): Response {

        val body = ctx.body()

        val params = json.adapter(Params::class.java).nonNull().fromJson(body) ?: return commonError("无效的数据")

        logger.info("用户登录: $params")

        Account.find(params.username)?.let { account ->
            if (account.password != params.password) {
                return commonError("密码错误")
            }
            return Response(0, "", json.adapter(Account::class.java).toJson(account))
        } ?: kotlin.run {
            return commonError("用户不存在")
        }
    }

    @JsonClass(generateAdapter = true)
    data class Params(
        val username: String,
        val password: String
    )

}