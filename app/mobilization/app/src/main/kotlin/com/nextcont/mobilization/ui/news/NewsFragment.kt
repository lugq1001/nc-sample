package com.nextcont.mobilization.ui.news

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.BannerNews
import com.nextcont.mobilization.model.News
import com.nextcont.mobilization.ui.main.WebActivity
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.indicator.enums.IndicatorStyle


class NewsFragment : Fragment() {

    private lateinit var iBanner: BannerViewPager<BannerNews, NewsBannerAdapter.ViewHolder>

    private lateinit var itemViews: List<LinearLayout>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        iBanner = view.findViewById(R.id.iBanner)
        iBanner
            .setAutoPlay(true)
            .setScrollDuration(500)
            .setIndicatorGravity(IndicatorGravity.END)
            .setIndicatorStyle(IndicatorStyle.DASH)
            .setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            .setInterval(3000)
            .setAdapter(NewsBannerAdapter())
            .setOnPageClickListener { position ->
                val news = BannerNews.data()[position]
                val intent = Intent(activity, WebActivity::class.java)
                intent.putExtra("url", news.url)
                startActivity(intent)
            }
            .create(BannerNews.data())

        itemViews = listOf(
            view.findViewById(R.id.iNewsItem1),
            view.findViewById(R.id.iNewsItem2),
            view.findViewById(R.id.iNewsItem3),
            view.findViewById(R.id.iNewsItem4),
            view.findViewById(R.id.iNewsItem5),
            view.findViewById(R.id.iNewsItem6),
            view.findViewById(R.id.iNewsItem7),
            view.findViewById(R.id.iNewsItem8),
            view.findViewById(R.id.iNewsItem9),
            view.findViewById(R.id.iNewsItem10)
        )
        val allNews = News.data()

        for (i in itemViews.indices) {
            val itemView = itemViews[i]
            val news = allNews[i]
            itemView.findViewById<TextView>(R.id.iTitleText).text = news.title
            itemView.findViewById<TextView>(R.id.iTimeText).text = news.time
            itemView.findViewById<TextView>(R.id.iDescText).text = news.desc
            itemView.isClickable = true
            itemView.setOnClickListener {
                val intent = Intent(activity, WebActivity::class.java)
                intent.putExtra("url", news.url)
                startActivity(intent)
            }
        }

        return view
    }

}
