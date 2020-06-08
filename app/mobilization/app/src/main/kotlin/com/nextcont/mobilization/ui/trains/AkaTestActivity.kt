package com.nextcont.mobilization.ui.trains

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.Aka


class AkaTestActivity : AppCompatActivity() {

    private lateinit var iProgress: ProgressBar
    private lateinit var iCountdownText: TextView
    private lateinit var iRestText: TextView
    private lateinit var iTitleText: TextView
    private lateinit var iAButton: Button
    private lateinit var iBButton: Button
    private lateinit var iTestView: LinearLayout
    private lateinit var iResultView: LinearLayout

    private var position = 0
    private val aka = Aka.test

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aka_test)
        title = "心理评估"

        iProgress = findViewById(R.id.iProgress)
        iCountdownText = findViewById(R.id.iCountdownText)
        iRestText = findViewById(R.id.iRestText)
        iTitleText = findViewById(R.id.iTitleText)
        iAButton = findViewById(R.id.iAButton)
        iBButton = findViewById(R.id.iBButton)
        iTestView = findViewById(R.id.iTestView)
        iResultView = findViewById(R.id.iResultView)

        iAButton.setOnClickListener {
            next()
        }
        iBButton.setOnClickListener {
            next()
        }
        iCountdownText.text = "05:00"

        iResultView.visibility = View.GONE
        iTestView.visibility = View.VISIBLE
        updateView()
        timer.start()
    }

    private fun next() {
        position += 1
        if (position >= aka.size - 1) {
            timer.cancel()
            updateResultView()
        } else {
            updateView()
        }

    }

    private fun updateView() {
        iProgress.progress = position + 1
        iRestText.text = "${position + 1} / ${aka.size}"
        iTitleText.text = aka[position].title
        iAButton.text = aka[position].answer.first
        iBButton.text = aka[position].answer.second
    }

    private fun updateResultView() {
        val result = Aka.result.random()
        iResultView.visibility = View.VISIBLE
        iTestView.visibility = View.GONE
        findViewById<ProgressBar>(R.id.iProgress1).progress = (0..100).random()
        findViewById<ProgressBar>(R.id.iProgress2).progress = (0..100).random()
        findViewById<ProgressBar>(R.id.iProgress3).progress = (0..100).random()
        findViewById<ProgressBar>(R.id.iProgress4).progress = (0..100).random()
        findViewById<TextView>(R.id.iNameText).text = "测试报告"
        findViewById<TextView>(R.id.iResultText).text = "您的性格分析结果为：${result.title}"
        findViewById<TextView>(R.id.iContentText).text = "${result.answer.first}"
        findViewById<Button>(R.id.iCloseButton).setOnClickListener {
            finish()
        }

    }

    /**
     * 倒数计时器
     */
    private val timer: CountDownTimer = object : CountDownTimer(5 * 60 * 1000, 1000) {
        /**
         * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
         * @param millisUntilFinished
         */
        override fun onTick(millisUntilFinished: Long) {
            iCountdownText.text = formatTime(millisUntilFinished)
        }

        /**
         * 倒计时完成时被调用
         */
        override fun onFinish() {
            iCountdownText.text = "00:00"
        }
    }

    /**
     * 将毫秒转化为 分钟：秒 的格式
     *
     * @param millisecond 毫秒
     * @return
     */
    fun formatTime(millisecond: Long): String? {
        val minute: Int = (millisecond / 1000 / 60).toInt() //分钟
        val second: Int = (millisecond / 1000 % 60).toInt() //秒数
        return if (minute < 10) {
            if (second < 10) {
                "0$minute:0$second"
            } else {
                "0$minute:$second"
            }
        } else {
            if (second < 10) {
                "$minute:0$second"
            } else {
                "$minute:$second"
            }
        }
    }
}
