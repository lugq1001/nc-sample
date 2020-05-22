package com.nextcont.mobilization.ui.main

import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var iUsernameEdit: EditText
    private lateinit var iPasswordEdit: EditText
    private lateinit var iPassword2Edit: EditText
    private lateinit var iFullNameEdit: EditText
    private lateinit var iIdCardEdit: EditText

    private lateinit var iBirthdayButton: Button
    private lateinit var iRoleButton: Button
    private lateinit var iGenderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = getString(R.string.title_register)
        initView()

    }

    private fun initView() {
        val iUserNameRow = findViewById<LinearLayout>(R.id.iUserNameRow)
        iUserNameRow.findViewById<TextView>(R.id.iTitleText).text = getString(R.string.login_username)
        iUsernameEdit = iUserNameRow.findViewById(R.id.iInputEdit)
        iUsernameEdit.hint = getString(R.string.register_username_hint)
        iUsernameEdit.keyListener = DigitsKeyListener.getInstance("abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")

        val iPasswordRow = findViewById<LinearLayout>(R.id.iPasswordRow)
        iPasswordRow.findViewById<TextView>(R.id.iTitleText).text = getString(R.string.login_password)
        iPasswordEdit = iPasswordRow.findViewById(R.id.iInputEdit)
        iPasswordEdit.hint = getString(R.string.register_password_hint)
        iPasswordEdit.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        iPasswordEdit.keyListener = DigitsKeyListener.getInstance("abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")

        val iPassword2Row = findViewById<LinearLayout>(R.id.iPassword2Row)
        iPassword2Row.findViewById<TextView>(R.id.iTitleText).text = ""
        iPassword2Edit = iPassword2Row.findViewById(R.id.iInputEdit)
        iPassword2Edit.hint = getString(R.string.register_password_confirm)
        iPassword2Edit.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        iPassword2Edit.keyListener = DigitsKeyListener.getInstance("abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")


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
        iBirthdayButton.text = "1990-01-01"

        // 性别
        val iGenderRow = findViewById<LinearLayout>(R.id.iGenderRow)
        iGenderRow.findViewById<TextView>(R.id.iTitleText).text = "性别"
        iGenderButton = iGenderRow.findViewById(R.id.iSelectButton)
        iGenderButton.text = "男"

        // 角色
        val iRoleRow = findViewById<LinearLayout>(R.id.iRoleRow)
        iRoleRow.findViewById<TextView>(R.id.iTitleText).text = "军职"
        iRoleButton = iRoleRow.findViewById(R.id.iSelectButton)
        iRoleButton.text = "士兵"

    }

}
