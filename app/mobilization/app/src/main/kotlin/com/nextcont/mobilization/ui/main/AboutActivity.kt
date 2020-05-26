package com.nextcont.mobilization.ui.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R
import com.nextcont.mobilization.util.AppUtil

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        title = getString(R.string.app_name)

        val version = "v ${AppUtil.appVersion(this)}"
        findViewById<TextView>(R.id.iVersionText).text = version
    }


}
