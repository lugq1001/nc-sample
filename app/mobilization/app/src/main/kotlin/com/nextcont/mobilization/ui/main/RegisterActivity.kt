package com.nextcont.mobilization.ui.main

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.network.MobApi
import com.nextcont.mobilization.network.request.RegisterRequest
import com.nextcont.mobilization.util.DialogUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var iUsernameEdit: EditText
    private lateinit var iPasswordEdit: EditText
    private lateinit var iPassword2Edit: EditText
    private lateinit var iFullNameEdit: EditText
    private lateinit var iIdCardEdit: EditText

    private lateinit var iBirthdayButton: Button
    private lateinit var iRoleButton: Button
    private lateinit var iGenderButton: Button

    private lateinit var iRegisterButton: Button
    private lateinit var iProgress: ProgressBar

    private var gender = User.Gender.Male
    private var role = User.Role.Sergeant
    private lateinit var birthday: Calendar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = getString(R.string.title_register)

        birthday = Calendar.getInstance()
        birthday.set(1990, 0, 1)

        initView()
    }

    private fun initView() {
        val iUserNameRow = findViewById<LinearLayout>(R.id.iUserNameRow)
        iUserNameRow.findViewById<TextView>(R.id.iTitleText).text = getString(R.string.login_username)
        iUsernameEdit = iUserNameRow.findViewById(R.id.iInputEdit)
        iUsernameEdit.hint = getString(R.string.register_username_hint)

        val iPasswordRow = findViewById<LinearLayout>(R.id.iPasswordRow)
        iPasswordRow.findViewById<TextView>(R.id.iTitleText).text = getString(R.string.login_password)
        iPasswordEdit = iPasswordRow.findViewById(R.id.iInputEdit)
        iPasswordEdit.hint = getString(R.string.register_password_hint)
        iPasswordEdit.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT

        val iPassword2Row = findViewById<LinearLayout>(R.id.iPassword2Row)
        iPassword2Row.findViewById<TextView>(R.id.iTitleText).text = ""
        iPassword2Edit = iPassword2Row.findViewById(R.id.iInputEdit)
        iPassword2Edit.hint = getString(R.string.register_password_confirm)
        iPassword2Edit.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT


        val iFullNameRow = findViewById<LinearLayout>(R.id.iFullNameRow)
        iFullNameRow.findViewById<TextView>(R.id.iTitleText).text = getString(R.string.register_full_name)
        iFullNameEdit = iFullNameRow.findViewById(R.id.iInputEdit)
        iFullNameEdit.hint = ""

        val iIdCardRow = findViewById<LinearLayout>(R.id.iIdCardRow)
        iIdCardRow.findViewById<TextView>(R.id.iTitleText).text = getString(R.string.register_id_card)
        iIdCardEdit = iIdCardRow.findViewById(R.id.iInputEdit)
        iIdCardEdit.hint = ""

        // 生日
        val iBirthdayRow = findViewById<LinearLayout>(R.id.iBirthdayRow)
        iBirthdayRow.findViewById<TextView>(R.id.iTitleText).text = "生日"
        iBirthdayButton = iBirthdayRow.findViewById(R.id.iSelectButton)
        iBirthdayButton.text = birthdayString()
        iBirthdayButton.setOnClickListener {
            chooseBirthday()
        }

        // 性别
        val iGenderRow = findViewById<LinearLayout>(R.id.iGenderRow)
        iGenderRow.findViewById<TextView>(R.id.iTitleText).text = "性别"
        iGenderButton = iGenderRow.findViewById(R.id.iSelectButton)
        iGenderButton.text = gender.string
        iGenderButton.setOnClickListener {
            chooseGender()
        }

        // 角色
        val iRoleRow = findViewById<LinearLayout>(R.id.iRoleRow)
        iRoleRow.findViewById<TextView>(R.id.iTitleText).text = "军职"
        iRoleButton = iRoleRow.findViewById(R.id.iSelectButton)
        iRoleButton.text = role.string
        iRoleButton.setOnClickListener {
            chooseRole()
        }

        iRegisterButton = findViewById(R.id.iRegisterButton)
        iRegisterButton.setOnClickListener {
            register()
        }

        iProgress = findViewById(R.id.iProgress)
    }

    private fun chooseBirthday() {
        DialogUtil.showDatePicker(this, birthday) {
            birthday = it
            iBirthdayButton.text = birthdayString()
        }
    }

    private fun chooseGender() {
        DialogUtil.showChoice(this, User.Gender.values().map { it.string }) { index ->
            gender = User.Gender.values()[index]
            iGenderButton.text = gender.string
        }
    }

    private fun chooseRole() {
        DialogUtil.showChoice(this, User.Role.values().map { it.string }) { index ->
            role = User.Role.values()[index]
            iRoleButton.text = role.string
        }
    }

    private fun birthdayString(): String {
        return "${birthday.get(Calendar.YEAR)}年${birthday.get(Calendar.MONTH) + 1}月${birthday.get(Calendar.DAY_OF_MONTH)}日"
    }

    private fun register() {
        val username = iUsernameEdit.text.toString()
        val password = iPasswordEdit.text.toString()
        val password2 = iPassword2Edit.text.toString()
        val fullName = iFullNameEdit.text.toString()
        val idCard = iIdCardEdit.text.toString()
        val birthday = birthdayString()
        val gender = gender.code
        val role = role.code

        if (password != password2) {
            DialogUtil.showAlert(this, "两次密码不一致")
            return
        }

        enableUI(false)

        val req = RegisterRequest(username, password, fullName, gender, birthday, idCard, role)

        MobApi.register(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                enableUI(true)
                DialogUtil.showAlert(this, "注册成功", action = {
                    finish()
                })

            }, { e ->
                enableUI(true)
                DialogUtil.showAlert(this, e.localizedMessage, action = {

                })
            })

    }

    private fun enableUI(enable: Boolean) {
        iUsernameEdit.isEnabled = enable
        iPasswordEdit.isEnabled = enable
        iPassword2Edit.isEnabled = enable
        iFullNameEdit.isEnabled = enable
        iIdCardEdit.isEnabled = enable
        iBirthdayButton.isEnabled = enable
        iGenderButton.isEnabled = enable
        iRoleButton.isEnabled = enable
        iRegisterButton.isEnabled = enable
        if (enable) {
            iProgress.visibility = View.INVISIBLE
            iRegisterButton.visibility = View.VISIBLE
        } else {
            iProgress.visibility = View.VISIBLE
            iRegisterButton.visibility = View.INVISIBLE
        }
    }

}
