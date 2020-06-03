package com.nextcont.mobilization.ui.chat

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.model.chat.*
import com.nextcont.mobilization.store.Store
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.realm.Realm
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

internal class ChatViewModel {

    var page = 0
    private var pageSize = 20
    private val disposableBag = CompositeDisposable()

    companion object {
        val messageSendBag = CompositeDisposable()
    }

    private var activity: WeakReference<ChatActivity>? = null
    private lateinit var conversationId: String

    fun bind(activity: ChatActivity, conversationId: String) {
        this.activity = WeakReference(activity)
        this.conversationId = conversationId

        // TODO消息推送
//        this.disposableBag.add(ChatSession.asyncSubject.subscribe { obj ->
//            onMessageReceive(obj)
//        })
        loadData()
    }

    fun destroy() {
        activity = null
    }

    fun loadData() {
        Store.openRealm.executeTransactionAsync {
            VMConversation.findBySid(it, conversationId)?.let { c ->
                this.activity?.get()?.updateView(c)
            }
        }
    }

    fun loadMessages() {
        if (page == Integer.MAX_VALUE) {
            return
        }
        Store.openRealm.executeTransactionAsync {
            val messages = VMMessage.findAll(it, conversationId, page, pageSize).toMutableList()
            if (messages.isEmpty() || messages.count() < pageSize) {
                page = Integer.MAX_VALUE
            }
            activity?.get()?.onLoadMessages(messages)
        }
    }

    /**
     * 加载更早的消息
     */
    fun loadMoreMessages() {
        if (page == Integer.MAX_VALUE) {
            return
        }
        page++
        Store.openRealm.executeTransactionAsync {
            val messages = VMMessage.findAll(it, conversationId, page, pageSize).toMutableList()
            if (messages.isEmpty() || messages.count() < pageSize) {
                page = Integer.MAX_VALUE
            }
            activity?.get()?.onLoadMoreMessages(messages)
        }
    }

    fun readMessages(messages: List<VMMessage>) {
        val ids = mutableSetOf<String>()
        messages.forEach {
            ids.add(it.sid)
        }
        // 将所有消息设为已读
        Store.openRealm.executeTransactionAsync {
            VMMessage.allRead(it, ids)
            // TODO 发送已读消息
            //ChatSession.send(MsgMarkEventObserved0(conversationId, ids))
        }
    }

    //==============================================
    // 播放语音
    //==============================================
    fun playVoice(message: VMMessage) {
        val scheduler = Schedulers.computation()
        val voice = message.content as? VMMessageContentVoice ?: return
        voice.loading = true
        disposableBag.add(setVoicePlayed(voice).subscribeOn(scheduler).flatMap { v ->
            message.content = v
            activity?.get()?.updateMessages(listOf(message))
            getVoicePath(voice)
        }.subscribe({ v ->
            v.loading = false
            message.content = v
            activity?.get()?.voicePlay(message)
        }, { throwable ->
            activity?.get()?.voiceLoadingFailed(throwable.localizedMessage)
        }))
    }

    private fun setVoicePlayed(voice: VMMessageContentVoice): Single<VMMessageContentVoice> {
        return Single.create { emitter ->
            if (voice.played) {
                emitter.onSuccess(voice)
                return@create
            }
            var rm: Realm? = null
            try {
                rm = Store.openRealm
                rm.executeTransaction {
                    voice.played = true
                    voice.saveOrUpdate(it)
                }
            } catch (e: Exception) {

            } finally {
                Store.closeRealm(rm)
            }
            emitter.onSuccess(voice)
        }
    }

    private fun getVoicePath(voice: VMMessageContentVoice): Single<VMMessageContentVoice> {
        if (voice.localPath.isNotEmpty()) {
            val file = File(voice.localPath)
            if (file.exists() && file.isFile) {
                return Single.just(voice)
            }
        }
        if (voice.downloadUrl.isNotEmpty()) {
            return voiceDownload(voice)
        }
        return getVoiceDownloadUrl(voice).flatMap { msg ->
            voiceDownload(msg)
        }
    }

    // TODO 获取语音下载地址
    private fun getVoiceDownloadUrl(voice: VMMessageContentVoice): Single<VMMessageContentVoice> {
        return Single.create { emitter ->
//            disposableBag.add(NCApi.render.chat.voiceInfo(VMAccountInfo.load().sid, voice.code).subscribeOn(Schedulers.computation()).subscribe({ obj ->
//                Logger.i("语音下载地址; ${obj.data.cdnUrl}")
//                Store.getRealm.executeTransaction { realm ->
//                    voice.downloadUrl = obj.data.cdnUrl
//                    voice.saveOrUpdate(realm)
//                    emitter.onSuccess(voice)
//                }
//            }, { throwable ->
//                emitter.onError(throwable)
//            }))
        }
    }

    // TODO 下载语音
    private fun voiceDownload(voice: VMMessageContentVoice): Single<VMMessageContentVoice> {
        return Single.create { emitter ->
//            val name = "${UUID.randomUUID()}.amr"
//            val tempPath = "${PathProvider.tempFolder(PathProvider.FileType.Voice)}${File.separator}$name"
//            val filePath = "${PathProvider.downloadFolder(PathProvider.FileType.Voice)}${File.separator}$name"
//            disposableBag.add(NCApi.render.download(voice.downloadUrl, tempPath).subscribe({
//                Logger.d("下载进度: ${it.percent}")
//            }, {
//                emitter.onError(it)
//            }, {
//                // 下载成功
//                if (!FileUtils.move(tempPath, filePath)) {
//                    emitter.onError(Throwable("Download Failed."))
//                    return@subscribe
//                }
//                var rm: Realm? = null
//                try {
//                    rm = Store.getRealm
//                    rm.executeTransaction {
//                        voice.localPath = filePath
//                        voice.saveOrUpdate(it)
//                    }
//                } catch (e: Exception) {
//
//                } finally {
//                    Store.closeRealm(rm)
//                    emitter.onSuccess(voice)
//                }
//
//            }))
        }
    }

    //==============================================
    // 发送文字消息
    //==============================================
    fun resend(message: VMMessage) {
        message.sendStatus = VMMessage.SendState.Processing
        Store.openRealm.executeTransactionAsync(Realm.Transaction { realm ->
            message.saveOrUpdate(realm)
            activity?.get()?.updateMessages(listOf(message))
        }, Realm.Transaction.OnSuccess {
            when (message.content.type) {
                VMMessageContent.Type.Text -> {
                    send(message)
                }
                VMMessageContent.Type.Voice -> {
                    messageSendBag.add(uploadVoice(message).subscribe({ message ->
                        send(message)
                    }, {
                        messageFailed(message)
                    }))
                }
                VMMessageContent.Type.Image -> {
                    val image = message.content as? VMMessageContentImage ?: return@OnSuccess
                    if (image.localPath.isEmpty() || !FileUtils.isFileExists(image.localPath)) {
                        val view = activity ?: return@OnSuccess
                        messageFailed(message)
                        ToastUtils.showShort(view.get()?.getString(R.string.activity_chat_image_not_exist))
                        return@OnSuccess
                    }
                    messageSendBag.add(uploadImage(message).subscribe({ message ->
                        send(message)
                    }, {
                        messageFailed(message)
                    }))
                }
                VMMessageContent.Type.Video -> {

                }
            }
        })
    }

    fun sendText(text: String) {
        var msg: VMMessage? = null
        Store.openRealm.executeTransactionAsync(Realm.Transaction { realm ->
            val localId = UUID.randomUUID().toString()
            val conversation = VMConversation.findBySid(realm, conversationId) ?: return@Transaction
            val sender = VMContact.create(User.load()!!)
            val textContent = VMMessageContentText.create(localId)
            textContent.plainText = text
            val message = VMMessage.createLocalMessage(localId, conversation, sender, textContent)
            message.sendStatus = VMMessage.SendState.Processing
            makeDisplayTime(message)
            message.saveOrUpdate(realm)
            activity?.get()?.showSendMessage(message)
            msg = message
        }, Realm.Transaction.OnSuccess {
            msg?.let {
                send(it)
            }
        })
    }


    // TODO 发送图片
    fun sendImage(path: String) {
        val view = activity?.get() ?: return
        var msg: VMMessage? = null
//        ImageProvider.compression(view, path, PathProvider.tempFolder(PathProvider.FileType.Image), completion = { resultPath ->
//            val ext = VMMessageContentImage.getExtension(resultPath)
//            val finalPath = "${PathProvider.localFolder(PathProvider.FileType.Image)}${File.separator}${UUID.randomUUID()}.$ext"
//            FileUtils.move(resultPath, finalPath)
//            Store.openRealm.executeTransactionAsync(Realm.Transaction { realm ->
//                val conversation = VMConversation.findBySid(realm, conversationId)
//                        ?: return@Transaction
//                val sender = VMContact.create(VMAccountInfo.load())
//                val localId = UUID.randomUUID().toString()
//                val imageContent = VMMessageContentImage.create(localId)
//                imageContent.localPath = finalPath
//                val message = VMMessage.createLocalMessage(localId, conversation, sender, imageContent)
//                message.sendStatus = VMMessage.SendState.Processing
//                makeDisplayTime(message)
//                message.saveOrUpdate(realm)
//                activity?.get()?.showSendMessage(message)
//                msg = message
//            }, Realm.Transaction.OnSuccess {
//                msg?.let { msg ->
//                    messageSendBag.add(uploadImage(msg).subscribe({ message ->
//                        send(message)
//                    }, {
//                        messageFailed(msg)
//                    }))
//                }
//            })
//        })
    }

    // TODO 上传图片
    private fun uploadImage(msg: VMMessage): Single<VMMessage> {
        val image = msg.content as? VMMessageContentImage ?: return Single.just(msg)
        return Single.create { emitter ->
//            val file = File(image.localPath)
//            messageSendBag.add(NCApi.render.chat.uploadImage(file).subscribe({ obj ->
//                if (obj.data.isEmpty()) {
//                    emitter.onError(Throwable("Data Error"))
//                    return@subscribe
//                }
//                val imageObj = obj.data.first()
//
//                var rm: Realm? = null
//                try {
//                    rm = Store.getRealm
//                    rm.executeTransaction {
//                        val url = imageObj.downloadUrl
//                        image.remoteUrl = url
//                        image.imageUrl = url
//                        image.thumbnailUrl = url
//                        image.saveOrUpdate(it)
//                        msg.content = image
//                    }
//                } catch (e: Exception) {
//
//                } finally {
//                    Store.closeRealm(rm)
//                }
//                emitter.onSuccess(msg)
//            }, { throwable ->
//                emitter.onError(throwable)
//            }))
        }
    }

    // 发送语音
    fun send(voice: String, duration: Long) {
//        var msg: VMMessage? = null
//        val localPath = "${PathProvider.localFolder(PathProvider.FileType.Voice)}${File.separator}${UUID.randomUUID()}.amr"
//        FileUtils.move(voice, localPath)
//        Store.openRealm.executeTransactionAsync(Realm.Transaction { realm ->
//            val conversation = VMConversation.findBySid(realm, conversationId) ?: return@Transaction
//            val sender = VMContact.create(VMAccountInfo.load())
//            val localId = UUID.randomUUID().toString()
//            val voiceContent = VMMessageContentVoice.create(localId)
//            voiceContent.duration = duration.toInt()
//            voiceContent.localPath = localPath
//            voiceContent.played = true
//            val message = VMMessage.createLocalMessage(localId, conversation, sender, voiceContent)
//            message.sendStatus = VMMessage.SendState.Processing
//            makeDisplayTime(message)
//            message.saveOrUpdate(realm)
//            activity?.get()?.showSendMessage(message)
//            msg = message
//        }, Realm.Transaction.OnSuccess {
//            msg?.let { msg ->
//                messageSendBag.add(uploadVoice(msg).subscribe({ message ->
//                    send(message)
//                }, {
//                    messageFailed(msg)
//                }))
//            }
//        })
    }

    // TODO 上传语音
    private fun uploadVoice(msg: VMMessage): Single<VMMessage> {
        val voice = msg.content as? VMMessageContentVoice ?: return Single.just(msg)
        return Single.create { emitter ->
//            val file = File(voice.localPath)
//            val receiveId = msg.conversation.findTarget()?.sid ?: ""
//            val senderId = VMAccountInfo.load().sid
//            messageSendBag.add(NCApi.render.chat.uploadVoice(file, voice.duration, msg.conversation.sid, senderId, receiveId).subscribe({ obj ->
//                if (obj.data.isEmpty()) {
//                    emitter.onError(Throwable("Data Error"))
//                    return@subscribe
//                }
//                val voiceObj = obj.data.first()
//                var rm: Realm? = null
//                try {
//                    rm = Store.getRealm
//                    rm.executeTransaction {
//                        voice.downloadUrl = voiceObj.cdnUrl
//                        voice.shareLink = voiceObj.shareLink
//                        voice.code = voiceObj.code
//                        voice.saveOrUpdate(it)
//                        msg.content = voice
//                    }
//                } catch (e: Exception) {
//
//                } finally {
//                    Store.closeRealm(rm)
//                }
//                emitter.onSuccess(msg)
//            }, { throwable ->
//                emitter.onError(throwable)
//            }))
        }
    }

    // TODO 发送
    private fun send(msg: VMMessage) {
//        messageSendBag.add(ChatSession.sendSync(MsgConversationSendMsg0(msg)).subscribe({
//            val msg1 = it as MsgConversationSendMsg1
//            msg1.message?.let { msg ->
//                activity?.get()?.updateMessages(listOf(msg))
//            }
//            MtaProvider.upload(MtaProvider.MtaEvent.IMSend)
//        }, {
//            if (it.localizedMessage == VMConversation.EXIT_GROUP_ERROR) {
//                activity?.get()?.exitConversation()
//            }
//            messageFailed(msg)
//        }))
    }

    private fun messageFailed(msg: VMMessage) {
        Store.openRealm.executeTransactionAsync { realm ->
            val exist = VMMessage.findBySid(realm, msg.sendLocalId)
                    ?: return@executeTransactionAsync
            if (exist.sendStatus == VMMessage.SendState.Processing) {
                exist.sendStatus = VMMessage.SendState.Failure
                exist.saveOrUpdate(realm)
                activity?.get()?.updateMessages(listOf(exist))
            }
        }
    }

    private fun makeDisplayTime(message: VMMessage) {
        VMConversation.latestMessageTime[conversationId]?.let { time ->
            if (message.time - time > VMMessage.displayTimeInterval) {
                // 距上一条消息大于时间间隔，UI中需显示时间
                message.displayTime = message.time
            } else {
                message.displayTime = 0
            }
        }
        VMConversation.latestMessageTime[conversationId] = message.time
    }
}