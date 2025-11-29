package com.example.clocker

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import Controller.ReportController
import Data.MemoryDataManager
import Entity.ReportRow
import java.util.Calendar
import java.util.Date

class ReportActivity : AppCompatActivity() {

    private val reportController = ReportController(MemoryDataManager)
    private val selectedPersonIds = mutableListOf<String>()
    private var selectedZoneId: String? = null
    private var fromDate: Date? = null
    private var toDate: Date? = null

    private val adapter = ReportAdapter()

    private lateinit var rvReport: RecyclerView
    private lateinit var btnFrom: Button
    private lateinit var btnTo: Button
    private lateinit var btnGenerate: Button
    private lateinit var btnExportPDF: Button
    private lateinit var btnSelectPersons: Button
    private lateinit var btnSelectZone: Button
    private lateinit var spinnerTipoReporte: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // Inicializar vistas
        rvReport = findViewById(R.id.rvReport)
        btnFrom = findViewById(R.id.btnFrom)
        btnTo = findViewById(R.id.btnTo)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnExportPDF = findViewById(R.id.btnExportPDF)
        btnSelectPersons = findViewById(R.id.btnSelectPersons)
        btnSelectZone = findViewById(R.id.btnSelectZone)
        spinnerTipoReporte = findViewById(R.id.spinnerTipoReporte)

        // Configurar RecyclerView
        rvReport.layoutManager = LinearLayoutManager(this)
        rvReport.adapter = adapter

        // Configurar Spinner de tipo de reporte (opcional, para futuras mejoras)
        val opciones = listOf(
            "Detalle por día",
            "Resumen por persona",
            "Total de horas trabajadas"
        )
        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opciones
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoReporte.adapter = adapterSpinner

        spinnerTipoReporte.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                Toast.makeText(
                    this@ReportActivity,
                    "Seleccionaste: ${opciones[position]}",
                    Toast.LENGTH_SHORT
                ).show()
                // Aquí puedes hacer lógica para cambiar el tipo de reporte si quieres
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Selección de fechas
        btnFrom.setOnClickListener {
            pickDate { date ->
                fromDate = date
                btnFrom.text = dateToDisplayString(date)
            }
        }

        btnTo.setOnClickListener {
            pickDate { date ->
                toDate = date
                btnTo.text = dateToDisplayString(date)
            }
        }

        // Botones para generar reporte y exportar PDF
        btnGenerate.setOnClickListener { generarReporte() }
        btnExportPDF.setOnClickListener { exportarPDF() }

        // Selección de personas y zona
        btnSelectPersons.setOnClickListener { mostrarSelectorPersonas() }
        btnSelectZone.setOnClickListener { showZoneDialog() }
    }

    private fun pickDate(onSelected: (Date) -> Unit) {
        val c = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth, 0, 0, 0)
                onSelected(calendar.time)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun dateToDisplayString(date: Date): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(date)
    }

    private fun showZoneDialog() {
        val zones = MemoryDataManager.getActiveZones()
        if (zones.isEmpty()) {
            Toast.makeText(this, "No hay zonas registradas", Toast.LENGTH_SHORT).show()
            return
        }
        val items = zones.map { it.Code }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Seleccionar zona")
            .setItems(items) { _, index ->
                val zone = zones[index]
                selectedZoneId = zone.ID
                btnSelectZone.text = "Zona: ${zone.Code}"
            }
            .show()
    }

    private fun mostrarSelectorPersonas() {
        val personas = MemoryDataManager.getAllPerson()
        if (personas.isEmpty()) {
            Toast.makeText(this, "No hay personas registradas", Toast.LENGTH_SHORT).show()
            return
        }

        val nombres = personas.map { it.FullName() }.toTypedArray()
        val seleccionadas = BooleanArray(personas.size) { index -> selectedPersonIds.contains(personas[index].ID) }

        AlertDialog.Builder(this)
            .setTitle("Seleccionar personas")
            .setMultiChoiceItems(nombres, seleccionadas) { _, index, isChecked ->
                seleccionadas[index] = isChecked
            }
            .setPositiveButton("OK") { _, _ ->
                selectedPersonIds.clear()
                personas.forEachIndexed { i, p ->
                    if (seleccionadas[i]) selectedPersonIds.add(p.ID)
                }
                btnSelectPersons.text = "Personas: ${selectedPersonIds.size} seleccionadas"
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun generarReporte() {
        try {
            val filas = reportController.generarReporte(
                fromDate,
                toDate,
                selectedPersonIds,
                selectedZoneId
            )
            adapter.setData(filas)

            if (filas.isEmpty()) {
                Toast.makeText(this, "No hay datos para mostrar", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "Error al generar reporte", Toast.LENGTH_LONG).show()
        }
    }

    private fun exportarPDF() {
        val rows = adapter.getData()
        if (rows.isEmpty()) {
            Toast.makeText(this, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        val allPersons = MemoryDataManager.getAllPerson()
        val personNames = allPersons
            .filter { selectedPersonIds.contains(it.ID) }
            .map { it.FullName() }

        val totalMinutes = rows.sumOf { row ->
            row.hoursWorked?.let {
                val parts = it.split(":")
                if (parts.size == 2) {
                    val h = parts[0].toIntOrNull() ?: 0
                    val m = parts[1].toIntOrNull() ?: 0
                    h * 60 + m
                } else 0
            } ?: 0
        }
        val totalHours = "%02d:%02d".format(totalMinutes / 60, totalMinutes % 60)

        ReportPdfGenerator.generatePdf(
            this,
            rows,
            fromDate,
            toDate,
            selectedZoneId,
            personNames,
            totalHours
        )
    }
}
