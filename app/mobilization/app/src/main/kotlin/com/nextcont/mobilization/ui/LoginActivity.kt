package com.nextcont.mobilization.ui

import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.Account
import com.nextcont.mobilization.network.MobApi
import com.nextcont.mobilization.util.DialogUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginActivity : AppCompatActivity() {

    private lateinit var iUserNameEdit: EditText
    private lateinit var iPasswordEdit: EditText
    private lateinit var iLoginButton: Button
    private lateinit var iProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        iUserNameEdit = findViewById(R.id.iUserNameEdit)
        iPasswordEdit = findViewById(R.id.iPasswordEdit)
        iLoginButton = findViewById(R.id.iLoginButton)
        iProgress = findViewById(R.id.iProgress)

        iLoginButton.setOnClickListener {
            login()
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

        iUserNameEdit.isEnabled = false
        iPasswordEdit.isEnabled = false
        iLoginButton.visibility = INVISIBLE
        iProgress.visibility = VISIBLE

        // 登录
        MobApi.login(username, password, Account.deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    Account.user = resp.user
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }, { e ->
                    iUserNameEdit.isEnabled = true
                    iPasswordEdit.isEnabled = true
                    iLoginButton.visibility = VISIBLE
                    iProgress.visibility = INVISIBLE
                    DialogUtil.showAlert(this, e.localizedMessage)
                })
    }


}
