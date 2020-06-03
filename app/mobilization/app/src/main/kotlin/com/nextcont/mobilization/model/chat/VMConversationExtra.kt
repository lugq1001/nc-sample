package com.nextcont.mobilization.model.chat

/**
 * 会话额外信息，用于本地功能，不与服务端同步
 */
class VMConversationExtra {

    /**
     * 列表显示最近会话内容
     */
    var snippet: String = ""
    /**
     * 缩略图
     */
    var thumbnail: String = ""
    /**
     * 最后一条消息的时间 微秒
     */
    var activeTime: Long = 0
    /**
     * 是否有未读消息，显示红点用
     */
    var hasUnreadMessage: Boolean = false
    /**
     * 显示名称,不入库
     */
    var displayName: String = ""

}
