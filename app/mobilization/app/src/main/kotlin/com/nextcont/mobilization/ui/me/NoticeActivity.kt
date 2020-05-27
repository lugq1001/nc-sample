package com.nextcont.mobilization.ui.me

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.Notice

class NoticeActivity : AppCompatActivity() {

    private lateinit var iNoticeRecycle: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        title = getString(R.string.title_notice)


        iNoticeRecycle = findViewById(R.id.iNoticeRecycle)

        iNoticeRecycle.layoutManager = LinearLayoutManager(this)

        val adapter = NoticeAdapter()

        iNoticeRecycle.adapter = adapter
    }

    class NoticeAdapter : BaseQuickAdapter<Notice, NoticeAdapter.ViewHolder>(R.layout.item_notice, Notice.data().toMutableList()) {

        override fun convert(holder: ViewHolder, item: Notice) {
            holder.iTitleText.text = item.title
            holder.iTimeText.text = item.time
            holder.iContentText.text = item.content

            if (holder.adapterPosition == data.size - 1) {
                holder.iSpacingBottom.visibility = VISIBLE
            } else {
                holder.iSpacingBottom.visibility = GONE
            }
        }

        class ViewHolder(view: View) : BaseViewHolder(view) {

            val iTitleText: TextView = view.findViewById(R.id.iTitleText)
            val iTimeText: TextView = view.findViewById(R.id.iTimeText)
            val iContentText: TextView = view.findViewById(R.id.iContentText)
            val iSpacingBottom: View = view.findViewById(R.id.iSpacingBottom)
        }


    }


}
