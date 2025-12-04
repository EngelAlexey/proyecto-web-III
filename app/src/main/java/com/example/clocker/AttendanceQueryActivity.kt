package com.example.clocker

import Controller.PersonController
import Data.IDataManager
import Data.MemoryDataManager
import Entity.Attendances
import Entity.Person
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.app.AlertDialog

class AttendanceQueryActivity : AppCompatActivity() {

    private lateinit var dataManager: IDataManager
    private lateinit var personController: PersonController

    private lateinit var btnSelectPersons: Button
    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button
    private lateinit var btnSearch: Button
    private lateinit var txtTotals: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AttendanceListAdapterSimple

    private var startDate: Date? = null
    private var endDate: Date? = null

    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))

    private var personsList: List<Person> = emptyList()
    private val selectedPersonIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_query)

        dataManager = MemoryDataManager
        personController = PersonController(this)

        btnSelectPersons = findViewById(R.id.btnSelectPersons)
        btnStart = findViewById(R.id.btnStartDate)
        btnEnd = findViewById(R.id.btnEndDate)
        btnSearch = findViewById(R.id.btnSearch)
        txtTotals = findViewById(R.id.txtTotals)
        recycler = findViewById(R.id.recyclerAttendance)

        personsList = personController.getAllPerson().filter { it.Status }

        adapter = AttendanceListAdapterSimple(
            emptyList(),
            personsList
        )

        adapter = AttendanceListAdapterSimple(
            emptyList(),
            personsList
        )


        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        personsList = personController.getAllPerson()

        btnSelectPersons.setOnClickListener { showPersonSelector() }
        btnStart.setOnClickListener { pickDate(true) }
        btnEnd.setOnClickListener { pickDate(false) }
        btnSearch.setOnClickListener { search() }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnExportPdf).setOnClickListener { exportPdf() }
    }

    private fun showPersonSelector() {
        val names = personsList.map { "${it.ID} - ${it.FullName()}" }.toTypedArray()
        val checkedItems = personsList.map { selectedPersonIds.contains(it.ID) }.toBooleanArray()

        AlertDialog.Builder(this)
            .setTitle("Seleccionar Personas")
            .setMultiChoiceItems(names, checkedItems) { _, which, isChecked ->
                val id = personsList[which].ID
                if (isChecked) {
                    selectedPersonIds.add(id)
                } else {
                    selectedPersonIds.remove(id)
                }
            }
            .setPositiveButton("Aceptar") { _, _ ->
                if (selectedPersonIds.isEmpty()) {
                    btnSelectPersons.text = "Seleccionar Personas"
                } else if (selectedPersonIds.size == 1) {
                    val person = personsList.firstOrNull { it.ID == selectedPersonIds[0] }
                    btnSelectPersons.text = person?.FullName() ?: "1 persona seleccionada"
                } else {
                    btnSelectPersons.text = "${selectedPersonIds.size} personas seleccionadas"
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
        var list: List<Attendances> = dataManager.getAllAttendance()

        if (selectedPersonIds.isNotEmpty()) {
            list = list.filter { selectedPersonIds.contains(it.idPerson) }
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

        val personName =
            if (selectedPersonIds.isEmpty()) null
            else "${selectedPersonIds.size} persona(s)"

        val file = Util.AttendancePdfGenerator.generate(
            this,
            list,
            personName,
            null,
            startDate,
            endDate
        )

        Toast.makeText(this, "PDF generado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }
}
