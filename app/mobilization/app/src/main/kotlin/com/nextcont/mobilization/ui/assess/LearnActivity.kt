package com.nextcont.mobilization.ui.assess

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nextcont.mobilization.R
import com.nextcont.mobilization.ui.face.FaceLivenessExpActivity
import com.nextcont.mobilization.ui.face.FaceLivenessExpActivity.Companion.CODE_SUCCESS
import com.nextcont.mobilization.util.DialogUtil
import java.io.InputStream


class LearnActivity : AppCompatActivity() {

    companion object {
        private const val REQ_CODE_CAMERA = 101
    }

    private lateinit var iTitleText: TextView
    private lateinit var iContentText: TextView
    private lateinit var iAssessButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        supportActionBar?.hide()

        val assess = intent.getBooleanExtra("assess", false)

        iTitleText = findViewById(R.id.iTitleText)
        iContentText = findViewById(R.id.iContentText)
        iAssessButton = findViewById(R.id.iAssessButton)

        iAssessButton.setOnClickListener {
            assess()
        }

        val txt: Int
        if (assess) {
            iTitleText.text = "军事理论知识30条"
            txt = R.raw.learn
            iAssessButton.visibility = View.VISIBLE
        } else {
            iTitleText.text = "国防知识：中国人民解放军史"
            txt = R.raw.learn2
            iAssessButton.visibility = View.GONE
        }

        val `is`: InputStream = resources.openRawResource(txt)
        val buffer = ByteArray(`is`.available())
        var text = ""
        while (`is`.read(buffer) != -1) {
            text = String(buffer)
        }

        if (!assess) {
            text = text.trim().replace("\n", "");
        }
        iContentText.text = text
    }

    private fun assess() {
        DialogUtil.showConfirm(this, "在线考核需要人脸识别验证身份。", "开始验证") {
            checkFace()
        }
    }

    private fun checkFace() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivityForResult(Intent(this, FaceLivenessExpActivity::class.java), 98)
        } else {
            // 权限申请
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQ_CODE_CAMERA
            )
        }
    }

    private fun enterAssess() {
        DialogUtil.showConfirm(this, "身份验证成功。", "开始考核") {
            startActivity(Intent(this, AssessActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkFace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 98 && resultCode == CODE_SUCCESS) {
            enterAssess()
        }
    }

}
