package com.example.clocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import Entity.ReportRow

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    private var data: List<ReportRow> = emptyList()

    fun setData(newData: List<ReportRow>) {
        data = newData
        notifyDataSetChanged()
    }

    fun getData(): List<ReportRow> = data

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPerson: TextView = itemView.findViewById(R.id.txtPersonIdItem_recycler)
        val txtDate: TextView = itemView.findViewById(R.id.txtDateItem_recycler)
        val txtType: TextView = itemView.findViewById(R.id.txtTypeItem_recycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_clock, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = data[position]

        holder.txtPerson.text = row.personName
        holder.txtDate.text = row.date
        holder.txtType.text = row.hoursWorked ?: "-"   // No existe "tipo", así que usamos algo del ReportRow
    }
}
