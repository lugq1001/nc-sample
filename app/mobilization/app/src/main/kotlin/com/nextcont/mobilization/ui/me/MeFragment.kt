package com.nextcont.mobilization.ui.me

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.nextcont.mobilization.model.Account
import com.nextcont.mobilization.network.MobApi
import com.nextcont.mobilization.util.DialogUtil
import com.nextcont.mobilization.R
import com.nextcont.mobilization.ui.main.AboutActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class MeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_me, container, false)
//        Account.user?.let { u ->
//            view.findViewById<TextView>(R.id.iNameText).text = u.fullName
//            view.findViewById<TextView>(R.id.iAgeText).text = "${u.age} 岁"
//            view.findViewById<TextView>(R.id.iJobText).text = u.job
//            Glide.with(this)
//                .load(u.avatar)
//                .into(view.findViewById<ImageView>(R.id.iAvatar))
//        }
        val aboutView = view.findViewById<LinearLayout>(R.id.iAboutView)
        aboutView.isClickable = true
        aboutView.setOnClickListener {
            activity?.let { act ->
                act.startActivity(Intent(act, AboutActivity::class.java))
            }
        }
        val logoutView = view.findViewById<LinearLayout>(R.id.iLogoutView)
        logoutView.isClickable = true
        logoutView.setOnClickListener {
            logout()
        }
        return view
    }

    private fun logout() {
        val act = activity?: return
        act.finish()
//        val pd = ProgressDialog(act)
//        pd.show()

//        MobApi.logout(Account.deviceId)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                pd.dismiss()
//                Account.deviceId = ""
//                Account.user = null
//                act.finish()
//            }, { e ->
//                pd.dismiss()
//                DialogUtil.showAlert(act, e.localizedMessage, action = {
//
//                })
//            })
    }

    private fun disable() {
        val act = activity?: return
        DialogUtil.showAlert(act, "停用后该应用将失效。(30秒后恢复，以便测试)", action = {
            val pd = ProgressDialog(act)
            pd.show()
//            MobApi.disable(Account.deviceId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    pd.dismiss()
//                    Account.deviceId = ""
//                    Account.user = null
//                    act.finish()
//                }, { e ->
//                    pd.dismiss()
//                    DialogUtil.showAlert(act, e.localizedMessage, action = {
//
//                    })
//                })
        })
    }

}
