package com.example.clocker

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import Data.MemoryDataManager
import Entity.Attendances
import java.text.SimpleDateFormat
import java.util.*


class AttendanceListActivity : AppCompatActivity() {

    private lateinit var btnDate: Button
    private lateinit var spinnerPerson: Spinner
    private lateinit var listView: ListView
    private val displayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var selectedDate: Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendance_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnDate = findViewById(R.id.btnDate)
        spinnerPerson = findViewById(R.id.spinnerPerson)
        listView = findViewById(R.id.attendanceList)

        selectedDate = Date()
        btnDate.text = displayFormat.format(selectedDate)

        btnDate.setOnClickListener { openDatePicker() }

        val persons = try {
            MemoryDataManager.getAllPerson().map { "${it.ID.trim()} - ${it.FullName().trim()}" }
        } catch (e: Exception) {
            listOf("No persons")
        }

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }


        val personAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, persons)
        personAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPerson.adapter = personAdapter

        spinnerPerson.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                refreshList()
            }
            override fun onNothingSelected(parent: AdapterView<*>) { refreshList() }
        }

        refreshList()
    }

    private fun openDatePicker() {
        val cal = Calendar.getInstance()
        cal.time = selectedDate
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)

        val dp = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth, 0, 0, 0)
            selectedDate = c.time
            btnDate.text = displayFormat.format(selectedDate)
            refreshList()
        }, y, m, d)
        dp.show()
    }

    private fun refreshList() {
        val personSelected = if (spinnerPerson.adapter.count > 0) spinnerPerson.selectedItem.toString() else ""
        val idPerson = personSelected.split("-").firstOrNull()?.trim() ?: ""

        val allAttendances = try {
            MemoryDataManager.getAllAttendance()
        } catch (e: Exception) {
            emptyList<Attendances>()
        }

        val filtered = allAttendances.filter { att ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val attDateStr = sdf.format(att.dateAttendance)
            val selDateStr = sdf.format(selectedDate)
            val matchDate = attDateStr == selDateStr
            val matchPerson = idPerson.isBlank() || att.idPerson.trim() == idPerson
            matchDate && matchPerson
        }

        val display = if (filtered.isEmpty()) {
            listOf(getString(R.string.MsgDataNoFound))
        } else {
            filtered.map { att ->
                val entry = att.timeEntry?.let { SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(it) } ?: "--"
                val exit = att.timeExit?.let { SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(it) } ?: "--"
                val minutes = att.hoursAttendanceMinutes()
                "${att.idPerson} | ${displayFormat.format(att.dateAttendance)} | $entry - $exit | ${minutes} min"
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, display)
        listView.adapter = adapter
    }
}
