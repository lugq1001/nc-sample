package com.nextcont.mobilization.service.recording

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import java.lang.ref.WeakReference

/**
 * 录音播放
 */
internal class RecordingPlayer(private val context: Context) : MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    interface RecordingPlayerProtocol {
        fun recordingPlayerCompleted()
    }

    private var mediaPlayer: MediaPlayer? = null
    private var delegate: WeakReference<RecordingPlayerProtocol>? = null

    init {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)
    }

    fun setDelegate(delegate: RecordingPlayerProtocol) {
        this.delegate = WeakReference(delegate)
    }

    fun startPlay(filePath: String) {
        try {
            stopPlay()
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.mode = AudioManager.MODE_NORMAL
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(filePath)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener { it.start() }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun stopPlay() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        delegate?.get()?.recordingPlayerCompleted()
    }
}