package com.nextcont.mobilization.service.recording

import android.media.MediaRecorder
import com.nextcont.mobilization.service.PathProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min

/**
 * 录音工具
 * filePath 录音文件
 */
class Recording : MediaRecorder.OnErrorListener {

    interface RecordingProtocol {

        fun recordingWillOverTime(restTime: Long)
        fun recordingCompleted(path: String, duration: Long)
        fun recordingVolumeChanged(volume: Int)
        fun recordingOnError(msg: String)
        fun recordingFailedTimeTooShort()

    }

    companion object {
        /// 最大录音时间 秒
        private const val MAX_DURATION = 60L
        /// 最小录音时间 秒
        private const val MIN_DURATION = 1L
        /// 几秒后显示倒计时
        private const val REST_TO_COUNTDOWN = 10L
    }

    private var startTime = 0L
    private var mediaRecorder: MediaRecorder? = null
    private var delegate: WeakReference<RecordingProtocol>? = null

    private val disposable = CompositeDisposable()
    private lateinit var filePath: String

    fun setDelegate(delegate: RecordingProtocol) {
        this.delegate = WeakReference(delegate)
    }

    private fun makeTempFile(): String {
        return "${PathProvider.tempFolder(PathProvider.FileType.Voice)}${File.separator}${UUID.randomUUID()}.amr"
    }

    fun start() {
        try {
            this.filePath = makeTempFile()
            val file = File(filePath)
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.setAudioSamplingRate(8000)
            mediaRecorder?.setAudioChannels(1)
            mediaRecorder?.setOutputFile(file.absolutePath)
            mediaRecorder?.setOnErrorListener(this)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            startTime = System.currentTimeMillis()
            disposable.add(Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
                val duration = System.currentTimeMillis() - startTime
                val restDuration: Long = MAX_DURATION * 1000 - duration
                if (restDuration <= REST_TO_COUNTDOWN * 1000) { // 最后n秒显示倒计时
                    delegate?.get()?.recordingWillOverTime(max(restDuration, 0))
                    if (restDuration <= 0) {
                        stop()
                    }
                }
            })
            disposable.add(Observable.interval(800, TimeUnit.MICROSECONDS).subscribe {
                if (mediaRecorder == null) {
                    return@subscribe
                }
                val ratio = mediaRecorder?.maxAmplitude ?: 0
                if (ratio in 1..999999) {
                    val db = 20 * log10(ratio.toDouble())
                    Timber.d("db: $db")
                    delegate?.get()?.recordingVolumeChanged(db.toInt())
                }
            })
        } catch (e: IOException) {
            cancel()
            val error = "Recording failed: ${e.localizedMessage}"
            Timber.e(error)
            delegate?.get()?.recordingOnError(error)
        }
    }

    fun stop() {
        cancel()
        if (startTime < 1) {
            return
        }
        val duration = min(MAX_DURATION * 1000, System.currentTimeMillis() - startTime)
        if (duration <= MIN_DURATION * 1000) {
            // 说话时间过短
            delegate?.get()?.recordingFailedTimeTooShort()
            return
        }
        delegate?.get()?.recordingCompleted(filePath, duration)
    }

    fun cancel() {
        try {
            disposable.clear()
            if (mediaRecorder == null) {
                return
            }
            mediaRecorder?.setOnErrorListener(this) // 调用MediaRecorder的start()与stop()间隔不能小于1秒(有时候大于1秒也崩)，否则必崩, 需加入此代码
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
            Timber.e("取消录音失败: ${e.localizedMessage}")
        }

    }

    // ================================
    // OnErrorListener
    // ================================
    override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {
        Timber.e("Recording Error: $what $extra")
    }

}