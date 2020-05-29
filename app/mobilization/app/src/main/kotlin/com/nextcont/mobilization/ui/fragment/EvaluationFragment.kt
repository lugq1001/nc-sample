package com.nextcont.mobilization.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextcont.mobilization.model.Evaluation
import com.nextcont.mobilization.network.MobApi
import com.nextcont.mobilization.util.DialogUtil
import com.nextcont.mobilization.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class EvaluationFragment : Fragment() {

    private lateinit var iRecycler: RecyclerView
    private lateinit var iProgress: ProgressBar

    private var iAdapter: EvaluationAdapter = EvaluationAdapter()
    private var evaluations = mutableListOf<Evaluation>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
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
        iRecycler.visibility = GONE
        iProgress.visibility = VISIBLE
        // 登录
//        MobApi.evaluations()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ resp ->
//                evaluations = resp.evaluations.toMutableList()
//                iRecycler.visibility = VISIBLE
//                iProgress.visibility = GONE
//
//                iAdapter.evaluations = evaluations
//                iAdapter.notifyDataSetChanged()
//
//            }, { e ->
//                DialogUtil.showAlert(act, e.localizedMessage, action = {
//                    fetchData()
//                })
//            })
    }

    private fun modifyData(eva: Evaluation) {
        val act = activity ?: return
        val dialog: AlertDialog.Builder = AlertDialog.Builder(act)
        dialog.setTitle("${eva.name}")
        dialog.setMessage("输入修改后的评分")
        val input = EditText(act)
        input.gravity = Gravity.CENTER
        input.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(eva.score)
        dialog.setView(input)

        dialog.setPositiveButton("修改"
        ) { _, _ -> //When OK button is clicked, an int with value of 1 will be saved in sharedPreferences.
            evaluations.forEachIndexed { index, evaluation ->
                if (evaluation.id == eva.id) {
                    evaluations[index].score = input.text.toString()
                    iAdapter.evaluations = evaluations
                    iAdapter.notifyDataSetChanged()
                }
            }

        }
        dialog.setNegativeButton("取消"
        ) { d, _ -> //Second Edit: To open another acitivty on No Thanks Button
            d.dismiss()
        }
        dialog.show()
    }

    fun updateList(showEdit: Boolean) {
        iAdapter.showEdit = showEdit
        iAdapter.notifyDataSetChanged()
    }

    // ① 创建Adapter
    inner class EvaluationAdapter : RecyclerView.Adapter<EvaluationAdapter.ViewHolder>() {

        var evaluations: List<Evaluation> = emptyList()
        var showEdit = true

        //② 创建ViewHolder
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val iTitleText: TextView = v.findViewById(R.id.iTitleText)
            val iUserText: TextView = v.findViewById(R.id.iUserText)
            val iTimeText: TextView = v.findViewById(R.id.iTimeText)
            val iScoreText: TextView = v.findViewById(R.id.iScoreText)
            val iEditButton: Button = v.findViewById(R.id.iEditButton)
        }

        //③ 在Adapter中实现3个方法
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val eva = evaluations[position]
            val title = "考核项目: ${eva.title} (${eva.no})"
            holder.iTitleText.text = title
            val desc = "${eva.name} ${eva.genderString} 年龄:${eva.age}"
            holder.iUserText.text = desc
            val time = "时间: ${eva.createAt}"
            holder.iTimeText.text = time
            val score = "评分: ${eva.score}"
            holder.iScoreText.text = score

            if (showEdit) {
                holder.iEditButton.visibility = VISIBLE
            } else {
                holder.iEditButton.visibility = GONE
            }

            holder.iEditButton.setOnClickListener {
                modifyData(eva)
            }
        }

        override fun getItemCount(): Int {
            return evaluations.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_evaluation, parent, false)
            return ViewHolder(v)
        }


    }
}
