package com.nextcont.mobilization.ui.main

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R

class WebActivity : AppCompatActivity() {

    private lateinit var iWebView: WebView
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        supportActionBar?.hide()

        iWebView = findViewById(R.id.iWebView)

        url = intent.getStringExtra("url") ?: ""

        iWebView.loadUrl(url)

    }

}
