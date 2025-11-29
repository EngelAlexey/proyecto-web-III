package com.example.clocker

import Entity.Attendances
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AttendanceListAdapterSimple(
    private var items: List<Attendances>
) : RecyclerView.Adapter<AttendanceListAdapterSimple.ViewHolder>() {

    val itemsList: List<Attendances>
        get() = items

    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    private val timeFmt = SimpleDateFormat("HH:mm", Locale("es", "ES"))

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtEntry: TextView = view.findViewById(R.id.txtEntry)
        val txtExit: TextView = view.findViewById(R.id.txtExit)
        val txtHours: TextView = view.findViewById(R.id.txtHours)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_attendance_list_adapter_simple, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val att = items[position]

        holder.txtDate.text = formatter.format(att.dateAttendance)
        holder.txtEntry.text = att.timeEntry?.let { timeFmt.format(it) } ?: "--"
        holder.txtExit.text = att.timeExit?.let { timeFmt.format(it) } ?: "--"

        val minutes = att.hoursAttendanceMinutes()
        holder.txtHours.text = "${minutes / 60}h ${minutes % 60}m"

        if (att.timeEntry != null && att.timeExit != null) {
            holder.itemView.setBackgroundColor(Color.parseColor("#C8E6C9")) // verde claro
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFF9C4")) // amarillo claro
        }
    }

    fun update(newList: List<Attendances>) {
        items = newList
        notifyDataSetChanged()
    }
}
