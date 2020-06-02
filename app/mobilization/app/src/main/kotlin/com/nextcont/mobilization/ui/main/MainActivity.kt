package com.nextcont.mobilization.ui.main

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nextcont.mobilization.R
import com.nextcont.mobilization.service.LocationService
import com.nextcont.mobilization.ui.assess.AssessFragment
import com.nextcont.mobilization.ui.chat.ChatFragment
import com.nextcont.mobilization.ui.me.MeFragment
import com.nextcont.mobilization.ui.news.NewsFragment
import com.nextcont.mobilization.ui.trains.TrainsFragment
import com.nextcont.mobilization.util.ImageUtil


class MainActivity : AppCompatActivity() {

    private lateinit var iTab: TabLayout
    private lateinit var iViewPager: ViewPager

    companion object {
        const val REQ_CODE_LOCATION = 100
    }

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
        R.string.main_tab_news,
        R.string.main_tab_trains,
        R.string.main_tab_chat,
        R.string.main_tab_assess,
        R.string.main_tab_me
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

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            LocationService.start()
        } else {
            // 权限申请
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), REQ_CODE_LOCATION)
        }
    }

    override fun onStop() {
        super.onStop()
        LocationService.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationService.destroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
            if (requestCode == REQ_CODE_LOCATION) {
                LocationService.start()
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    inner class DemoCollectionPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int  = fragments.size

        override fun getItem(i: Int): Fragment {
            return fragments[i]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return getString(titles[position])
        }


    }

}
