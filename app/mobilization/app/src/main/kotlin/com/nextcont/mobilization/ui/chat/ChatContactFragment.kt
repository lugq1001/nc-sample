package com.nextcont.mobilization.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nextcont.mobilization.R
import com.nextcont.mobilization.ui.me.MeFragment
import com.nextcont.mobilization.ui.news.NewsFragment


class ChatContactFragment : Fragment() {



    private val fragments = listOf(
        MeFragment(),
        NewsFragment()
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_contact, container, false)


        return view

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


}