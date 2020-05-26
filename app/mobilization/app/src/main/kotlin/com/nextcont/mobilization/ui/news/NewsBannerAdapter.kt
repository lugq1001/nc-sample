package com.nextcont.mobilization.ui.news

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.BannerNews
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder


class NewsBannerAdapter : BaseBannerAdapter<BannerNews, NewsBannerAdapter.ViewHolder>() {
    override fun onBind(
        holder: ViewHolder,
        data: BannerNews,
        position: Int,
        pageSize: Int
    ) {
        holder.bindData(data, position, pageSize)
    }

    override fun createViewHolder(itemView: View, viewType: Int): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_news_banner
    }

    class ViewHolder(itemView: View) : BaseViewHolder<BannerNews>(itemView) {

        override fun bindData(data: BannerNews, position: Int, pageSize: Int) {
            val imageView: ImageView = itemView.findViewById(R.id.iBannerImage)
            Glide.with(itemView).load(data.image).into(imageView)

            val iTitleText: TextView = itemView.findViewById(R.id.iTitleText)
            iTitleText.text = data.title
        }

    }
}
