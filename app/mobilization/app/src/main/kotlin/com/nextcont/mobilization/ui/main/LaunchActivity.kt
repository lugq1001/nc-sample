package com.nextcont.mobilization.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R

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
}
