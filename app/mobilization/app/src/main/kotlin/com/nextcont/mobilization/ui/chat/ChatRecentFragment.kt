package com.nextcont.mobilization.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.chat.VMConversation
import com.nextcont.mobilization.model.chat.VMConversationExtra
import com.nextcont.mobilization.service.DisplayTimeProvider
import com.nextcont.mobilization.service.ImageProvider
import java.util.*


class ChatRecentFragment : Fragment() {

    private lateinit var adapter: ConversationAdapter
    private lateinit var iConversationRecycle: RecyclerView
    private lateinit var iProgress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_recent, container, false)

        iProgress = view.findViewById(R.id.iProgress)
        iConversationRecycle = view.findViewById(R.id.iConversationRecycle)
        iConversationRecycle.layoutManager = LinearLayoutManager(context)

        adapter = ConversationAdapter()
        iConversationRecycle.adapter = adapter

        adapter.setOnItemClickListener { _, _, position ->
            this.adapter.getItem(position).let {
                // TODO 进入聊天
                //startChat(it.sid)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.setNewInstance(testData().toMutableList())
        //initData()
    }

    private fun testData(): List<VMConversation> {
        val c = VMConversation()
        c.localId = UUID.randomUUID().toString()
        c.sid = "5"
        c.name = "聊天"
        var extraInfo = VMConversationExtra()

        return listOf(
            c
        )
    }

    inner class ConversationAdapter :
        BaseQuickAdapter<VMConversation, ConversationAdapter.RecentViewHolder>(
            R.layout.chat_item_conversation,
            mutableListOf()
        ) {

        override fun convert(holder: RecentViewHolder, item: VMConversation) {
            when (item.chatType) {
                VMConversation.ChatType.Group -> {
                    ImageProvider.loadAvatar(
                        context,
                        R.mipmap.nc_img_chat_avatar_group,
                        holder.iAvatarImage,
                        R.dimen.chat_avatar_corner
                    )
                }
                VMConversation.ChatType.Personal -> {
                    if (item.extraInfo.thumbnail.isEmpty()) {
                        ImageProvider.loadAvatar(
                            context,
                            R.mipmap.nc_img_chat_avatar,
                            holder.iAvatarImage,
                            R.dimen.chat_avatar_corner
                        )
                    } else {
                        ImageProvider.loadAvatar(
                            context,
                            ImageProvider.makeProxyUrl(item.extraInfo.thumbnail),
                            holder.iAvatarImage,
                            R.mipmap.nc_img_chat_avatar,
                            R.dimen.chat_avatar_corner
                        )
                    }
                }
            }
            holder.iNameText.text = item.extraInfo.displayName
            holder.iDescText.text = item.extraInfo.snippet
            holder.iTimeText.text =
                if (item.extraInfo.activeTime > 0) DisplayTimeProvider.conversationTime(item.extraInfo.activeTime) else ""
            holder.iBadgeView.visibility =
                if (item.extraInfo.hasUnreadMessage) View.VISIBLE else View.GONE

            holder.iSpacingView.visibility =
                if (holder.adapterPosition == data.count() - 1) View.VISIBLE else View.GONE
        }

        inner class RecentViewHolder(view: View) : BaseViewHolder(view) {

            val iNameText: TextView = view.findViewById(R.id.iNameText)
            val iTimeText: TextView = view.findViewById(R.id.iTimeText)
            val iDescText: TextView = view.findViewById(R.id.iDescText)
            val iAvatarImage: ImageView = view.findViewById(R.id.iAvatarImage)
            val iSpacingView: View = view.findViewById(R.id.iSpacingView)
            val iBadgeView: View = view.findViewById(R.id.iBadgeView)

        }

    }

}
