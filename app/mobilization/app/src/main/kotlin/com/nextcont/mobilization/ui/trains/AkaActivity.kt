package com.nextcont.mobilization.ui.trains

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R


class AkaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aka)
        title = "心理评估"

        findViewById<Button>(R.id.iStartButton).setOnClickListener {
            startActivity(Intent(this, AkaTestActivity::class.java))
        }
    }


}
