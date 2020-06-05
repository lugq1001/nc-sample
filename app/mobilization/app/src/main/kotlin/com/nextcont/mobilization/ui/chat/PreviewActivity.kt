package com.nextcont.mobilization.ui.chat

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.chrisbanes.photoview.PhotoView
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.model.chat.*
import com.nextcont.mobilization.service.ImageProvider
import com.nextcont.mobilization.service.recording.Recording
import com.nextcont.mobilization.service.recording.RecordingPlayer
import com.nextcont.mobilization.util.DrawableUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * 图片、视频消息预览
 */
internal class PreviewActivity : AppCompatActivity(), Recording.RecordingProtocol,
    RecordingPlayer.RecordingPlayerProtocol {

    companion object {
        private var closeIcon: Drawable? = null
        lateinit var message: VMMessage
    }

    private val disposableBag = CompositeDisposable()

    private val viewModel = PreviewViewModel()

    private val handler = Handler()

    private lateinit var iPreviewImage: PhotoView
    private lateinit var iPreviewVideo: VideoView
    private lateinit var iPreviewProgress: ProgressBar
    private lateinit var iCloseButton: ImageButton
    private lateinit var iTipsText: TextView
    private lateinit var iSaveButton: Button
    private lateinit var iContentText: TextView
    private lateinit var iVoiceButton: Button

    // 正在播放的语音消息id
    private var playingVoiceId = ""
    private lateinit var recording: Recording
    private lateinit var recordingPlayer: RecordingPlayer

    private val saveSubject = PublishSubject.create<Unit>()

    // 点击提示文字显示错误信息，不将错误信息直接展示给用户
    private var error: String = ""

    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity_preview)

        supportActionBar?.hide()

        initView()

        disposableBag.add(saveSubject.throttleFirst(1, TimeUnit.SECONDS).subscribeOn(
            AndroidSchedulers.mainThread()).subscribe {
            saveImageToPhone()
        })

        recording = Recording()
        recording.setDelegate(this)
        recordingPlayer = RecordingPlayer(this)
        recordingPlayer.setDelegate(this)
    }

    override fun finish() {
        iPreviewVideo.stopPlayback()
        if (message.burn) {
            ChatActivity.BURN_MESSAGE_ID = message.sid
            setResult(100)
        }
        super.finish()
    }

    override fun onDestroy() {
        disposableBag.clear()
        viewModel.destroy()
        super.onDestroy()
    }

    private fun initView() {
        //applyStatusBarColor(R.color.nc_bg_activity_dark)
        iPreviewImage = findViewById(R.id.iPreviewImage)
        iPreviewVideo = findViewById(R.id.iPreviewVideo)
        iPreviewProgress = findViewById(R.id.iPreviewProgress)
        iCloseButton = findViewById(R.id.iCloseButton)
        iTipsText = findViewById(R.id.iTipsText)
        iSaveButton = findViewById(R.id.iSaveButton)
        iContentText = findViewById(R.id.iContentText)
        iVoiceButton = findViewById(R.id.iVoiceButton)
        iVoiceButton.setOnClickListener {
            playVoice()
        }
        iTipsText.setOnLongClickListener {
            if (error.isNotEmpty()) {
                ToastUtils.showShort(error)
            }
            true
        }
        iTipsText.visibility = GONE
        if (closeIcon == null) {
            closeIcon = DrawableUtils.createDrawable(this, R.mipmap.nc_ic_x, android.R.color.white, R.dimen.nc_common_icon_size)
        }
        iCloseButton.setImageDrawable(closeIcon!!)
        iCloseButton.setOnClickListener {
            if (iPreviewVideo.isPlaying) {
                iPreviewVideo.stopPlayback()
            }
            finish()
        }
        iSaveButton.setOnClickListener {
            saveSubject.onNext(Unit)
        }
    }

    override fun onStart() {
        super.onStart()
        updateView(message)
    }

    private fun updateView(message: VMMessage) {
        if (message.burn) {
            when (message.content.type) {
                VMMessageContent.Type.Image -> {
                    showImage()
                }
                VMMessageContent.Type.Video -> {
                    showVideo()
                }
                VMMessageContent.Type.Text -> {
                    val text = Companion.message.content as VMMessageContentText
                    iContentText.visibility = VISIBLE
                    iContentText.text = text.plainText
                }
                VMMessageContent.Type.Voice -> {
                    iVoiceButton.visibility = VISIBLE
                }
            }
        } else {
            when (message.content.type) {
                VMMessageContent.Type.Image -> {
                    showImage()
                }
                VMMessageContent.Type.Video -> {
                    showVideo()
                }
                else -> {
                }
            }
        }

    }

    private fun playVoice() {
        val voice = Companion.message.content as VMMessageContentVoice
        if (message.sid == playingVoiceId) {
            // 重复点击同一个语音
            playingVoiceId = ""
            recordingPlayer.stopPlay()
            resetPlayingVoice { }
            return
        }
        resetPlayingVoice {
            playingVoiceId = message.sid
            val voice = message.content as? VMMessageContentVoice ?: return@resetPlayingVoice
            voice.played = true
            message.content = voice
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
        iVoiceButton.isEnabled = true
        val voice = message.content as? VMMessageContentVoice ?: return
        recordingPlayer.startPlay(voice.localPath)
    }

    private fun resetPlayingVoice(completion: () -> Unit) {
        completion()
    }

    override fun onStop() {
        recordingPlayer.stopPlay()
        super.onStop()
    }

    private fun showVideo() {
        val video = message.content as VMMessageContentVideo
        playVideo(video.localPath)
    }

    private fun showImage() {
        val image = message.content as VMMessageContentImage
        val localPath = image.localPath
        if (localPath.isNotEmpty() && FileUtils.isFileExists(localPath)) {
            // 本地存在文件
            iPreviewImage.visibility = VISIBLE
            //iSaveButton.visibility = VISIBLE
            ImageProvider.loadImageWithResult(this, localPath, iPreviewImage, null)
            return
        }
        val downloadUrl = image.getDownloadUrl
        startLoading()
        iPreviewImage.visibility = VISIBLE
        ImageProvider.loadImageWithResult(this, downloadUrl, iPreviewImage, completion = {
            stopLoading()
            //iSaveButton.visibility = VISIBLE
        })
    }

    private fun startLoading() {
        handler.post {
            iPreviewProgress.visibility = VISIBLE
        }
    }

    private fun stopLoading() {
        handler.post {
            iPreviewProgress.visibility = GONE
        }
    }

    private fun playVideo(path: String) {
        handler.post {
            this.filePath = path
            iPreviewProgress.visibility = GONE
            iTipsText.visibility = GONE

            val uri = Uri.parse(path)//将路径转换成uri
            iPreviewVideo.visibility = VISIBLE
            iSaveButton.visibility = GONE
            iPreviewVideo.setVideoURI(uri)
            iPreviewVideo.setMediaController(MediaController(this))
            iPreviewVideo.setOnPreparedListener {
                iPreviewProgress.visibility = GONE
                it.start()
            }
            iPreviewVideo.start()
        }
    }

    private fun saveImageToPhone() {
        viewModel.message?.let { message ->
            val image = message.content as VMMessageContentImage
            startLoading()
            iSaveButton.isEnabled = false
            disposableBag.add(viewModel.downloadImage(image.getDownloadUrl).subscribe({ path ->
                handler.post {
                    val file = File(path)
                    val uri = Uri.fromFile(file)
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    MediaStore.Images.Media.insertImage(contentResolver, bitmap, "image", "From NextCont")

                    ToastUtils.showShort(getString(R.string.activity_preview_image_saved))
                    iSaveButton.isEnabled = true
                    stopLoading()
                }
            }, {
                handler.post {
                    ToastUtils.showShort(it.localizedMessage)
                    iSaveButton.isEnabled = true
                    stopLoading()
                }
            }))
        }

    }

    override fun recordingWillOverTime(restTime: Long) {
    }

    override fun recordingCompleted(path: String, duration: Long) {
    }

    override fun recordingVolumeChanged(volume: Int) {
    }

    override fun recordingOnError(msg: String) {
        ToastUtils.showShort(msg)
    }

    override fun recordingFailedTimeTooShort() {
    }

    override fun recordingPlayerCompleted() {
        resetPlayingVoice {
            playingVoiceId = ""
            iVoiceButton.isEnabled = true
        }
    }


}
