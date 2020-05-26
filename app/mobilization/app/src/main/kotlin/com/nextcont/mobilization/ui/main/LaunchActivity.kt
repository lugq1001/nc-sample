package com.nextcont.mobilization.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View.GONE
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.model.Account
import com.nextcont.mobilization.network.MobApi
import com.nextcont.mobilization.util.DialogUtil
import com.nextcont.mobilization.R
import com.nextcont.mobilization.ui.DashboardActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class LaunchActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        supportActionBar?.hide()

    }

    override fun onStart() {
        super.onStart()
        // 未登录
        handler.postDelayed(Runnable {
            startActivity(Intent(this, LoginActivity::class.java))
        }, 1500)

    }

    private fun uploadDevice() {
        val deviceName = Settings.Secure.getString(contentResolver, "bluetooth_name")
        val fingerprint = android.os.Build.FINGERPRINT

//        MobApi.deviceRegister(deviceName, fingerprint)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ resp ->
//
//                if (resp.disabled) {
//                    DialogUtil.showAlert(this, "该应用已失效") {
//                        findViewById<ProgressBar>(R.id.iProgress).visibility = GONE
//                        findViewById<TextView>(R.id.iTipsText).visibility = GONE
//                    }
//                    return@subscribe
//                }
//
//                Account.deviceId = resp.deviceId
//
//                resp.user?.let { user ->
//                    // 已登录
//                    if (resp.expired) {
//                        DialogUtil.showAlert(this, "登录已过期", action = {
//                            startActivity(Intent(this, LoginActivity::class.java))
//                        })
//                    } else {
//                        Account.user = user
//                        startActivity(Intent(this, DashboardActivity::class.java))
//                    }
//
//                }?: kotlin.run {
//                    // 未登录
//                    startActivity(Intent(this, LoginActivity::class.java))
//                }
//            }, { e ->
//                DialogUtil.showAlert(this, e.localizedMessage) {
//                    uploadDevice()
//                }
//            })
    }
}
