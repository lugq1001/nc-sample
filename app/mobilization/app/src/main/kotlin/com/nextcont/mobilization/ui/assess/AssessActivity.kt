package com.nextcont.mobilization.ui.assess

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.Question
import com.nextcont.mobilization.util.AppUtil
import com.nextcont.mobilization.util.DialogUtil

class AssessActivity : AppCompatActivity() {

    private val questions = Question.data()

    private lateinit var iAButton: Button
    private lateinit var iBButton: Button
    private lateinit var iCButton: Button
    private lateinit var iDButton: Button

    private lateinit var iIndexText: TextView
    private lateinit var iTitleText: TextView

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assess)
        supportActionBar?.hide()

        iIndexText = findViewById(R.id.iIndexText)
        iTitleText = findViewById(R.id.iTitleText)

        iAButton = findViewById(R.id.iAButton)
        iBButton = findViewById(R.id.iBButton)
        iCButton = findViewById(R.id.iCButton)
        iDButton = findViewById(R.id.iDButton)

        iAButton.setOnClickListener {
            answer(0)
        }
        iBButton.setOnClickListener {
            answer(1)
        }
        iCButton.setOnClickListener {
            answer(2)
        }
        iDButton.setOnClickListener {
            answer(3)
        }

        refreshQuestion()
    }

    private fun answer(tag: Int) {
        if (currentIndex == questions.size - 1) {
            DialogUtil.showAlert(this, "考核完成。") {
                finish()
            }
            return
        }
        currentIndex ++
        refreshQuestion()
    }

    private fun refreshQuestion() {
        val tips = "${currentIndex + 1} / ${questions.size}"
        iIndexText.text = tips
        val question = questions[currentIndex]
        iTitleText.text = question.title
        iAButton.text = "A  ${question.items[0]}"
        iBButton.text = "B  ${question.items[1]}"
        iCButton.text = "C  ${question.items[2]}"
        iDButton.text = "D  ${question.items[3]}"
    }

}
