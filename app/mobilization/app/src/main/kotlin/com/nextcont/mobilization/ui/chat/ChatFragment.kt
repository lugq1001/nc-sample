package com.nextcont.mobilization.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.R


class ChatFragment : Fragment() {

    private lateinit var iRecentButton: Button
    private lateinit var iContactButton: Button

    private var viewType = ViewType.RecentMessage

    private lateinit var recentView: ChatRecentFragment
    private lateinit var contactView: ChatContactFragment

    private enum class ViewType {
        RecentMessage,
        Contact
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        iRecentButton = view.findViewById(R.id.iRecentButton)
        iContactButton = view.findViewById(R.id.iContactButton)

        iRecentButton.setOnClickListener {
            toggleViewType(ViewType.RecentMessage)
        }

        iContactButton.setOnClickListener {
            toggleViewType(ViewType.Contact)
        }


        recentView = ChatRecentFragment()
        contactView = ChatContactFragment()

        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.iContentFrame, recentView)
        transaction.add(R.id.iContentFrame, contactView)
        transaction.commit()

        toggleViewType(viewType)
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

    private fun toggleViewType(viewType: ViewType) {
        val transaction = childFragmentManager.beginTransaction()
        when (viewType) {
            ViewType.RecentMessage -> {
                transaction.show(recentView)
                transaction.hide(contactView)
                iRecentButton.setTextColor(ContextCompat.getColor(MobApp.Context, R.color.text_text_btn))
                iContactButton.setTextColor(ContextCompat.getColor(MobApp.Context, R.color.text_text_btn_unsel))
            }
            ViewType.Contact -> {
                transaction.hide(recentView)
                transaction.show(contactView)
                iContactButton.setTextColor(ContextCompat.getColor(MobApp.Context, R.color.text_text_btn))
                iRecentButton.setTextColor(ContextCompat.getColor(MobApp.Context, R.color.text_text_btn_unsel))
            }
        }
        transaction.commit()

    }
}
