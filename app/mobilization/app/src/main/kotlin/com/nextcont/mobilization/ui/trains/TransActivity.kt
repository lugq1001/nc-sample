package com.nextcont.mobilization.ui.trains

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.Training
import com.nextcont.mobilization.ui.trains.TrainingActivity.Companion.KEY_TRAIN
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import timber.log.Timber


class TransActivity : AppCompatActivity() {

    private lateinit var iTransRecycle: RecyclerView
    private lateinit var iNoDataText: TextView
    private lateinit var trainings: List<Training>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans)

        when (intent.getStringExtra(TrainsFragment.KEY_TRANS_TYPE)) {
            TrainsFragment.TRANS_TYPE_DAILY -> {
                title = "日常训练"
                trainings = Training.daily()
            }
            TrainsFragment.TRANS_TYPE_ADDITION -> {
                title = "附加训练"
                trainings = Training.addition()
            }
        }

        iNoDataText = findViewById(R.id.iNoDataText)

        iTransRecycle = findViewById(R.id.iTransRecycle)
        iTransRecycle.layoutManager = LinearLayoutManager(this)
        val adapter = TransAdapter()
        iTransRecycle.adapter = adapter

        if (trainings.isEmpty()) {
            iNoDataText.visibility = VISIBLE
            iTransRecycle.visibility = GONE
        } else {
            iNoDataText.visibility = GONE
            iTransRecycle.visibility = VISIBLE
            adapter.setNewInstance(trainings.toMutableList())
        }
    }

    private fun startTraining(position: Int) {
        val train = trainings[position]
        Timber.i("开始训练: $train")
        val intent = Intent(this, TrainingActivity::class.java)

        val json = Moshi.Builder().build()
        val jsonAdapter = json.adapter(Training::class.java)
        intent.putExtra(KEY_TRAIN, jsonAdapter.toJson(train))
        startActivity(intent)
    }

    inner class TransAdapter : BaseQuickAdapter<Training, TransAdapter.ViewHolder>(R.layout.item_trans, mutableListOf()) {

        override fun convert(holder: ViewHolder, item: Training) {
            holder.iTitleText.text = item.title
            holder.iTimeText.text = item.time
            holder.iLocationText.text = item.location
            holder.iRemarkText.text = item.remark
            holder.iIconImage.setImageDrawable(ContextCompat.getDrawable(context, item.icon))
            holder.iTimeText.text = item.time
            if (holder.adapterPosition == data.size - 1) {
                holder.iSpacingBottom.visibility = VISIBLE
            } else {
                holder.iSpacingBottom.visibility = GONE
            }
            holder.iStartButton.setOnClickListener {
                startTraining(holder.adapterPosition)
            }
        }

        inner class ViewHolder(view: View) : BaseViewHolder(view) {

            val iTitleText: TextView = view.findViewById(R.id.iTitleText)
            val iTimeText: TextView = view.findViewById(R.id.iTimeText)
            val iLocationText: TextView = view.findViewById(R.id.iLocationText)
            val iRemarkText: TextView = view.findViewById(R.id.iRemarkText)
            val iIconImage: ImageView = view.findViewById(R.id.iIconImage)
            val iStartButton: Button = view.findViewById(R.id.iStartButton)
            val iSpacingBottom: View = view.findViewById(R.id.iSpacingBottom)
        }


    }


}
