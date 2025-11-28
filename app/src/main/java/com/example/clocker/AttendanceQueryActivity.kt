package com.example.clocker

import Data.IDataManager
import Data.MemoryDataManager
import Entity.Attendances
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AttendanceQueryActivity : AppCompatActivity() {

    private lateinit var dataManager: IDataManager

    private lateinit var txtID: EditText
    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button
    private lateinit var btnSearch: Button
    private lateinit var txtTotals: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AttendanceListAdapterSimple

    private var startDate: Date? = null
    private var endDate: Date? = null

    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_query)

        dataManager = MemoryDataManager

        txtID = findViewById(R.id.txtPersonID)
        btnStart = findViewById(R.id.btnStartDate)
        btnEnd = findViewById(R.id.btnEndDate)
        btnSearch = findViewById(R.id.btnSearch)
        txtTotals = findViewById(R.id.txtTotals)
        recycler = findViewById(R.id.recyclerAttendance)

        adapter = AttendanceListAdapterSimple(emptyList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnStart.setOnClickListener { pickDate(true) }
        btnEnd.setOnClickListener { pickDate(false) }
        btnSearch.setOnClickListener { search() }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnExportPdf).setOnClickListener {
            exportPdf()
        }
    }

    private fun pickDate(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val selected = Calendar.getInstance()
                selected.set(year, month, day, 0, 0, 0)

                if (isStart) {
                    startDate = selected.time
                    btnStart.text = "Inicio: ${formatter.format(startDate!!)}"
                } else {
                    endDate = selected.time
                    btnEnd.text = "Fin: ${formatter.format(endDate!!)}"
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun search() {
        val idPerson = txtID.text.toString().trim()
        var list: List<Attendances> = dataManager.getAllAttendance()

        if (idPerson.isNotEmpty()) {
            list = list.filter { it.idPerson == idPerson }
        }

        if (startDate != null && endDate != null) {
            list = list.filter {
                it.dateAttendance >= startDate && it.dateAttendance <= endDate
            }
        }

        adapter.update(list)
        showTotals(list)
    }

    private fun showTotals(list: List<Attendances>) {
        val totalMinutes = list.sumOf { it.hoursAttendanceMinutes() }
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        txtTotals.text = "Total trabajado: ${hours}h ${minutes}m"
    }

    private fun exportPdf() {
        val list = adapter.itemsList
        if (list.isEmpty()) {
            Toast.makeText(this, "No hay datos para exportar.", Toast.LENGTH_LONG).show()
            return
        }

        val personName = if (txtID.text.isNotEmpty()) txtID.text.toString() else null
        val zoneName = null  // si luego agregamos filtro por zona, se llena aquí

        val file = Util.AttendancePdfGenerator.generate(
            this,
            list,
            personName,
            zoneName,
            startDate,
            endDate
        )

        Toast.makeText(this, "PDF generado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }
}
