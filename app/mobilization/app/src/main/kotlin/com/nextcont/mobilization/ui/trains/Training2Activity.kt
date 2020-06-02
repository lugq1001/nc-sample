package com.nextcont.mobilization.ui.trains

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.Training
import com.nextcont.mobilization.util.DialogUtil
import com.squareup.moshi.Moshi


class Training2Activity : AppCompatActivity() {

    companion object {
        const val KEY_TRAIN = "train"
    }

    private lateinit var iVideoView: SimpleExoPlayerView
    private lateinit var iSimpleExoPlayer: SimpleExoPlayer

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_2)

        val json = Moshi.Builder().build()
        val jsonAdapter = json.adapter(Training::class.java)

        val train = jsonAdapter.fromJson(intent.getStringExtra(KEY_TRAIN)!!)!!

        title = train.title


        initPlayer()

        val mp4 = "file:///android_asset/train.mp4"

        //创建一个DataSource对象，通过它来下载多媒体数据
        val dataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"))
        //这是一个代表将要被播放的媒体的MediaSource
        val videoSource = ExtractorMediaSource(
            Uri.parse(mp4),
            dataSourceFactory, DefaultExtractorsFactory(), null, null
        )

        //使用资源准备播放器
        iSimpleExoPlayer.prepare(videoSource)

        iVideoView.hideController()

        findViewById<Button>(R.id.iStartButton).setOnClickListener {
            startTrain()
        }
    }


    /**
     * 初始化player
     */
    private fun initPlayer() {
        //1. 创建一个默认的 TrackSelector
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTackSelectionFactory: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTackSelectionFactory)
        val loadControl: LoadControl = DefaultLoadControl()
        //2.创建ExoPlayer
        iSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
        //3.创建SimpleExoPlayerView
        iVideoView = findViewById(R.id.iVideoView)
        //4.为SimpleExoPlayer设置播放器
        iVideoView.player = iSimpleExoPlayer

    }

    override fun onStart() {
        super.onStart()
        iSimpleExoPlayer.playWhenReady = true
        iSimpleExoPlayer.playbackState
    }

    override fun onStop() {
        super.onStop()
        iSimpleExoPlayer.playWhenReady = false
        iSimpleExoPlayer.playbackState
    }

    private fun startTrain() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10485760L)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120)

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val pd = ProgressDialog(this)
            pd.setMessage("正在上传,请稍候...")
            pd.show()
            handler.postDelayed({
                DialogUtil.showAlert(this, "上传成功") {
                    finish()
                }
            }, 3000)
        }

    }
}
