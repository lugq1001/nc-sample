package com.nextcont.mobilization.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.chat.VMConversation
import com.nextcont.mobilization.service.DisplayTimeProvider
import com.squareup.moshi.Moshi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit


class ChatRecentFragment : Fragment() {

    private lateinit var adapter: ConversationAdapter
    private lateinit var iConversationRecycle: RecyclerView
    private val disposableBag = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_recent, container, false)

        iConversationRecycle = view.findViewById(R.id.iConversationRecycle)
        iConversationRecycle.layoutManager = LinearLayoutManager(context)

        adapter = ConversationAdapter()
        iConversationRecycle.adapter = adapter

        adapter.setOnItemClickListener { _, _, position ->
            this.adapter.getItem(position).let { con ->
                activity?.let { act ->
                    val intent = Intent(act, ChatActivity::class.java)
                    val json = Moshi.Builder().build()
                    val string = json.adapter(VMConversation::class.java).toJson(con)
                    intent.putExtra(ChatActivity.INTENT_KEY_CONVERSION, string)
                    VMConversation.conversions[position].hasUnreadMessage = false
                    startActivity(intent)
                }

            }
        }
        disposableBag.add(ChatActivity.closeMonitor.subscribeOn(AndroidSchedulers.mainThread()).subscribe {
            val cons = VMConversation.conversions
            adapter.setList(cons)
        })
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.setList(VMConversation.conversions)

    }


    inner class ConversationAdapter :
        BaseQuickAdapter<VMConversation, ConversationAdapter.RecentViewHolder>(
            R.layout.chat_item_conversation,
            mutableListOf()
        ) {

        override fun convert(holder: RecentViewHolder, item: VMConversation) {
            Glide.with(context).load(item.thumbnail).into(holder.iAvatarImage)
            holder.iNameText.text = item.name
            holder.iDescText.text = item.snippet
            holder.iTimeText.text =
                if (item.activeTime > 0) DisplayTimeProvider.conversationTime(item.activeTime) else ""
            holder.iBadgeView.visibility =
                if (item.hasUnreadMessage) View.VISIBLE else View.GONE

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
