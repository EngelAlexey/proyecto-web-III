package com.example.clocker

import Entity.Attendances
import Entity.Person
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AttendanceListAdapterSimple(
    var itemsList: List<Attendances>,
    private val persons: List<Person>
) : RecyclerView.Adapter<AttendanceListAdapterSimple.ViewHolder>() {

    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    private val timeFmt = SimpleDateFormat("HH:mm", Locale("es", "ES"))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtEntry: TextView = view.findViewById(R.id.txtEntry)
        val txtExit: TextView = view.findViewById(R.id.txtExit)
        val txtHours: TextView = view.findViewById(R.id.txtHours)
        val txtPersonInfo: TextView = view.findViewById(R.id.txtPersonInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_attendance_list_adapter_simple, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val att = itemsList[position]

        // Fecha
        holder.txtDate.text = formatter.format(att.dateAttendance)

        // Buscar persona por ID
        val person = persons.firstOrNull { it.ID == att.idPerson }
        val fullName = person?.FullName() ?: "Desconocido"
        val cedula = person?.ID ?: "N/A"
        holder.txtPersonInfo.text = "$fullName — $cedula"

        // Entrada / Salida
        holder.txtEntry.text = att.timeEntry?.let { timeFmt.format(it) } ?: "--"
        holder.txtExit.text = att.timeExit?.let { timeFmt.format(it) } ?: "--"

        // Horas trabajadas
        val minutes = att.hoursAttendanceMinutes()
        holder.txtHours.text = "${minutes / 60}h ${minutes % 60}m"
    }

    fun update(newList: List<Attendances>) {
        itemsList = newList
        notifyDataSetChanged()
    }
}
