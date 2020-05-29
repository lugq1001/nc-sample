package com.nextcont.mobilization.ui.assess

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nextcont.mobilization.R
import com.nextcont.mobilization.ui.face.FaceLivenessExpActivity
import com.nextcont.mobilization.ui.main.MainActivity.Companion.REQ_CODE_CAMERA
import com.nextcont.mobilization.util.DialogUtil


class AssessFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_assess, container, false)

        val button = view.findViewById<Button>(R.id.iStartButton)
        button.setOnClickListener {
            startAssess()
        }
        return view
    }

    private fun startAssess() {
        val act = activity ?: return
        DialogUtil.showConfirm(act, "该考核需要人脸识别验证身份。", "开始验证") {
            checkFace()
        }
    }

    fun checkFace() {
        val act = activity ?: return
        if (ContextCompat.checkSelfPermission(act, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            act.startActivityForResult(Intent(act, FaceLivenessExpActivity::class.java), FaceLivenessExpActivity.CODE_SUCCESS)
        } else {
            // 权限申请
            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CAMERA), REQ_CODE_CAMERA)
        }
    }

    fun enterAssess() {
        val act = activity ?: return
        DialogUtil.showConfirm(act, "身份识别成功。", "开始考核") {
            act.startActivity(Intent(act, AssessActivity::class.java))
        }
    }

}
