package com.nextcont.mobilization.ui.assess

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.nextcont.mobilization.R


class AssessFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_assess, container, false)

        val button = view.findViewById<Button>(R.id.iStartButton)
        button.setOnClickListener {
            startAssess()
        }
        return view
    }

    private fun startAssess() {
        startActivity(Intent(activity, AssessActivity::class.java))
    }

}
