package com.nextcont.mobilization.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.CheckIn
import com.nextcont.mobilization.model.chat.VMContact
import com.nextcont.mobilization.model.chat.VMConversation
import com.nextcont.mobilization.service.ImageProvider
import com.nextcont.mobilization.ui.me.CheckInRecordActivity
import com.nextcont.mobilization.ui.me.MeFragment
import com.nextcont.mobilization.ui.news.NewsFragment
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.fragment_chat_contact.*
import timber.log.Timber


class ChatContactFragment : Fragment() {

    private lateinit var iFirstButton: Button
    private lateinit var iSecondButton: Button
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_contact, container, false)
        val iContactRecycle = view.findViewById<RecyclerView>(R.id.iContactRecycle)
        iContactRecycle.layoutManager = LinearLayoutManager(context)
        adapter = ContactAdapter()
        iContactRecycle.adapter = adapter

        adapter.setOnItemClickListener { _, _, position ->
            this.adapter.getItem(position).let {
                // TODO 进入聊天
                startChat(it)
            }
        }

        iFirstButton = view.findViewById(R.id.iFirstButton)
        iSecondButton = view.findViewById(R.id.iSecondButton)

        iFirstButton.setOnClickListener {
            toggleViewType(true)
        }

        iSecondButton.setOnClickListener {
            toggleViewType(false)
        }



        toggleViewType(true)

        return view
    }

    private fun toggleViewType(first: Boolean) {
        val contacts = VMContact.testData(first)
        if (first) {
            iFirstButton.setTextColor(ContextCompat.getColor(MobApp.Context, R.color.text_text_btn))
            iSecondButton.setTextColor(
                ContextCompat.getColor(
                    MobApp.Context,
                    R.color.text_text_btn_unsel
                )
            )
        } else {
            iFirstButton.setTextColor(
                ContextCompat.getColor(
                    MobApp.Context,
                    R.color.text_text_btn_unsel
                )
            )
            iSecondButton.setTextColor(
                ContextCompat.getColor(
                    MobApp.Context,
                    R.color.text_text_btn
                )
            )
        }
        adapter.setNewInstance(contacts)
    }

    private fun startChat(contact: VMContact) {
        Timber.i("$contact")
        val act = activity ?: return
        val intent = Intent(act, ChatActivity::class.java)
        val json = Moshi.Builder().build()
        val string = json.adapter(VMConversation::class.java).toJson(VMConversation.create(contact))
        intent.putExtra(ChatActivity.INTENT_KEY_CONVERSION, string)
        startActivity(intent)
    }

    class ContactAdapter : BaseQuickAdapter<VMContact, ContactAdapter.ViewHolder>(
        R.layout.item_contact,
        mutableListOf()
    ) {

        override fun convert(holder: ViewHolder, item: VMContact) {
            ImageProvider.loadAvatar(
                context,
                item.avatar,
                holder.iAvatarImage
            )

            holder.iNameText.text = item.nickname
            holder.iRoleText.text = item.role
        }

        class ViewHolder(view: View) : BaseViewHolder(view) {

            val iAvatarImage: ImageView = view.findViewById(R.id.iAvatarImage)
            val iNameText: TextView = view.findViewById(R.id.iNameText)
            val iRoleText: TextView = view.findViewById(R.id.iRoleText)

        }


    }
}
