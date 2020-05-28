package com.nextcont.mobilization.ui.assess

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nextcont.mobilization.R
import com.nextcont.mobilization.service.LocationService


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
        requestPermissions()
        val act = activity?: return
        act.startActivity(Intent(act, FaceLivenessExpActivity::class.java))
        //startActivity(Intent(activity, AssessActivity::class.java))
    }

    private fun requestPermissions() {
        val act = activity ?: return
        if (ContextCompat.checkSelfPermission(act, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            LocationService.start()
        } else {
            // 权限申请
            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }
}
