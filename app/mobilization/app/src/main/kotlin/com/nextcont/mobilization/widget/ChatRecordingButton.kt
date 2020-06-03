package com.nextcont.mobilization.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextcont.mobilization.R
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * 全屏显示的公共加载视图
 */
@SuppressLint("AppCompatCustomView")
internal class ChatRecordingButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : TextView(context, attrs, defStyle, defStyleRes) {

    interface RecordingButtonProtocol {
        fun recordingButtonTouchDown()
        fun recordingButtonTouchMoveIn()
        fun recordingButtonTouchMoveOut()
        fun recordingButtonTouchUpInArea()
        fun recordingButtonTouchUpOutArea()
    }

    companion object {
        private const val TOUCH_MIN_INTERVAL = 500
    }

    private var latestTrigger: Long = 0

    // UI锁，防止因录音超长自动取消后，用户任处于touch状态，再次触发事件
    private var lock = false
    // 当前是否在录音触摸区域
    private var touchInArea = false

    private var delegate: WeakReference<RecordingButtonProtocol>? = null

    init {
        applyNormalStyle()
    }

    fun setDelegate(delegate: RecordingButtonProtocol) {
        this.delegate = WeakReference(delegate)
    }

    private fun applyTouchedStyle() {
        text = context.getString(R.string.activity_chat_recording_release_speak)
        setTextColor(ContextCompat.getColor(context, R.color.chat_recording_button_text_touched))
        background = context.resources.getDrawable(R.drawable.nc_chat_recording_button_touched, null)
    }

    private fun applyNormalStyle() {
        text = context.getString(R.string.activity_chat_recording_hold_speak)
        setTextColor(ContextCompat.getColor(context, R.color.chat_recording_button_text))
        background = context.resources.getDrawable(R.drawable.nc_chat_recording_button_normal, null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (System.currentTimeMillis() - latestTrigger < TOUCH_MIN_INTERVAL) {
                    // 防止操作过于频繁
                    return true
                }
                Timber.i("开始录音")
                lock = true
                touchInArea = true
                applyTouchedStyle()
                delegate?.get()?.recordingButtonTouchDown()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!lock) {
                    return true
                }
                val rect = Rect(left, top, right, bottom)
                val y = event.y.toInt()
                val top = top
                if (rect.contains(left + event.x.toInt(), top + y)) {
                    applyTouchedStyle()
                    delegate?.get()?.recordingButtonTouchMoveIn()
                } else {
                    if (y < -200) { // 触摸点超出按钮上方一定距离触发
                        touchInArea = false
                        delegate?.get()?.recordingButtonTouchMoveOut()
                    } else {
                        touchInArea = true
                        applyTouchedStyle()
                        delegate?.get()?.recordingButtonTouchMoveIn()
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!lock) {
                    return true
                }
                Timber.i("取消录音 touchExit")
                latestTrigger = System.currentTimeMillis()
                applyNormalStyle()
                if (touchInArea) {
                    delegate?.get()?.recordingButtonTouchUpInArea()
                } else {
                    delegate?.get()?.recordingButtonTouchUpOutArea()
                }
                touchInArea = false
            }
        }
        return true
    }

    fun resetStyle() {
        applyNormalStyle()
        lock = false
    }

}