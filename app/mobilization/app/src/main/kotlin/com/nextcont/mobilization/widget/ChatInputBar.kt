package com.nextcont.mobilization.widget

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.nextcont.mobilization.R
import com.nextcont.mobilization.service.LocationService
import com.nextcont.mobilization.ui.main.MainActivity
import com.nextcont.mobilization.util.DrawableUtils
import java.lang.ref.WeakReference

/**
 * 聊天输入框
 */
internal class ChatInputBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {


    companion object {
        private var iAddIcon: Drawable? = null
        private var iSendIcon: Drawable? = null
        private var iVoiceIcon: Drawable? = null
        private var iKeyboardIcon: Drawable? = null
    }

    interface ChatInputBarProtocol {
        fun inputBarOnTextEnter(text: String)
        fun inputBarOnAddClick()
    }

    private var iSendButton: ImageButton
    private var iAddButton: ImageButton
    private var iToggleButton: ImageButton
    var iContentEdit: EditText
    var iRecordingButton: ChatRecordingButton

    private var textMode = false

    private var delegate: WeakReference<ChatInputBarProtocol>? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.chat_view_input_bar, this, true)
        iSendButton = findViewById(R.id.iSendButton)
        iAddButton = findViewById(R.id.iAddButton)
        iToggleButton = findViewById(R.id.iToggleButton)
        iContentEdit = findViewById(R.id.iContentEdit)
        iRecordingButton = findViewById(R.id.iRecordingButton)

        if (iAddIcon == null) {
            iAddIcon = DrawableUtils.createDrawable(context, R.mipmap.nc_ic_chat_add, R.color.chat_input_tint, R.dimen.chat_input_button_icon_size)
        }
        iAddButton.setImageDrawable(iAddIcon!!)
        iAddButton.setOnClickListener {
            delegate?.get()?.inputBarOnAddClick()
        }

        if (iSendIcon == null) {
            iSendIcon = DrawableUtils.createDrawable(context, R.mipmap.nc_ic_chat_send, R.color.chat_input_tint, R.dimen.chat_input_button_icon_size)
        }
        iSendButton.setImageDrawable(iSendIcon!!)
        iSendButton.setOnClickListener {
            sendButtonClick()
        }

        iToggleButton.setImageDrawable(getVoiceIcon())
        iToggleButton.setOnClickListener {
            toggleInputMode()
        }

        iContentEdit.visibility = VISIBLE
        iRecordingButton.visibility = GONE
        textMode = true

    }

    fun setDelegate(inputDelegate: ChatInputBarProtocol, recordingDelegate: ChatRecordingButton.RecordingButtonProtocol) {
        this.delegate = WeakReference(inputDelegate)
        this.iRecordingButton.setDelegate(recordingDelegate)
    }

    private fun toggleInputMode() {
        if (textMode) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShort("没有录音权限")
                return
            }
            // 切换语音模式
            KeyboardUtils.hideSoftInput(iContentEdit)
            iToggleButton.setImageDrawable(getKeyboardIcon())
            iRecordingButton.visibility = VISIBLE
            iContentEdit.visibility = GONE
            iContentEdit.clearFocus()
            iSendButton.isEnabled = false
            iSendButton.alpha = 0.2f
        } else {
            // 切换文字模式
            iToggleButton.setImageDrawable(getVoiceIcon())
            iRecordingButton.visibility = GONE
            iContentEdit.visibility = VISIBLE
            iContentEdit.requestFocus()
            iSendButton.isEnabled = true
            iSendButton.alpha = 1f
            KeyboardUtils.showSoftInput(iContentEdit)
        }
        textMode = !textMode

    }

    private fun getKeyboardIcon(): Drawable {
        if (iKeyboardIcon == null) {
            iKeyboardIcon = DrawableUtils.createDrawable(context, R.mipmap.nc_ic_chat_keyboard, R.color.chat_input_tint, R.dimen.chat_input_button_icon_size)
        }
        return iKeyboardIcon!!
    }

    private fun getVoiceIcon(): Drawable {
        if (iVoiceIcon == null) {
            iVoiceIcon = DrawableUtils.createDrawable(context, R.mipmap.nc_ic_chat_voice, R.color.chat_input_tint, R.dimen.chat_input_button_icon_size)
        }
        return iVoiceIcon!!
    }

    private fun sendButtonClick() {
        val text = iContentEdit.text.toString().trim()
        if (text.isEmpty()) {
            return
        }
        iContentEdit.setText("")
        delegate?.get()?.inputBarOnTextEnter(text)
    }
}