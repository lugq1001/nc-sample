package com.nextcont.mobilization.model.chat

import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.service.PathProvider
import com.squareup.moshi.JsonClass
import java.io.*

@JsonClass(generateAdapter = true)
class VMConversation {

    /**
     * 服务器id
     */
    var sid: String = ""
    /**
     * 显示名称，1对1为对方名称，群组为群组名称，空则使用群内人名拼接
     */
    var name: String = ""

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

    companion object {

        val conversions = mutableListOf<VMConversation>()
        val messages = mutableMapOf<String, MutableList<VMMessage>>()

        private const val CONVERSION_ID_GROUP = "CONVERSION_ID_GROUP"

        fun init() {
            // 系统通知
            val groupMessages = groupMessage()
            val group = VMConversation()
            group.sid = CONVERSION_ID_GROUP
            group.name = "民兵预备役-通信班-联络群"
            group.snippet = groupMessages.last().contentSnippet
            group.thumbnail = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1103476771,1752685729&fm=26&gp=0.jpg"
            group.activeTime = System.currentTimeMillis()
            group.hasUnreadMessage = true
            conversions.add(group)
            messages[CONVERSION_ID_GROUP] = groupMessages

            val contact = VMContact.testData(true)[1]
            val personal = VMConversation()
            personal.sid = contact.sid
            personal.name = contact.displayName
            personal.snippet = "[私密消息]"
            personal.thumbnail = contact.avatar
            personal.activeTime = System.currentTimeMillis() - 3600 * 3
            personal.hasUnreadMessage = true


            conversions.add(personal)





        }

        private fun groupMessage(): MutableList<VMMessage> {
            // 图片消息
            val msgImg = VMMessage()
            msgImg.sender = VMContact.testData(true)[3]
            msgImg.sendStatus = VMMessage.SendState.Success
            msgImg.read = false
            msgImg.displayTime = System.currentTimeMillis() - 3600 * 5
            val img = VMMessageContentImage()
            img.imageUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1591269459515&di=8db7fe77d7c4199df32caf68c638738c&imgtype=0&src=http%3A%2F%2Fwww.gx.xinhuanet.com%2Ftitlepic%2F1113163460_title0h.jpg"
            img.thumbnailUrl = img.imageUrl
            msgImg.content = img

            // 语音消息
            val msgVoice = VMMessage()
            msgVoice.sender = VMContact.testData(true)[3]
            msgVoice.sendStatus = VMMessage.SendState.Success
            msgVoice.read = false
            msgVoice.displayTime = System.currentTimeMillis() - 3600 * 3
            val voice = VMMessageContentVoice()
            voice.localPath = copyVoice()
            voice.duration = 15
            voice.played = true
            msgVoice.content = voice



            val msg1 = VMMessage()
            msg1.sender = VMContact.testData(true)[3]
            msg1.sendStatus = VMMessage.SendState.Success
            msg1.read = false
            msg1.displayTime = System.currentTimeMillis()
            val text1 = VMMessageContentText()
            text1.plainText = "下午召开民兵整组工作会议"
            msg1.content = text1

            val msg2 = VMMessage()
            msg2.sender = VMContact.testData(true)[3]
            msg2.sendStatus = VMMessage.SendState.Success
            msg2.read = false
            msg2.displayTime = 0L
            val text2 = VMMessageContentText()
            text2.plainText = "时间为下午两点整"
            msg2.content = text2

            val msg3 = VMMessage()
            msg3.sender = VMContact.testData(true)[3]
            msg3.sendStatus = VMMessage.SendState.Success
            msg3.read = false
            msg3.displayTime = 0L
            val text3 = VMMessageContentText()
            text3.plainText = "地点在大会议室"
            msg3.content = text3

            val msg4 = VMMessage()
            msg4.sender = VMContact.testData(true)[4]
            msg4.sendStatus = VMMessage.SendState.Success
            msg4.read = false
            msg4.displayTime = 0L
            val text4 = VMMessageContentText()
            text4.plainText = "收到"
            msg4.content = text4

            return mutableListOf(msgVoice, msgImg, msg1, msg2, msg3, msg4)
        }

        fun create(c: VMContact): VMConversation {
            val conversion = VMConversation()
            conversion.sid = c.sid
            conversion.name = c.nickname
            conversion.thumbnail = c.avatar
            conversion.activeTime = System.currentTimeMillis()
            conversion.hasUnreadMessage = false
            return conversion
        }

        private fun copyVoice(): String {
            val path = "${PathProvider.path()}${File.separator}test.amr"
            try {
                val stream: InputStream = MobApp.Shared.assets.open("gs-16b-1c-8000hz.amr")
                val output: OutputStream = BufferedOutputStream(FileOutputStream(path))
                val data = ByteArray(1024)
                var count: Int
                while (stream.read(data).also { count = it } != -1) {
                    output.write(data, 0, count)
                }
                output.flush()
                output.close()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return path
        }

    }


    /**
     * 1对1聊天获取聊天对象
     */
//    fun findTarget(): VMContact? {
//        return when (chatType) {
//            ChatType.Group -> {
//                null
//            }
//            ChatType.Personal -> {
//                val selfId = User.load()?.id
//                participants.firstOrNull {
//                    selfId != it.sid
//                }
//            }
//        }
//    }

//    /**
//     * 处理显示名称及头像
//     */
//    fun makeDisplayNameAndAvatar() {
//        val unknown = MobApp.Shared.getString(R.string.contact_name_unknown)
//        // 处理显示名称、头像
//        when (chatType) {
//            ChatType.Group -> {
//                extraInfo.displayName = if (name.isEmpty()) {
//                    val names = mutableListOf<String>()
//                    participants.forEach {
//                        names.add(it.nickname)
//                    }
//                    if (names.isEmpty()) unknown else names.joinToString(",")
//                } else {
//                    name
//                }
//            }
//            ChatType.Personal -> {
//                findTarget()?.let {
//                    extraInfo.displayName = it.displayName
//                    extraInfo.thumbnail = "http://img3.imgtn.bdimg.com/it/u=3140403455,2984550794&fm=26&gp=0.jpg"
//                } ?: kotlin.run {
//                    extraInfo.displayName = unknown
//                }
//            }
//        }
//    }

   // companion object {

//        /**
//         * 记录每一个会话最后消息的时间，用于计算显示时间
//         */
//        val latestMessageTime = ConcurrentHashMap<String, Long>()
//
//        const val EXIT_GROUP_ERROR = "Former member"

//        fun create(sid: String): VMConversation? {
//            if (sid.isEmpty()) {
//                return null
//            }
//            val conversation = VMConversation()
//            conversation.localId = UUID.randomUUID().toString()
//            conversation.sid = sid
//            return conversation
//        }
//
//        fun create(c: NCChatProtocol.NctConversation): VMConversation? {
//            if (c.id.isEmpty()) {
//                return null
//            }
//            if (c.leaveTimestamp > 0) {
//                return null
//            }
//            val conversation = VMConversation()
//            conversation.localId = UUID.randomUUID().toString()
//            conversation.sid = c.id
//            conversation.name = c.name
//            val participantList = c.participantDataList
//            val participants = mutableListOf<VMContact>()
//            for (participantData in participantList) {
//                val participant = VMContact.create(participantData.participantId)
//                        ?: return null // 联系人sid为空， 说明已被删除 丢弃该数据
//                participant.nickname = participantData.fallbackName
//                participants.add(participant)
//            }
//            conversation.participants = participants
//            var chatType = ChatType.Personal
//            c.type?.let {
//                chatType = when (it) {
//                    NCChatProtocol.NctConversationType.NCT_CONVERSATION_UNKNOWN -> {
//                        return null
//                    }
//                    NCChatProtocol.NctConversationType.NCT_CONVERSATION_GROUP -> ChatType.Group
//                    NCChatProtocol.NctConversationType.NCT_CONVERSATION_ONE_TO_ONE -> ChatType.Personal
//                }
//            }
//            conversation.chatType = chatType
//
//            var type = Type.Chat
//            // 邀请
//            participantList.forEach { participantData ->
//                if (participantData.participantId == VMAccountInfo.load().sid && participantData.invitationStatus === NCChatProtocol.NctInvitationStatus.NCTINVITATION_STATUS_PENDING) {
//                    type = Type.Invitation
//                }
//            }
//            conversation.type = type
//
//            c.view?.let { view ->
//                when (view) {
//                    NCChatProtocol.NctConversationView.NCT_CONVERSATIONVIEW_ARCHIVED -> {
//                        conversation.viewType = ViewType.Archive
//                    }
//                    else -> {
//                        conversation.viewType = ViewType.Inbox
//                    }
//                }
//            }
//            return conversation
//        }

//        /**
//         * 保存、更新消息至DB
//         */
//        fun saveOrUpdate(realm: Realm, conversations: List<VMConversation>) {
//            val entities = mutableListOf<ENConversation>()
//            for (c in conversations) {
//                val participantsList = RealmList<String>()
//                c.participants.forEach { p ->
//                    val contact = VMContact.findBySid(realm, p.sid)
//                    if (contact == null) {
//                        VMContact.create(p.sid)?.saveOrUpdate(realm)
//                    }
//                    participantsList.add(p.sid)
//                }
//                val query = realm.where(ENConversation::class.java)
//                query.equalTo("sid", c.sid)
//                var en = query.findFirst()
//                if (en == null) {
//                    en = ENConversation()
//                    en.id = c.localId
//                    en.sid = c.sid
//                }
//                en.name = c.name
//                en.chatType = c.chatType.code
//                en.participants = participantsList
//                entities.add(en)
//            }
//            if (entities.isNotEmpty()) {
//                realm.insertOrUpdate(entities)
//            }
//        }

//        fun findBySid(realm: Realm, sid: String): VMConversation? {
//            findEntityBySid(realm, sid)?.let {
//                return convert(realm, it)
//            }
//            return null
//        }

//        private fun findEntityBySid(realm: Realm, sid: String): ENConversation? {
//            val query = realm.where(ENConversation::class.java)
//            query.equalTo("sid", sid)
//            query.findFirst()?.let {
//                return it
//            }
//            return null
//        }
//
//        /**
//         * 会话列表
//         */
//        fun findAll(realm: Realm): List<VMConversation> {
//            val conversations = mutableListOf<VMConversation>()
//            val query = realm.where(ENConversation::class.java)
//            val result = query.distinct("sid").findAll()
//            result.forEach { en ->
//                val c = convert(realm, en)
//                // 根据最后的消息显示时间、内容
//                c?.let {
//
//                    // 没有任何消息，不显示
//                    VMMessage.findLatest(realm, it.sid)?.let { m ->
//                        c.extraInfo.snippet = m.contentSnippet
//                        c.extraInfo.activeTime = m.time
//                        c.extraInfo.hasUnreadMessage = VMMessage.countUnread(realm, it.sid) > 0
//                        latestMessageTime[c.sid] = m.time
//                        conversations.add(it)
//                    }
//                }
//            }
//            conversations.sortWith(Comparator { c1, c2 -> if (c1.extraInfo.activeTime > c2.extraInfo.activeTime) -1 else 1 })
//            return conversations
//        }
//
//        private fun convert(realm: Realm, en: ENConversation): VMConversation? {
//            val c = VMConversation()
//            c.localId = en.id
//            c.sid = en.sid
//            c.name = en.name
//            c.chatType = ChatType.form(en.chatType)
//            val contacts = mutableListOf<VMContact>()
//            en.participants.forEach { contactSid ->
//                VMContact.findBySid(realm, contactSid)?.let { contact ->
//                    contacts.add(contact)
//                }
//            }
//            c.participants = contacts
//            c.extraInfo = VMConversationExtra()
//            // 处理显示名称、头像
//            c.makeDisplayNameAndAvatar()
//            return c
//        }
//
//        fun delete(realm: Realm, conversationId: String) {
//            realm.where(ENConversation::class.java).equalTo("sid", conversationId).findFirst()?.deleteFromRealm()
//            // 删除所有消息
//            val query = realm.where(ENMessage::class.java)
//            query.equalTo("conversationId", conversationId)
//            val messages = query.findAll()
//            messages.forEach {
//                VMMessageContent.delete(realm, it.contentType, it.id)
//            }
//            messages.deleteAllFromRealm()
//        }
//
//        fun resetMessageState(realm: Realm) {
//            val query = realm.where(ENConversation::class.java)
//            query.findAll().forEach { conversation ->
//                VMMessage.resetSendStateInConversation(realm, conversation.sid)
//            }
//        }

 //   }

//    fun saveOrUpdate(realm: Realm) {
//        val conversations = listOf(this)
//        return saveOrUpdate(realm, conversations)
//    }

}
