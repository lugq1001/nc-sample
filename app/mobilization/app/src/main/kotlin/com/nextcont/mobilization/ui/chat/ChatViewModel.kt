package com.nextcont.mobilization.ui.chat

import com.blankj.utilcode.util.FileUtils
import com.nextcont.mobilization.model.chat.*
import com.nextcont.mobilization.service.ImageProvider
import com.nextcont.mobilization.service.PathProvider
import com.nextcont.mobilization.ui.chat.ChatActivity.Companion.updateMonitor
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

internal class ChatViewModel {

    var page = 0
    private var pageSize = 20

    private var activity: WeakReference<ChatActivity>? = null
    private lateinit var conversation: VMConversation

    fun bind(activity: ChatActivity, conversation: VMConversation) {
        this.activity = WeakReference(activity)
        this.conversation = conversation

        loadData()
    }

    fun destroy() {
        activity = null
    }

    private fun loadData() {

    }

    fun loadMessages() {
        if (page == Integer.MAX_VALUE) {
            return
        }
        val messages = VMConversation.messages[conversation.sid] ?: mutableListOf()
        if (messages.isEmpty() || messages.count() < pageSize) {
            page = Integer.MAX_VALUE
        }
        activity?.get()?.onLoadMessages(messages)
    }

    /**
     * 加载更早的消息
     */
    fun loadMoreMessages() {
//        if (page == Integer.MAX_VALUE) {
//            return
//        }
//        page++
//        Store.openRealm.executeTransactionAsync {
//            val messages = VMMessage.findAll(it, conversationId, page, pageSize).toMutableList()
//            if (messages.isEmpty() || messages.count() < pageSize) {
//                page = Integer.MAX_VALUE
//            }
//            activity?.get()?.onLoadMoreMessages(messages)
//        }
    }

    fun readMessages(messages: List<VMMessage>) {
//        val ids = mutableSetOf<String>()
//        messages.forEach {
//            ids.add(it.sid)
//        }
//        // 将所有消息设为已读
//        Store.openRealm.executeTransactionAsync {
//            VMMessage.allRead(it, ids)
//            // TODO 发送已读消息
//            //ChatSession.send(MsgMarkEventObserved0(conversationId, ids))
//        }
    }


    fun sendText(text: String) {
        val localId = UUID.randomUUID().toString()
        val sender = VMContact.self
        val textContent = VMMessageContentText.create(localId)
        textContent.plainText = text
        val message = VMMessage.createLocalMessage(localId, sender, textContent)
        message.sendStatus = VMMessage.SendState.Success
        message.read = true
        makeDisplayTime(message)

        refreshLocalData(message)

        activity?.get()?.showSendMessage(message)
        send(message)
        updateMonitor.onNext(Unit)
    }

    fun sendImage(path: String) {
        val view = activity?.get() ?: return
        ImageProvider.compression(view, path, PathProvider.tempFolder(PathProvider.FileType.Image), completion = { resultPath ->
            val ext = VMMessageContentImage.getExtension(resultPath)
            val finalPath = "${PathProvider.localFolder(PathProvider.FileType.Image)}${File.separator}${UUID.randomUUID()}.$ext"
            FileUtils.move(resultPath, finalPath)

            val sender = VMContact.self
            val localId = UUID.randomUUID().toString()
            val imageContent = VMMessageContentImage.create(localId)
            imageContent.localPath = finalPath
            val message = VMMessage.createLocalMessage(localId, sender, imageContent)
            message.sendStatus = VMMessage.SendState.Success
            message.read = true
            makeDisplayTime(message)

            refreshLocalData(message)

            activity?.get()?.showSendMessage(message)

        })
    }

    // 发送语音
    fun send(voice: String, duration: Long) {
        val localPath = "${PathProvider.localFolder(PathProvider.FileType.Voice)}${File.separator}${UUID.randomUUID()}.amr"
        FileUtils.move(voice, localPath)
        val sender = VMContact.self
        val localId = UUID.randomUUID().toString()
        val voiceContent = VMMessageContentVoice.create(localId)
        voiceContent.duration = duration.toInt()
        voiceContent.localPath = localPath
        voiceContent.played = true
        val message = VMMessage.createLocalMessage(localId, sender, voiceContent)
        message.sendStatus = VMMessage.SendState.Success
        message.read = true
        makeDisplayTime(message)

        refreshLocalData(message)

        activity?.get()?.showSendMessage(message)

    }

    private fun refreshLocalData(message: VMMessage) {
        conversation.snippet = message.contentSnippet
        conversation.hasUnreadMessage = false
        if (VMConversation.messages[conversation.sid] == null) {
            val messages = mutableListOf(message)
            VMConversation.messages[conversation.sid] = messages
            VMConversation.conversions.add(0, conversation)
        } else {
            VMConversation.conversions.indexOfFirst {
                it.sid == conversation.sid
            }.let {
                if (it >= 0) {
                    VMConversation.conversions[it] = conversation
                }
            }
            VMConversation.messages[conversation.sid]?.add(message)
        }
    }

    private fun send(msg: VMMessage) {
        activity?.get()?.updateMessages(listOf(msg))
    }

    private fun makeDisplayTime(message: VMMessage) {
//        VMConversation.latestMessageTime[conversationId]?.let { time ->
//            if (message.time - time > VMMessage.displayTimeInterval) {
//                // 距上一条消息大于时间间隔，UI中需显示时间
//                message.displayTime = message.time
//            } else {
//                message.displayTime = 0
//            }
//        }
//        VMConversation.latestMessageTime[conversationId] = message.time
    }
}