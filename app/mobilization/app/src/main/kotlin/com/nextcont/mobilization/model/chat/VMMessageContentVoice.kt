package com.nextcont.mobilization.model.chat

import com.nextcont.mobilization.store.entity.ENVoiceContent
import io.realm.Realm
import kotlin.math.max

class VMMessageContentVoice: VMMessageContent() {

    override val type = Type.Voice

    /**
     * 时长 秒 最大60秒
     */
    var duration: Int = 0
    /**
     * 服务器code，包含了语音信息
     */
    var code: String = ""
    /**
     * 文件下载地址
     */
    var downloadUrl: String = ""
    /**
     * 是否已播放
     */
    var played = false
    /**
     * 本地文件地址
     */
    var localPath: String = ""
    /**
     * 临时变量，不入库，用于控制语音是否正在下载或上传
     */
    var loading = false
    /**
     * 临时变量，不入库，用于控制语音是否正在下载或上传
     */
    var shareLink: String = ""

    val displayDuration: String
        get() {
            return "${max(1, duration)}\""
        }

    val durationPlaceholder: String
        get() {
            val buffer = StringBuffer(" ")
            for (number in 0..duration step 2) {
                buffer.append("  ")
            }
            return buffer.toString()
        }

    companion object {

        fun create(targetId: String): VMMessageContentVoice {
            val content = VMMessageContentVoice()
            content.targetId = targetId
            return content
        }

        fun find(realm: Realm, messageId: String): VMMessageContentVoice? {
            val query = realm.where(ENVoiceContent::class.java)
            query.equalTo("targetId", messageId)
            query.findFirst()?.let {
                val voice = VMMessageContentVoice()
                voice.targetId = it.targetId
                voice.code = it.code
                voice.played = it.played
                voice.downloadUrl = it.downloadUrl
                voice.duration = it.duration
                voice.localPath = it.localPath
                voice.shareLink = it.shareLink
                return voice
            }
            return null
        }
    }

    fun saveOrUpdate(realm: Realm) {
        val query = realm.where(ENVoiceContent::class.java)
        query.equalTo("targetId", targetId)
        var en = query.findFirst()
        if (en == null) {
            en = ENVoiceContent()
            en.targetId = targetId
        }
        en.code = code
        en.played = played
        en.downloadUrl = downloadUrl
        en.duration = duration
        en.localPath = localPath
        en.shareLink = shareLink
        realm.insertOrUpdate(en)
    }

}