package com.nextcont.mobilization.ui.me

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.nextcont.mobilization.R
import timber.log.Timber


class MeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_me, container, false)
        initView(view)
        return view
    }

    private fun initView(v: View) {
//        Account.user?.let { u ->
//            view.findViewById<TextView>(R.id.iNameText).text = u.fullName
//            view.findViewById<TextView>(R.id.iAgeText).text = "${u.age} 岁"
//            view.findViewById<TextView>(R.id.iJobText).text = u.job
//            Glide.with(this)
//                .load(u.avatar)
//                .into(view.findViewById<ImageView>(R.id.iAvatar))
//        }

        initCell(v, R.id.iCheckInView, R.string.title_check_in, R.mipmap.ic_check_in) { checkIn() }
        initCell(v, R.id.iNoticeView, R.string.title_notice, R.mipmap.ic_notice) { notice() }
        initCell(v, R.id.iFeedbackView, R.string.title_feedback, R.mipmap.ic_feedback) { feedback() }
        initCell(v, R.id.iContactView, R.string.title_contact_us, R.mipmap.ic_contact_us) { contact() }
        initCell(v, R.id.iAboutView, R.string.title_about, R.mipmap.ic_about) { about() }
        initCell(v, R.id.iLogoutView, R.string.me_logout, R.mipmap.ic_logout) { logout() }
    }

    private fun initCell(v: View, @IdRes cellId: Int, @StringRes title: Int, @DrawableRes icon: Int, action: () -> Unit) {
        val cell = v.findViewById<LinearLayout>(cellId)
        val iconImage = cell.findViewById<ImageView>(R.id.iIconImage)
        val titleText = cell.findViewById<TextView>(R.id.iTitleText)
        titleText.text = getString(title)
        iconImage.setImageResource(icon)

        cell.isClickable = true
        cell.setOnClickListener {
            action()
        }
    }

    private fun checkIn() {
        Timber.i("打卡")
        activity?.let { act ->
            act.startActivity(Intent(act, CheckInActivity::class.java))
        }
    }

    private fun notice() {
        Timber.i("通知")
        activity?.let { act ->
            act.startActivity(Intent(act, NoticeActivity::class.java))
        }
    }

    private fun feedback() {
        Timber.i("反馈")
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "ye.liu@nextcont.com", null))
        intent.putExtra(Intent.EXTRA_SUBJECT, "国防动员 问题反馈")
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }

    private fun contact() {
        Timber.i("联系我们")
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:021-62894215")))
    }

    private fun about() {
        Timber.i("关于")
        activity?.let { act ->
            act.startActivity(Intent(act, AboutActivity::class.java))
        }
    }

    private fun logout() {
        Timber.i("退出登录")
        val act = activity?: return
        act.finish()
    }

}
