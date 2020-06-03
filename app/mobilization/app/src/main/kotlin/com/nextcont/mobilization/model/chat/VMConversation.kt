package com.nextcont.mobilization.model.chat

import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.store.entity.ENConversation
import com.nextcont.mobilization.store.entity.ENMessage
import io.realm.Realm
import io.realm.RealmList
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class VMConversation {

    /**
     * 本地id
     */
    var localId: String = UUID.randomUUID().toString()

    /**
     * 服务器id
     */
    var sid: String = ""
    /**
     * 显示名称，1对1为对方名称，群组为群组名称，空则使用群内人名拼接
     */
    var name: String = ""
    /**
     * 1对1聊天或者群组聊天
     */
    var chatType: ChatType = ChatType.Personal
    /**
     * 群组成员
     */
    var participants: List<VMContact> = emptyList()

    var extraInfo = VMConversationExtra()

    /**
     * 1对1或群组
     */
    enum class ChatType(var code: Int) {

        Personal(0),
        Group(1);

        companion object {

            fun form(code: Int): ChatType {
                ChatType.values().forEach {
                    if (it.code == code) {
                        return it
                    }
                }
                return Personal
            }
        }
    }

    /**
     * 1对1聊天获取聊天对象
     */
    fun findTarget(): VMContact? {
        return when (chatType) {
            ChatType.Group -> {
                null
            }
            ChatType.Personal -> {
                val selfId = User.load()?.id
                participants.firstOrNull {
                    selfId != it.sid
                }
            }
        }
    }

    /**
     * 处理显示名称及头像
     */
    fun makeDisplayNameAndAvatar() {
        val unknown = MobApp.Shared.getString(R.string.contact_name_unknown)
        // 处理显示名称、头像
        when (chatType) {
            ChatType.Group -> {
                extraInfo.displayName = if (name.isEmpty()) {
                    val names = mutableListOf<String>()
                    participants.forEach {
                        names.add(it.nickname)
                    }
                    if (names.isEmpty()) unknown else names.joinToString(",")
                } else {
                    name
                }
            }
            ChatType.Personal -> {
                findTarget()?.let {
                    extraInfo.displayName = it.displayName
                    extraInfo.thumbnail = "http://img3.imgtn.bdimg.com/it/u=3140403455,2984550794&fm=26&gp=0.jpg"
                } ?: kotlin.run {
                    extraInfo.displayName = unknown
                }
            }
        }
    }

    companion object {

        /**
         * 记录每一个会话最后消息的时间，用于计算显示时间
         */
        val latestMessageTime = ConcurrentHashMap<String, Long>()

        const val EXIT_GROUP_ERROR = "Former member"

        fun create(sid: String): VMConversation? {
            if (sid.isEmpty()) {
                return null
            }
            val conversation = VMConversation()
            conversation.localId = UUID.randomUUID().toString()
            conversation.sid = sid
            return conversation
        }
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

        /**
         * 保存、更新消息至DB
         */
        fun saveOrUpdate(realm: Realm, conversations: List<VMConversation>) {
            val entities = mutableListOf<ENConversation>()
            for (c in conversations) {
                val participantsList = RealmList<String>()
                c.participants.forEach { p ->
                    val contact = VMContact.findBySid(realm, p.sid)
                    if (contact == null) {
                        VMContact.create(p.sid)?.saveOrUpdate(realm)
                    }
                    participantsList.add(p.sid)
                }
                val query = realm.where(ENConversation::class.java)
                query.equalTo("sid", c.sid)
                var en = query.findFirst()
                if (en == null) {
                    en = ENConversation()
                    en.id = c.localId
                    en.sid = c.sid
                }
                en.name = c.name
                en.chatType = c.chatType.code
                en.participants = participantsList
                entities.add(en)
            }
            if (entities.isNotEmpty()) {
                realm.insertOrUpdate(entities)
            }
        }

        fun findBySid(realm: Realm, sid: String): VMConversation? {
            findEntityBySid(realm, sid)?.let {
                return convert(realm, it)
            }
            return null
        }

        private fun findEntityBySid(realm: Realm, sid: String): ENConversation? {
            val query = realm.where(ENConversation::class.java)
            query.equalTo("sid", sid)
            query.findFirst()?.let {
                return it
            }
            return null
        }

        /**
         * 会话列表
         */
        fun findAll(realm: Realm): List<VMConversation> {
            val conversations = mutableListOf<VMConversation>()
            val query = realm.where(ENConversation::class.java)
            val result = query.distinct("sid").findAll()
            result.forEach { en ->
                val c = convert(realm, en)
                // 根据最后的消息显示时间、内容
                c?.let {

                    // 没有任何消息，不显示
                    VMMessage.findLatest(realm, it.sid)?.let { m ->
                        c.extraInfo.snippet = m.contentSnippet
                        c.extraInfo.activeTime = m.time
                        c.extraInfo.hasUnreadMessage = VMMessage.countUnread(realm, it.sid) > 0
                        latestMessageTime[c.sid] = m.time
                        conversations.add(it)
                    }
                }
            }
            conversations.sortWith(Comparator { c1, c2 -> if (c1.extraInfo.activeTime > c2.extraInfo.activeTime) -1 else 1 })
            return conversations
        }

        private fun convert(realm: Realm, en: ENConversation): VMConversation? {
            val c = VMConversation()
            c.localId = en.id
            c.sid = en.sid
            c.name = en.name
            c.chatType = ChatType.form(en.chatType)
            val contacts = mutableListOf<VMContact>()
            en.participants.forEach { contactSid ->
                VMContact.findBySid(realm, contactSid)?.let { contact ->
                    contacts.add(contact)
                }
            }
            c.participants = contacts
            c.extraInfo = VMConversationExtra()
            // 处理显示名称、头像
            c.makeDisplayNameAndAvatar()
            return c
        }

        fun delete(realm: Realm, conversationId: String) {
            realm.where(ENConversation::class.java).equalTo("sid", conversationId).findFirst()?.deleteFromRealm()
            // 删除所有消息
            val query = realm.where(ENMessage::class.java)
            query.equalTo("conversationId", conversationId)
            val messages = query.findAll()
            messages.forEach {
                VMMessageContent.delete(realm, it.contentType, it.id)
            }
            messages.deleteAllFromRealm()
        }

        fun resetMessageState(realm: Realm) {
            val query = realm.where(ENConversation::class.java)
            query.findAll().forEach { conversation ->
                VMMessage.resetSendStateInConversation(realm, conversation.sid)
            }
        }

    }

    fun saveOrUpdate(realm: Realm) {
        val conversations = listOf(this)
        return saveOrUpdate(realm, conversations)
    }

}
