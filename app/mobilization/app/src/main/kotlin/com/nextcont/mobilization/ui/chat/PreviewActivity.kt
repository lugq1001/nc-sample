package com.nextcont.mobilization.ui.chat

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.chrisbanes.photoview.PhotoView
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.chat.VMMessage
import com.nextcont.mobilization.model.chat.VMMessageContent
import com.nextcont.mobilization.model.chat.VMMessageContentImage
import com.nextcont.mobilization.service.ImageProvider
import com.nextcont.mobilization.util.DrawableUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * 图片、视频消息预览
 */
internal class PreviewActivity : AppCompatActivity() {

    companion object {
        private var closeIcon: Drawable? = null
        const val INTENT_KEY_MESSAGE_ID = "MESSAGE_ID"
    }

    private val disposableBag = CompositeDisposable()

    private val viewModel = PreviewViewModel()

    private val handler = Handler()

    private lateinit var messageId: String

    private lateinit var iPreviewImage: PhotoView
    private lateinit var iPreviewVideo: VideoView
    private lateinit var iPreviewProgress: ProgressBar
    private lateinit var iCloseButton: ImageButton
    private lateinit var iTipsText: TextView
    private lateinit var iSaveButton: Button

    private val saveSubject = PublishSubject.create<Unit>()

    // 点击提示文字显示错误信息，不将错误信息直接展示给用户
    private var error: String = ""

    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 显示status bar文字为白色
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        setContentView(R.layout.chat_activity_preview)

        messageId = intent.getStringExtra(INTENT_KEY_MESSAGE_ID)

        viewModel.bind(this, messageId)

        initView()

        disposableBag.add(saveSubject.throttleFirst(1, TimeUnit.SECONDS).subscribeOn(
            AndroidSchedulers.mainThread()).subscribe {
            saveImageToPhone()
        })
    }

    override fun finish() {
        iPreviewVideo.stopPlayback()
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

    fun updateView(message: VMMessage) {
        handler.post {
            when (message.content.type) {
                VMMessageContent.Type.Image -> {
                    val image = message.content as VMMessageContentImage
                    val localPath = image.localPath
                    if (localPath.isNotEmpty() && FileUtils.isFileExists(localPath)) {
                        // 本地存在文件
                        iPreviewImage.visibility = VISIBLE
                        iSaveButton.visibility = VISIBLE
                        ImageProvider.loadImageWithResult(this, localPath, iPreviewImage, null)
                        return@post
                    }
                    val downloadUrl = image.getDownloadUrl
                    startLoading()
                    iPreviewImage.visibility = VISIBLE
                    ImageProvider.loadImageWithResult(this, downloadUrl, iPreviewImage, completion = {
                        stopLoading()
                        iSaveButton.visibility = VISIBLE
                    })
                }
                VMMessageContent.Type.Video -> {
                    viewModel.initVideoFile(message)
                }
                else -> {
                }
            }
        }
    }

    fun startLoading() {
        handler.post {
            iPreviewProgress.visibility = VISIBLE
        }
    }

    private fun stopLoading() {
        handler.post {
            iPreviewProgress.visibility = GONE
        }
    }

    fun showDownloadProgress(percent: Int) {
        handler.post {
            iPreviewProgress.visibility = GONE
            iPreviewVideo.visibility = GONE
            iPreviewImage.visibility = GONE
            iTipsText.visibility = VISIBLE
            val progress = "$percent %"
            iTipsText.text = progress
        }
    }

    fun playVideo(path: String) {
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

    fun showVideoError(error: String) {
        this.error = error
        handler.post {
            iPreviewProgress.visibility = GONE
            iPreviewVideo.visibility = GONE
            iTipsText.visibility = VISIBLE
            iTipsText.text = getString(R.string.activity_preview_video_failed)
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


}
