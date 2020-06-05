package com.nextcont.mobilization.model.chat

import android.annotation.SuppressLint
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.store.entity.ENMessage
import com.squareup.moshi.JsonClass
import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
class VMMessage: MultiItemEntity {

    /**
     * 本地标记id
     */
    var localId = UUID.randomUUID().toString()

    /**
     * 本地标记id
     */
    var sid: String = UUID.randomUUID().toString()

    /**
     * 发送人
     */
    lateinit var sender: VMContact

    /**
     * 消息内容
     */
    lateinit var content: VMMessageContent

    var sendStatus: SendState = SendState.Success

    var read: Boolean = true

    /*时戳*/
    var time: Long = 0 // google数据以微秒为单位

    var burn = false

    /**
     * 显示时间,入库,用于ui显示
     */
    var displayTime: Long = 0L

    val formatTime: String
        get() {
            if (displayTime == 0L) {
                return ""
            }
            val date = Date(displayTime / 1000)
            return format.format(date)
        }

    val contentSnippet: String
        get() {
            if (burn) {
                return "[私密消息, 阅后即焚]"
            } else {
                return when (content.type) {
                    VMMessageContent.Type.Text -> {
                        (content as VMMessageContentText).plainText
                    }
                    VMMessageContent.Type.Image -> {
                        MobApp.Shared.getString(R.string.message_media_type_image)
                    }
                    VMMessageContent.Type.Video -> {
                        MobApp.Shared.getString(R.string.message_media_type_video)
                    }
                    VMMessageContent.Type.Voice -> {
                        MobApp.Shared.getString(R.string.message_media_type_voice)
                    }
                }
            }

        }


    /**
     * 消息状态
     */
    enum class SendState(val code: Int) {

        Processing(0),
        Success(1),
        Failure(2);

        companion object {

            fun form(code: Int): SendState {
                SendState.values().forEach {
                    if (it.code == code) {
                        return it
                    }
                }
                return Success
            }
        }
    }

    override val itemType: Int
        get() = if (VMContact.self.sid == sender.sid) 0 else 1

    companion object {

        const val displayTimeInterval = 5 * 60 * 1000 * 1000

        @SuppressLint("ConstantLocale")
        private val format = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

        fun createLocalMessage(localId: String, sender: VMContact, content: VMMessageContent): VMMessage {
            val message = VMMessage()
            message.localId = localId
            message.sid = UUID.randomUUID().toString()
            message.sender = sender
            message.content = content
            message.time = System.currentTimeMillis() * 1000
            message.read = true
            return message
        }

//        fun createLocalMessage(localId: String, conversation: VMConversation, sender: VMContact, content: VMMessageContent): VMMessage {
//            val message = VMMessage()
//            message.localId = localId
//            message.sendLocalId = UUID.randomUUID().toString()
//            message.sid = message.sendLocalId
//            message.sender = sender
//            message.conversation = conversation
//            message.content = content
//            message.time = System.currentTimeMillis() * 1000
//            message.read = true
//            return message
//        }

//        fun create(conversationId: String, event: NCChatProtocol.NctEvent): VMMessage? {
//            if (event.id.isEmpty()) {
//                return null
//            }
//            val localId = UUID.randomUUID().toString()
//            val message = VMMessage()
//            message.localId = localId
//            message.sid = event.id
//            message.time = event.timestamp
//            message.sendStatus = SendState.Success
//
//            val eventContent = event.content
//
//            val attachment = eventContent.attachment
//            val textContent = eventContent.textContentList
//
//            if (textContent.isEmpty()) {
//                // 图片 视频消息
//                when (attachment.attachmentType) {
//                    NCChatProtocol.NctAttachmentType.ATTACHMENT_TYPE_PHOTO, NCChatProtocol.NctAttachmentType.ATTACHMENT_TYPE_ANIMATED_PHOTO -> {
//                        val content = VMMessageContentImage.create(localId)
//                        content.imageUrl = attachment.photoUrl
//                        content.thumbnailUrl = attachment.photoUrlThumbnail
//                        message.content = content
//                    }
//                    NCChatProtocol.NctAttachmentType.ATTACHMENT_TYPE_VIDEO -> {
//                        val content = VMMessageContentVideo.create(localId)
//                        content.downloadUrl = attachment.downloadUrl
//                        content.imageUrl = attachment.photoUrl
//                        content.thumbnailUrl = attachment.photoUrlThumbnail
//                        message.content = content
//                    }
//                    else -> {
//                        return null
//                    }
//                }
//            } else {
//                val buffer = StringBuffer()
//                textContent.forEach {
//                    when (it.textContentType) {
//                        NCChatProtocol.NctTextContentType.TEXT_CONTENT_TYPE_LINE_BREAK -> {
//                            buffer.append("\n")
//                        }
//                        NCChatProtocol.NctTextContentType.TEXT_CONTENT_TYPE_LINK -> {
//                            buffer.append(" ${it.text} ")
//                        }
//                        else -> {
//                            buffer.append(it.text)
//                        }
//                    }
//                }
//                VMMessageContentVoice.convert(localId, buffer.toString(), conversationId, event.senderId)?.let {
//                    // 语音消息
//                    message.content = it
//                    return message
//                }
//                if (eventContent.hasAttachment()) {
//                    // web端 图文连发处理
//                    buffer.append(" ${attachment.photoUrl} ")
//                }
//                val content = VMMessageContentText.create(localId)
//                content.plainText = buffer.toString()
//                message.content = content
//            }
//            return message
//        }
//
//        /**
//         * 保存、更新消息至DB
//         */
//        fun saveOrUpdate(realm: Realm, messages: List<VMMessage>) {
//            val entities = mutableListOf<RealmObject>()
//            for (m in messages) {
//                val query = realm.where(ENMessage::class.java)
//                query.equalTo("sid", m.sid)
//                var en = query.findFirst()
//                if (en == null) {
//                    en = ENMessage()
//                    en.id = m.localId
//                    en.senderId = m.sender.sid
//                    en.sendLocalId = m.sendLocalId
//                    en.conversationId = m.conversation.sid
//                    en.contentType = m.content.type.code
//                    en.displayTime = m.displayTime
//                }
//                when (m.content.type) {
//                    VMMessageContent.Type.Text -> {
//                        val text = m.content as VMMessageContentText
//                        text.saveOrUpdate(realm)
//                    }
//                    VMMessageContent.Type.Image -> {
//                        val image = m.content as VMMessageContentImage
//                        image.saveOrUpdate(realm)
//                    }
//                    VMMessageContent.Type.Video -> {
//                        val video = m.content as VMMessageContentVideo
//                        video.saveOrUpdate(realm)
//                    }
//                    VMMessageContent.Type.Voice -> {
//                        val voice = m.content as VMMessageContentVoice
//                        voice.saveOrUpdate(realm)
//                    }
//                }
//                en.sid = m.sid //本地发送的sid发送成功会变，需更新
//                en.time = m.time
//                en.sendStatus = m.sendStatus.code
//                en.read = m.read
//                entities.add(en)
//            }
//            if (entities.isNotEmpty()) {
//                realm.insertOrUpdate(entities)
//            }
//        }
//
//        fun findBySid(realm: Realm, sid: String): VMMessage? {
//            val query = realm.where(ENMessage::class.java)
//            query.equalTo("sid", sid)
//            query.findFirst()?.let {
//                return convert(realm, it)
//            }
//            return null
//        }
//
//        /**
//         * 消息列表
//         */
//        fun findAll(realm: Realm, conversationId: String, page: Int, pageSize: Int = 20): List<VMMessage> {
//            val messages = mutableListOf<VMMessage>()
//            val query = realm.where(ENMessage::class.java)
//            query.equalTo("conversationId", conversationId).sort("time", Sort.DESCENDING).distinct("sid")
//            val result = query.findAll()
//            if (result.isEmpty()) {
//                return messages
//            }
//            val start = page * pageSize
//            val end = result.size.coerceAtMost(start + pageSize)
//            loop@ for (i in start until end) {
//                val en = result[i] ?: continue
//                val m = convert(realm, en) ?: continue
//                messages.add(m)
//            }
//            return messages.reversed()
//        }
//
//        fun countUnread(realm: Realm, conversationId: String): Long {
//            val query = realm.where(ENMessage::class.java)
//            query.equalTo("conversationId", conversationId)
//            query.equalTo("read", false)
//            return query.count()
//        }
//
//        fun allRead(realm: Realm, conversationId: String): Set<String> {
//            val ids = mutableSetOf<String>()
//            val query = realm.where(ENMessage::class.java)
//            query.equalTo("conversationId", conversationId)
//            query.equalTo("read", false)
//            val result = query.findAll()
//            result.forEach {
//                it.read = true
//                ids.add(it.sid)
//            }
//            realm.insertOrUpdate(result)
//            return ids
//        }
//
//        fun allRead(realm: Realm, messageIds: Set<String>) {
//            messageIds.forEach { id ->
//                val query = realm.where(ENMessage::class.java)
//                query.equalTo("sid", id)
//                val result = query.findFirst()
//                result?.let {
//                    it.read = true
//                    realm.insertOrUpdate(it)
//                }
//            }
//        }
//
//        private fun convert(realm: Realm, en: ENMessage): VMMessage? {
//            val conversation = VMConversation.findBySid(realm, en.conversationId) ?: return null
//            val sender = VMContact.findBySid(realm, en.senderId) ?: return null
//            val m = VMMessage()
//            m.localId = en.id
//            m.sid = en.sid
//            m.displayTime = en.displayTime
//            m.sendStatus = SendState.form(en.sendStatus)
//            m.read = en.read
//            m.time = en.time
//            m.sendLocalId = en.sendLocalId
//            val content = when (VMMessageContent.Type.from(en.contentType)) {
//                VMMessageContent.Type.Text -> {
//                    VMMessageContentText.find(realm, m.localId) ?: return null
//                }
//                VMMessageContent.Type.Image -> {
//                    VMMessageContentImage.find(realm, m.localId) ?: return null
//                }
//                VMMessageContent.Type.Video -> {
//                    VMMessageContentVideo.find(realm, m.localId) ?: return null
//                }
//                VMMessageContent.Type.Voice -> {
//                    VMMessageContentVoice.find(realm, m.localId) ?: return null
//                }
//            }
//            m.content = content
//            m.conversation = conversation
//            m.sender = sender
//            return m
//        }
//
//        fun findLatest(realm: Realm, conversationId: String): VMMessage? {
//            val query = realm.where(ENMessage::class.java)
//            query.equalTo("conversationId", conversationId).sort("time", Sort.DESCENDING)
//            query.findFirst()?.let {
//                convert(realm, it)?.let { result ->
//                    return result
//                }
//            }
//            return null
//        }
//
//        fun resetSendStateInConversation(realm: Realm, conversationId: String) {
//            val query = realm.where(ENMessage::class.java)
//            query.equalTo("conversationId", conversationId)
//            query.equalTo("sendStatus", SendState.Processing.code)
//            val result = query.findAll()
//            result.forEach {
//                it.sendStatus = SendState.Failure.code
//            }
//            realm.insertOrUpdate(result)
//        }

    }

//    fun saveOrUpdate(realm: Realm) {
//        return saveOrUpdate(realm, listOf(this))
//    }




}