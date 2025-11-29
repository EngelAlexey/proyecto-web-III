package com.example.clocker

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import Controller.ReportController
import Data.MemoryDataManager
import Entity.ReportRow
import java.util.Calendar
import java.util.Date

class ReportActivity : AppCompatActivity() {

    private val reportController = ReportController(MemoryDataManager)
    private val selectedPersonIds = mutableListOf<String>()

    private var fromDate: Date? = null
    private var toDate: Date? = null

    private val adapter = ReportAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // Configurar RecyclerView
        rvReport.layoutManager = LinearLayoutManager(this)
        rvReport.adapter = adapter

        // Botón desde
        btnFrom.setOnClickListener {
            pickDate { date ->
                fromDate = date
                btnFrom.text = date.toString()
            }
        }

        // Botón hasta
        btnTo.setOnClickListener {
            pickDate { date ->
                toDate = date
                btnTo.text = date.toString()
            }
        }

        // Botón generar reporte
        btnGenerate.setOnClickListener {
            generarReporte()
        }

        // Botón exportar PDF
        btnExportPDF.setOnClickListener {
            exportarPDF()
        }

        // BOTÓN SELECCIONAR PERSONAS
        btnSelectPersons.setOnClickListener {
            // Aquí luego agregamos el selector de personas (si lo quieres)
            Toast.makeText(this, "Módulo de selección pronto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickDate(onSelected: (Date) -> Unit) {
        val c = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day, 0, 0, 0)
                onSelected(calendar.time)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun generarReporte() {
        try {
            val result: List<ReportRow> = reportController.generarReporte(
                fromDate,
                toDate,
                selectedPersonIds
            )
            adapter.setData(result)

            if (result.isEmpty()) {
                Toast.makeText(this, "No hay datos para mostrar", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun exportarPDF() {
        val rows = adapter.getData()

        if (rows.isEmpty()) {
            Toast.makeText(this, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        ReportPdfGenerator.generatePdf(this, rows, fromDate, toDate)
    }
}
