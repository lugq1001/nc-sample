package com.nextcont.mobilization.ui.fragment

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
import com.bumptech.glide.Glide
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.network.MobApi
import com.nextcont.mobilization.util.DialogUtil
import com.nextcont.mobilization.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class UsersFragment : Fragment() {

    private lateinit var iRecycler: RecyclerView
    private lateinit var iProgress: ProgressBar

    private var iAdapter: EvaluationAdapter = EvaluationAdapter()
    private var users = mutableListOf<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trains, container, false)
        //iRecycler = view.findViewById(R.id.iRecycler)
        iProgress = view.findViewById(R.id.iProgress)

        activity?.let { act ->
            val layoutManager = LinearLayoutManager(act)
            layoutManager.orientation = RecyclerView.VERTICAL
            iRecycler.layoutManager = layoutManager
            iRecycler.adapter = iAdapter
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchData()

    }

    private fun fetchData() {
        val act = activity ?: return
        iRecycler.visibility = View.GONE
        iProgress.visibility = View.VISIBLE
        // 登录
//        MobApi.users()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ resp ->
//                users = resp.users.toMutableList()
//                iRecycler.visibility = View.VISIBLE
//                iProgress.visibility = View.GONE
//
//                iAdapter.users = users
//                iAdapter.notifyDataSetChanged()
//
//            }, { e ->
//                DialogUtil.showAlert(act, e.localizedMessage, action = {
//                    fetchData()
//                })
//            })
    }

    // ① 创建Adapter
    inner class EvaluationAdapter : RecyclerView.Adapter<EvaluationAdapter.ViewHolder>() {

        var users: List<User> = emptyList()

        //② 创建ViewHolder
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val iAvatar: ImageView = v.findViewById(R.id.iAvatar)
            val iNameText: TextView = v.findViewById(R.id.iNameText)
            val iJobText: TextView = v.findViewById(R.id.iJobText)
        }

        //③ 在Adapter中实现3个方法
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user = users[position]
            val title = "${user.fullName} 年龄: ${user.age}"
            holder.iNameText.text = title
            val desc = "${user.job}"
            holder.iJobText.text = desc
            Glide.with(this@UsersFragment)
                .load(user.avatar)
                .into(holder.iAvatar)

        }

        override fun getItemCount(): Int {
            return users.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            return ViewHolder(v)
        }


    }

}
