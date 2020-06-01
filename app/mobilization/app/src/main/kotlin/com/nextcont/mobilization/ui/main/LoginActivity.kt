package com.nextcont.mobilization.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.network.MobApi
import com.nextcont.mobilization.network.request.LoginRequest
import com.nextcont.mobilization.util.AppUtil
import com.nextcont.mobilization.util.DialogUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginActivity : AppCompatActivity() {

    private lateinit var iUserNameEdit: EditText
    private lateinit var iPasswordEdit: EditText
    private lateinit var iLoginButton: Button
    private lateinit var iRegisterButton: Button
    private lateinit var iProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val version = "v ${AppUtil.appVersion(this)}"
        findViewById<TextView>(R.id.iVersionText).text = version

        iUserNameEdit = findViewById(R.id.iUserNameEdit)
        iPasswordEdit = findViewById(R.id.iPasswordEdit)
        iLoginButton = findViewById(R.id.iLoginButton)
        iRegisterButton = findViewById(R.id.iRegisterButton)
        iProgress = findViewById(R.id.iProgress)

        iLoginButton.setOnClickListener {
            login()
        }

        iRegisterButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        User.load()?.let {
            iUserNameEdit.setText(it.username)
            iPasswordEdit.setText(it.password)
        }

    }

    private fun login() {
        val username = iUserNameEdit.text.toString()
        val password = iPasswordEdit.text.toString()
        if (username.isEmpty()) {
            DialogUtil.showAlert(this, getString(R.string.login_username_empty))
            return
        }
        if (password.isEmpty()) {
            DialogUtil.showAlert(this, getString(R.string.login_password_empty))
            return
        }

        enableUI(false)

        // 登录
        val req = LoginRequest(username, password)
        MobApi.login(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                val user = User.convert(resp)
                user.save()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, { e ->
                enableUI(true)
                DialogUtil.showAlert(this, e.localizedMessage)
            })
    }

    private fun enableUI(enable: Boolean) {
        iUserNameEdit.isEnabled = enable
        iPasswordEdit.isEnabled = enable
        iLoginButton.isEnabled = enable
        iRegisterButton.isEnabled = enable
        if (enable) {
            iProgress.visibility = INVISIBLE
            iLoginButton.visibility = VISIBLE
            iRegisterButton.visibility = VISIBLE
        } else {
            iProgress.visibility = VISIBLE
            iLoginButton.visibility = INVISIBLE
            iRegisterButton.visibility = INVISIBLE
        }
    }

}
