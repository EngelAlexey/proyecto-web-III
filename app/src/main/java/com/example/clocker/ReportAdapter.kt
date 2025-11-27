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
        val txtPerson: TextView = itemView.findViewById(R.id.txtPerson)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtEntry: TextView = itemView.findViewById(R.id.txtEntry)
        val txtExit: TextView = itemView.findViewById(R.id.txtExit)
        val txtHours: TextView = itemView.findViewById(R.id.txtHours)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = data[position]

        holder.txtPerson.text = row.personName
        holder.txtDate.text = row.date
        holder.txtEntry.text = row.timeEntry ?: "-"
        holder.txtExit.text = row.timeExit ?: "-"
        holder.txtHours.text = row.hoursWorked ?: "-"
    }
}
