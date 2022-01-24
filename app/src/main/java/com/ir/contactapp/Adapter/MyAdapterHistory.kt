package com.ir.contactapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ir.contactapp.HistoryData
import com.ir.contactapp.R
import kotlinx.android.synthetic.main.item_history_from1.view.*

class MyAdapterHistory(val context: Context, val arrayListHistory: ArrayList<HistoryData>) :
    RecyclerView.Adapter<MyAdapterHistory.VH>() {

    inner class VH(var itemRv: View) : RecyclerView.ViewHolder(itemRv) {
        fun onBind(historyData: HistoryData) {
            itemRv.tv_call_history_date.text = historyData.history
            itemRv.tv_call_history_number.text = historyData.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_history_from1, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(arrayListHistory[position])
    }

    override fun getItemCount(): Int = arrayListHistory.size
}