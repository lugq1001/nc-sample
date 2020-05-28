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
import com.nextcont.mobilization.model.CheckIn
import com.nextcont.mobilization.model.Notice

class CheckInRecordActivity : AppCompatActivity() {

    private lateinit var iCheckInRecycle: RecyclerView
    private lateinit var iNoDataText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_record)
        title = getString(R.string.title_check_in_record)

        iNoDataText = findViewById(R.id.iNoDataText)

        iCheckInRecycle = findViewById(R.id.iCheckInRecycle)
        iCheckInRecycle.layoutManager = LinearLayoutManager(this)
        val adapter = CheckInRecordAdapter()
        iCheckInRecycle.adapter = adapter

        val record = CheckIn.load().toMutableList()
        if (record.isEmpty()) {
            iNoDataText.visibility = VISIBLE
            iCheckInRecycle.visibility = GONE
        } else {
            iNoDataText.visibility = GONE
            iCheckInRecycle.visibility = VISIBLE
            adapter.setNewInstance(record)
        }
    }

    class CheckInRecordAdapter : BaseQuickAdapter<CheckIn, CheckInRecordAdapter.ViewHolder>(R.layout.item_check_in_record, mutableListOf()) {

        override fun convert(holder: ViewHolder, item: CheckIn) {
            holder.iAddressText.text = item.address
            holder.iTimeText.text = item.displayTime
        }

        class ViewHolder(view: View) : BaseViewHolder(view) {

            val iAddressText: TextView = view.findViewById(R.id.iAddressText)
            val iTimeText: TextView = view.findViewById(R.id.iTimeText)

        }


    }


}
