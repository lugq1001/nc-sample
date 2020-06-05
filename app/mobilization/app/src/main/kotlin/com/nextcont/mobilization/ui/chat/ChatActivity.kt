package com.nextcont.mobilization.ui.chat

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.model.chat.*
import com.nextcont.mobilization.service.LocationService
import com.nextcont.mobilization.service.recording.Recording
import com.nextcont.mobilization.service.recording.RecordingPlayer
import com.nextcont.mobilization.ui.main.MainActivity
import com.nextcont.mobilization.util.MatisseEngine
import com.nextcont.mobilization.widget.ChatInputBar
import com.nextcont.mobilization.widget.ChatRecordingButton
import com.nextcont.mobilization.widget.ChatRecordingView
import com.squareup.moshi.Moshi
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * 聊天界面
 */
internal class ChatActivity : AppCompatActivity(), ChatInputBar.ChatInputBarProtocol, ChatRecordingButton.RecordingButtonProtocol, Recording.RecordingProtocol, RecordingPlayer.RecordingPlayerProtocol {

    companion object {
        const val INTENT_KEY_CONVERSION = "CONVERSATION"

        private const val REQ_CODE_CHOOSE_IMAGE = 100

        private const val REQ_CODE_BURN_MESSAGE = 101
        lateinit var BURN_MESSAGE_ID: String

        val updateMonitor = PublishSubject.create<Unit>()!!
    }

    val viewModel = ChatViewModel()

    private lateinit var iChatRecycler: RecyclerView
    private lateinit var iInputBar: ChatInputBar
    private lateinit var iRecordingView: ChatRecordingView

    private lateinit var recording: Recording
    private lateinit var recordingPlayer: RecordingPlayer

    private val loadMoreMonitor = PublishSubject.create<Unit>()

    private var isRecording = false

    private var adapter = ChatAdapter()

    private lateinit var conversion: VMConversation

    private val handler = Handler()
    private var hasInit = false

    private val volumeDisposables = CompositeDisposable()
    private val itemChildSubject = PublishSubject.create<Pair<Int, Int>>()

    private lateinit var loadMoreView: View
    private var loadingMore = false

    private val disposableBag = CompositeDisposable()

    // 正在播放的语音消息id
    private var playingVoiceId = ""
    // 是否自动滚动至底部
    private var shouldAutoScrollToBottom = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity_chat)

        val json = Moshi.Builder().build()
        conversion = json.adapter(VMConversation::class.java).fromJson(intent.getStringExtra(INTENT_KEY_CONVERSION)!!)!!

        title = conversion.name

        viewModel.bind(this, conversion)
        recording = Recording()
        recording.setDelegate(this)
        recordingPlayer = RecordingPlayer(this)
        recordingPlayer.setDelegate(this)
        disposableBag.add(itemChildSubject.throttleFirst(500, TimeUnit.MILLISECONDS).subscribe {
            handler.post {
                itemChildClick(it)
            }
        })
        disposableBag.add(adapter.imageLoadedSubject.throttleLatest(500, TimeUnit.MILLISECONDS).subscribe {
            if (shouldAutoScrollToBottom) {
                handler.post {
                    scrollToBottom()
                }
            }
        })
        disposableBag.add(loadMoreMonitor.throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe {
            loadMoreMessage()
        })

        initView()
    }

    private fun initView() {
        iInputBar = findViewById(R.id.iInputBar)
        iInputBar.setDelegate(this, this)

        iRecordingView = findViewById(R.id.iRecordingView)

        iChatRecycler = findViewById(R.id.iChatRecycler)
        val layout = LinearLayoutManager(this)
        layout.reverseLayout = false
        iChatRecycler.layoutManager = layout
        (iChatRecycler.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        iChatRecycler.adapter = adapter

        // TODO bindToRecyclerView
        // adapter.bindToRecyclerView(iChatRecycler)

        adapter.setNewInstance(mutableListOf())

        adapter.addChildClickViewIds(R.id.iContentImageWrapper)
        adapter.addChildClickViewIds(R.id.iContentVideoWrapper)
        adapter.addChildClickViewIds(R.id.iVoiceWrapper)
        adapter.addChildClickViewIds(R.id.iContentTextWrapper)
        adapter.addChildClickViewIds(R.id.iBurnTextWrapper)

        adapter.setOnItemChildLongClickListener { _, view, position ->
            this.adapter.getItem(position).let {
                when (view.id) {
                    R.id.iContentTextWrapper -> {
                        copyText(it.content as VMMessageContentText)
                    }
                }
            }
            true
        }

        // 子视图点击
        adapter.setOnItemChildClickListener { _, view, position ->
            itemChildSubject.onNext(Pair(view.id, position))
        }
        val viewGroup = findViewById<View>(android.R.id.content) as ViewGroup
        loadMoreView = LayoutInflater.from(this).inflate(R.layout.common_load_more_view, viewGroup, false)
        adapter.addHeaderView(loadMoreView)
        addScrollListener()
        addInputListener()
        addKeyboardListener()
    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // 权限申请
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 90)
        }

        if (hasInit) {
            return
        }
        hasInit = true
        viewModel.loadMessages()
    }

    override fun finish() {
        KeyboardUtils.hideSoftInput(this)
        updateMonitor.onNext(Unit)
        super.finish()
    }

    override fun onPause() {
        KeyboardUtils.hideSoftInput(this)
        super.onPause()
    }

    override fun onStop() {
        recordingPlayer.stopPlay()
        resetPlayingVoice { }
        super.onStop()
    }

    // 防止多次点击
    private fun itemChildClick(pair: Pair<Int, Int>) {
        this.adapter.getItem(pair.second).let {
            when (pair.first) {
                R.id.iAvatarWrapper -> {
                    showUserInfo(it.sender)
                }
                R.id.iContentImageWrapper, R.id.iContentVideoWrapper, R.id.iBurnTextWrapper -> {
                    previewContent(it)
                }
                R.id.iVoiceWrapper -> {
                    playVoice(it)
                }
            }
        }
    }

    /**
     * 更新UI，Title等
     */
    fun updateView(conversation: VMConversation) {
        handler.post {
            title = conversation.name
            // TODO 更新UI
//            when (conversation.chatType) {
//                VMConversation.ChatType.Group -> {
//                    iRightButton?.visibility = VISIBLE
//                    iRightButton?.setImageDrawable(ResourceProvider.getNaviGroupIcon(this))
//                    iRightButton?.setOnClickListener {
//                        val intent = Intent(this, ConversationInfoActivity::class.java)
//                        intent.putExtra(ConversationInfoActivity.INTENT_KEY_CONVERSATION_ID, conversationId)
//                        startActivityForResult(intent, REQ_CODE_GROUP_INFO)
//                    }
//                }
//                VMConversation.ChatType.Personal -> {
//                    iRightButton?.visibility = GONE
//                }
//            }
        }
    }

    /**
     * 查看图片、视频
     */
    private fun previewContent(message: VMMessage) {
        if (isRecording) {
            return
        }
        val intent = Intent(this, PreviewActivity::class.java)
        PreviewActivity.message = message
        if (message.burn) {
            if (message.read) {
                ToastUtils.showShort("该消息已销毁")
            } else {
                startActivityForResult(intent, REQ_CODE_BURN_MESSAGE)
            }
        } else {
            startActivity(intent)
        }

    }



    /**
     * TODO 点击头像
     */
    private fun showUserInfo(sender: VMContact) {
        if (isRecording) {
            return
        }
//        val intent = Intent(this, ProfileActivity::class.java)
//        intent.putExtra(ProfileActivity.INTENT_KEY_USER_ID, sender.sid)
//        startActivity(intent)
    }

    /**
     * TODO 复制文本
     */
    private fun copyText(textContent: VMMessageContentText) {
        if (isRecording) {
            return
        }
        //ClipboardProvider.copy(this, textContent.plainText)
        //ToastUtils.showShort(getString(R.string.app_clipboard_copy_success))
    }

    /**
     * 滚动事件处理
     */
    private fun addScrollListener() {
        iChatRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        // 滚动时收起键盘
                        KeyboardUtils.hideSoftInput(this@ChatActivity)
                        shouldAutoScrollToBottom = false
                    }
                    RecyclerView.HORIZONTAL -> {
                        if (!recyclerView.canScrollVertically(-1)) {
                            if (viewModel.page != Integer.MAX_VALUE) {
                                loadMoreMonitor.onNext(Unit)
                            }
                            Timber.i("滚动至顶部")
                        } else if (!recyclerView.canScrollVertically(1)) {
                            Timber.i("滚动至底部")
                            shouldAutoScrollToBottom = true
                        }
                    }
                }
            }
        })
    }

    private fun loadMoreMessage() {
        if (viewModel.page == Integer.MAX_VALUE) {
            return
        }
        if (loadingMore) {
            return
        }
        loadingMore = true
        handler.postDelayed({
            viewModel.loadMoreMessages()
            // 显示0.5秒loading 防止过快闪屏
        }, 500)
    }

    /**
     * 加载消息
     */
    fun onLoadMessages(messages: MutableList<VMMessage>) {
        handler.post {
            adapter.removeHeaderView(loadMoreView)
            adapter.setList(messages)
            scrollToBottom()
        }
    }

    /**
     * 加载更多消息
     */
    fun onLoadMoreMessages(messages: MutableList<VMMessage>) {
        handler.post {
            adapter.removeHeaderView(loadMoreView)
            adapter.addData(0, messages)
            if (viewModel.page != Integer.MAX_VALUE) {
                adapter.setHeaderView(loadMoreView)
            }
            loadingMore = false
        }
    }

    /**
     * TODO 软键盘事件处理
     */
    private fun addKeyboardListener() {
        KeyboardVisibilityEvent.setEventListener(this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    handler.post { scrollToBottom() }
                }
            }

        })
    }

    /**
     * 文字输出处理 换行时是view滚动至底部
     */
    private fun addInputListener() {
        iInputBar.iContentEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                scrollToBottom()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    /**
     * 使视图滚动到底部
     */
    private fun scrollToBottom() {
        val position = max(adapter.itemCount - 1, 0)
        iChatRecycler.scrollToPosition(position)
    }

    //====================================================
    // 消息通知处理
    //====================================================
//    fun showNewMessages(messages: List<VMMessage>) {
//        this.viewModel.readMessages(messages)
//        handler.post {
//            val result = mutableListOf<VMMessage>()
//            val data = adapter.data
//            messages.forEach { m1 ->
//                var exist = false
//                data.forEach { m2 ->
//                    if (m1.sid == m2.sid) {
//                        exist = true
//                    }
//                }
//                if (!exist) {
//                    result.add(m1)
//                }
//            }
//            if (result.isEmpty()) {
//                return@post
//            }
//            this.adapter.addData(result)
//            if (shouldAutoScrollToBottom) {
//                this.scrollToBottom()
//            }
//        }
//    }

    fun showSendMessage(message: VMMessage) {
        handler.post {
            this.adapter.addData(message)
            shouldAutoScrollToBottom = true
            this.scrollToBottom()
        }
    }

    fun updateMessages(messages: List<VMMessage>) {
        handler.post {
            messages.forEach { message ->
                val index = this.adapter.data.indexOfFirst {
                    message.localId == it.localId
                }
                if (index >= 0) {
                    this.adapter.setData(index, message)
                }
            }
        }
    }

    //====================================================
    // 语音相关
    //====================================================
    private fun playVoice(message: VMMessage) {
        (message.content as? VMMessageContentVoice)?.loading ?: return // 禁止播放正在下载的语音
        if (message.sid == playingVoiceId) {
            // 重复点击同一个语音
            playingVoiceId = ""
            recordingPlayer.stopPlay()
            resetPlayingVoice { }
            volumeDisposables.clear()
            return
        }
        resetPlayingVoice {
            playingVoiceId = message.sid
            val voice = message.content as? VMMessageContentVoice ?: return@resetPlayingVoice
            voice.played = true
            message.content = voice
            updateMessages(listOf(message))
            voicePlay(message)
            if (message.sid == VMConversation.MESSAGE_ID_VOICE) {
                (VMConversation.messages[VMConversation.CONVERSION_ID_GROUP]!!.first().content as VMMessageContentVoice).played = true
            }
        }
    }

    private fun voicePlay(message: VMMessage) {
        if (message.sid != playingVoiceId) {
            // 防止下载成功后用户已点击其他语音
            return
        }
        val voice = message.content as? VMMessageContentVoice ?: return
        recordingPlayer.startPlay(voice.localPath)
        updateMessages(listOf(message))
        val position = adapter.data.indexOfFirst {
            it.sid == message.sid
        }
        if (position < 0) {
            return
        }
        val accountId = User.load()?.id
        volumeDisposables.add(Observable.interval(500, TimeUnit.MILLISECONDS).subscribe {
            val volumeImage = adapter.getViewByPosition(position, R.id.iVolumeImage) as? ImageView
                    ?: return@subscribe
            val i = (0..2).shuffled().last()
            handler.post {
                if (message.sender.sid != accountId) {
                    volumeImage.setImageDrawable(ChatAdapter.getVolumeIconLeft(this)[i])
                } else {
                    volumeImage.setImageDrawable(ChatAdapter.getVolumeIconRight(this)[i])
                }
            }
        })
    }

    private fun resetPlayingVoice(completion: () -> Unit) {
        volumeDisposables.clear()
        handler.post {
            adapter.data.forEachIndexed { index, message ->
                when (message.content.type) {
                    VMMessageContent.Type.Voice -> {
                        (message.content as VMMessageContentVoice).loading = false
                        adapter.setData(index, message)
                    }
                    else -> {

                    }
                }
            }
            completion()
        }
    }


    //====================================================
    // ChatInputBarProtocol
    //====================================================
    override fun inputBarOnTextEnter(text: String) {
        Timber.i("发送文字消息: $text")
        viewModel.sendText(text)
    }

    override fun inputBarOnAddClick() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(false)
                .capture(true)
                .captureStrategy(CaptureStrategy(true, MobApp.UrlAuthority))
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(MatisseEngine())
                .forResult(REQ_CODE_CHOOSE_IMAGE)
    }

    // ==============================
    // RecordingButtonProtocol
    // ==============================
    override fun recordingButtonTouchDown() {
        Timber.i("开始录音")
        recordingPlayer.stopPlay()
        isRecording = true
        iRecordingView.showRecordingView()
        iRecordingView.changeTips(true)
        recording.start()
    }

    override fun recordingButtonTouchMoveIn() {
        if (isRecording) {
            iRecordingView.showRecordingView()
            iRecordingView.changeTips(true)
        } else {
            iRecordingView.dismiss()
        }
    }

    override fun recordingButtonTouchMoveOut() {
        iRecordingView.showReleaseToCancelView()
        iRecordingView.changeTips(false)

    }

    override fun recordingButtonTouchUpInArea() {
        Timber.i("停止录音")
        recording.stop()
        iRecordingView.dismiss()
        isRecording = false
    }

    override fun recordingButtonTouchUpOutArea() {
        Timber.i("取消录音 TOUCH_UP_OUT_AREA")
        recording.cancel()
        iRecordingView.dismiss()
        isRecording = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_CHOOSE_IMAGE -> if (resultCode == RESULT_OK) {
                val paths = Matisse.obtainPathResult(data)
                paths.firstOrNull()?.let {
                    val file = File(it)
                    if (!file.exists() || file.length() == 0L) {
                        ToastUtils.showShort("")
                        return
                    }
                    viewModel.sendImage(it)
                }
            }
            REQ_CODE_BURN_MESSAGE -> {
                Timber.i("焚毁消息: $BURN_MESSAGE_ID")
                val messages = adapter.data
                val index = messages.indexOfFirst { msg ->
                    msg.sid == BURN_MESSAGE_ID
                }
                if (index >= 0) {
                    messages[index].read = true
                    adapter.setList(messages)
                }
            }
        }
    }

    // ====================================
    // Recording.RecordingProtocol
    // ====================================
    override fun recordingCompleted(path: String, duration: Long) {
        val durationSecond = duration / 1000
        Timber.i("录音成功: ${durationSecond}秒 $path")
        handler.post {
            iRecordingView.dismiss()
            iInputBar.iRecordingButton.resetStyle()
            viewModel.send(path, durationSecond)
        }
    }

    override fun recordingWillOverTime(restTime: Long) {
        Timber.i("录音即将超时: restTime $restTime")
        handler.post {
            iRecordingView.showCountdownView(restTime)
        }
    }

    override fun recordingVolumeChanged(volume: Int) {
        handler.post {
            iRecordingView.update(volume)
        }
    }

    override fun recordingOnError(msg: String) {
        ToastUtils.showShort(msg)
    }

    override fun recordingFailedTimeTooShort() {
        Timber.i("录音时间过短")
        handler.post {
            iRecordingView.showErrorView("说话时间太短")
        }
    }

    // ====================================
    // RecordingPlayer.RecordingPlayerProtocol
    // ====================================
    override fun recordingPlayerCompleted() {
        resetPlayingVoice {
            playingVoiceId = ""
        }
    }


}
