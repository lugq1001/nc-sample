package com.nextcont.mobilization.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nextcont.mobilization.R
import com.nextcont.mobilization.ui.assess.AssessFragment
import com.nextcont.mobilization.ui.chat.ChatFragment
import com.nextcont.mobilization.ui.me.MeFragment
import com.nextcont.mobilization.ui.news.NewsFragment
import com.nextcont.mobilization.ui.trains.TrainsFragment
import com.nextcont.mobilization.util.ImageUtil


class MainActivity : AppCompatActivity() {

    private lateinit var iTab: TabLayout
    private lateinit var iViewPager: ViewPager


    private val fragments = listOf(
        NewsFragment(),
        TrainsFragment(),
        ChatFragment(),
        AssessFragment(),
        MeFragment()
    )

    private val icons = listOf(
        R.mipmap.ic_news,
        R.mipmap.ic_trains,
        R.mipmap.ic_msg,
        R.mipmap.ic_examine,
        R.mipmap.ic_user
    )

    private val titles = listOf(
        "资讯",
        "训练",
        "通讯",
        "考核",
        "我"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        iTab = findViewById(R.id.iTab)

        val demoCollectionPagerAdapter = DemoCollectionPagerAdapter(supportFragmentManager)
        iViewPager = findViewById(R.id.iViewPager)
        iViewPager.adapter = demoCollectionPagerAdapter

        iTab.setupWithViewPager(iViewPager)

        for (i in 0 until iTab.tabCount) {
            val d = ImageUtil.tintDrawable(this, icons[i], R.color.primary)
            iTab.getTabAt(i)?.icon = d
        }





    }


    override fun onBackPressed() {
        //super.onBackPressed()
    }

    inner class DemoCollectionPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int  = fragments.size

        override fun getItem(i: Int): Fragment {
            return fragments[i]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }


    }

}
