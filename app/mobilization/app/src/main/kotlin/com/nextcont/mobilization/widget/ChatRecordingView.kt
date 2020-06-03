package com.nextcont.mobilization.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextcont.mobilization.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * 聊天输入框
 */
internal class ChatRecordingView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    companion object {
        private var volumeImages: List<Drawable>? = null
    }

    private var iVolumeView: LinearLayout
    private var iVolumeImage: ImageView
    private var iReleaseToCancelView: LinearLayout
    private var iWarningView: LinearLayout
    private var iCountdownView: LinearLayout
    private var iCountdownText: TextView
    private var iTipsText: TextView

    private var isShowing = false
    private var isShowingError = false

    private var latestPosition = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.chat_view_recording, this, true)
        iVolumeView = findViewById(R.id.iVolumeView)
        iVolumeImage = findViewById(R.id.iVolumeImage)
        iReleaseToCancelView = findViewById(R.id.iReleaseToCancelView)
        iWarningView = findViewById(R.id.iWarningView)
        iCountdownView = findViewById(R.id.iCountdownView)
        iCountdownText = findViewById(R.id.iCountdownText)
        iTipsText = findViewById(R.id.iTipsText)

        val res = context.resources
        if (volumeImages == null) {
            volumeImages = listOf(
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_0, null),
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_1, null),
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_2, null),
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_3, null),
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_4, null),
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_5, null),
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_6, null),
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_7, null),
                    res.getDrawable(R.mipmap.nc_ic_chat_volume_8, null)

            )
        }
        dismiss()
    }

    fun dismiss() {
        if (isShowingError) {
            return
        }
        iCountdownView.visibility = GONE
        iCountdownText.text = ""
        visibility = GONE
        isShowing = false
    }

    fun showRecordingView() {
        val images = volumeImages ?: return
        val isCountdown = iCountdownView.visibility == VISIBLE
        iVolumeImage.setImageDrawable(images[0])
        iVolumeView.visibility = if (isCountdown) GONE else VISIBLE
        iWarningView.visibility = GONE
        iReleaseToCancelView.visibility = GONE
        visibility = VISIBLE
        isShowing = true
    }

    fun changeTips(inRecordingArea: Boolean) {
        iTipsText.text = if (inRecordingArea) context.getString(R.string.activity_chat_recording_slide_cancel) else context.getString(R.string.activity_chat_recording_lose_cancel)
        iTipsText.setBackgroundColor(if (inRecordingArea) ContextCompat.getColor(context, android.R.color.transparent) else ContextCompat.getColor(context, android.R.color.holo_red_dark))
    }

    fun showReleaseToCancelView() {
        val isCountdown = iCountdownView.visibility == VISIBLE
        iVolumeView.visibility = GONE
        iReleaseToCancelView.visibility = if (isCountdown) GONE else VISIBLE
        iWarningView.visibility = GONE
        visibility = VISIBLE
        isShowing = true
    }


    fun update(db: Int) {
        val images = volumeImages ?: return
        var result = db
        if (result < 50) {
            result = 10 // 一般环境测试几台手机会在10~50之间抖动, 使其显示为1格
        }

        var position = result / 10
        position = min(position, images.size - 1)
        if (latestPosition == position) {
            return
        }
        iVolumeImage.setImageDrawable(images[position])
        latestPosition = position
    }

    fun showErrorView(error: String) {
        this.isShowingError = true
        visibility = VISIBLE
        iVolumeView.visibility = GONE
        iWarningView.visibility = VISIBLE
        iReleaseToCancelView.visibility = GONE
        iCountdownView.visibility = GONE
        iTipsText.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        iTipsText.text = error
        val disposable = CompositeDisposable()
        disposable.add(Observable.timer(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            isShowingError = false
            dismiss()
            disposable.clear()
        })
    }

    fun showCountdownView(restTime: Long) {
        if (!isShowing) {
            // 音量由handler.post队列触发,会导致已dismiss但再次显示
            visibility = GONE
            return
        }
        iCountdownView.visibility = VISIBLE
        iCountdownText.text = String.format(Locale.getDefault(), "%d", restTime / 1000 + 1)
        showRecordingView()
    }

//    interface ErrorListener {
//        fun showErrorCompleted()
//    }


}