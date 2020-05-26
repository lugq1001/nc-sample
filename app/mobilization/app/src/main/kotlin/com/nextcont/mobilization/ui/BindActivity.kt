package com.nextcont.mobilization.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.nextcont.mobilization.model.Account
import com.nextcont.mobilization.util.Util
import com.nextcont.mobilization.R

class BindActivity : AppCompatActivity() {

    private lateinit var iBindButton: Button
    private lateinit var iDeviceNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind)

        iDeviceNameText = findViewById(R.id.iDeviceNameText)
        iBindButton = findViewById(R.id.iBindButton)

        iDeviceNameText.text = if (Util.isConnectedWifi(this)) "Wifi" else "Mobile"

        iBindButton.setOnClickListener {

            val type = Account.currentNetwork(this)
            val account = Account.load()
            account?.networkType = type
            account?.login = true
            account?.save()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}
