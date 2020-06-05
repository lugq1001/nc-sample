package com.nextcont.mobilization.ui.trains

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.nextcont.mobilization.R
import com.nextcont.mobilization.util.DialogUtil


class TrainsFragment : Fragment() {

    companion object {
        const val KEY_TRANS_TYPE = "type"
        const val TRANS_TYPE_DAILY = "daily"
        const val TRANS_TYPE_ADDITION = "addition"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trains, container, false)
        view.findViewById<RelativeLayout>(R.id.iDailyView).setOnClickListener {
            showTrans(TRANS_TYPE_DAILY)
        }
        view.findViewById<RelativeLayout>(R.id.iAdditionView).setOnClickListener {
            showTrans(TRANS_TYPE_ADDITION)
        }
        view.findViewById<RelativeLayout>(R.id.iPsychologicalView).setOnClickListener {
            activity?.let {
                DialogUtil.showAlert(it, "该模块暂未开放，敬请期待。")
            }
        }
        return view
    }

    private fun showTrans(type: String) {
        val act = activity ?: return
        val intent = Intent(act, TransActivity::class.java)
        intent.putExtra(KEY_TRANS_TYPE, type)
        startActivity(intent)
    }

}
