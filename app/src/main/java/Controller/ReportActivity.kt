package com.example.clocker

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.clocker.Data.Report
import com.example.clocker.Data.ReportFilter
import com.example.clocker.Data.ReportTemplate
import com.example.clocker.Interface.ReportRepositoryImpl
import com.example.clocker.Interface.ReportGeneratorImpl
import com.example.clocker.Util.PDFGenerator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import Controller.PersonController
import Controller.ZoneController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var btnTemplateMonthly: MaterialButton
    private lateinit var btnTemplateWeekly: MaterialButton
    private lateinit var btnTemplateDaily: MaterialButton
    private lateinit var btnTemplateHours: MaterialButton
    private lateinit var btnTemplateLate: MaterialButton
    private lateinit var btnGenerateCustomReport: MaterialButton
    private lateinit var fabGenerateReport: FloatingActionButton
    private lateinit var rvReportHistory: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var layoutLoading: View
    private lateinit var tvLoadingMessage: TextView
    private lateinit var tvDateRange: TextView
    private lateinit var tvPersonsFilter: TextView
    private lateinit var tvZonesFilter: TextView

    private lateinit var personController: PersonController
    private lateinit var zoneController: ZoneController
    private lateinit var repository: ReportRepositoryImpl
    private lateinit var generator: ReportGeneratorImpl

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))

    private var currentFilter: ReportFilter = ReportFilter.currentMonth()
    private var lastGeneratedReport: Report? = null

    // Listas para selección múltiple
    private var selectedPersonIds = mutableListOf<String>()
    private var selectedZoneIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        initControllers()
        initViews()
        setupToolbar()
        setupFilters()
        setupTemplates()
        setupButtons()
        setupRecyclerView()
        loadReportHistory()
    }

    private fun initControllers() {
        personController = PersonController(this)
        zoneController = ZoneController(this)
        repository = ReportRepositoryImpl(this, personController, zoneController)
        generator = ReportGeneratorImpl()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        tvDateRange = findViewById(R.id.tvDateRange)
        tvPersonsFilter = findViewById(R.id.tvPersonsFilter)
        tvZonesFilter = findViewById(R.id.tvZonesFilter)
        btnTemplateMonthly = findViewById(R.id.btnTemplateMonthly)
        btnTemplateWeekly = findViewById(R.id.btnTemplateWeekly)
        btnTemplateDaily = findViewById(R.id.btnTemplateDaily)
        btnTemplateHours = findViewById(R.id.btnTemplateHours)
        btnTemplateLate = findViewById(R.id.btnTemplateLate)
        btnGenerateCustomReport = findViewById(R.id.btnGenerateCustomReport)
        fabGenerateReport = findViewById(R.id.fabGenerateReport)
        rvReportHistory = findViewById(R.id.rvReportHistory)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        layoutLoading = findViewById(R.id.layoutLoading)
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage)


    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.module_reports)
    }

    private fun setupFilters() {
        updateFilterUI(currentFilter)

        // ✅ HACER CLIC EN PERÍODO para cambiar fechas
        tvDateRange.setOnClickListener {
            showDateRangePicker()
        }

        // ✅ HACER CLIC EN PERSONAS para seleccionar
        tvPersonsFilter.setOnClickListener {
            showPersonSelector()
        }

        // ✅ HACER CLIC EN ZONAS para seleccionar
        tvZonesFilter.setOnClickListener {
            showZoneSelector()
        }
    }

    private fun setupTemplates() {
        btnTemplateMonthly.setOnClickListener {
            generateReportFromTemplate(ReportTemplate.MONTHLY_BY_PERSON)
        }
        btnTemplateWeekly.setOnClickListener {
            generateReportFromTemplate(ReportTemplate.WEEKLY_SUMMARY)
        }
        btnTemplateDaily.setOnClickListener {
            generateReportFromTemplate(ReportTemplate.DAILY_DETAIL)
        }
        btnTemplateHours.setOnClickListener {
            generateReportFromTemplate(ReportTemplate.HOURS_WORKED)
        }
        btnTemplateLate.setOnClickListener {
            generateReportFromTemplate(ReportTemplate.LATE_ARRIVALS)
        }
    }

    private fun setupButtons() {
        btnGenerateCustomReport.setOnClickListener {
            generateCustomReport()
        }
        fabGenerateReport.setOnClickListener {
            generateCustomReport()
        }
    }

    private fun setupRecyclerView() {
        rvReportHistory.layoutManager = LinearLayoutManager(this)
    }

    // ✅ SELECTOR DE RANGO DE FECHAS
    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()

        // Seleccionar fecha INICIO
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val startDate = Calendar.getInstance()
                startDate.set(year, month, day, 0, 0, 0)

                // Ahora seleccionar fecha FIN
                DatePickerDialog(
                    this,
                    { _, year2, month2, day2 ->
                        val endDate = Calendar.getInstance()
                        endDate.set(year2, month2, day2, 23, 59, 59)

                        // Actualizar filtro
                        currentFilter = currentFilter.copy(
                            startDate = startDate.time,
                            endDate = endDate.time
                        )
                        updateFilterUI(currentFilter)

                        Toast.makeText(this, "Período actualizado", Toast.LENGTH_SHORT).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    setTitle("Seleccionar fecha FIN")
                    datePicker.minDate = startDate.timeInMillis
                    show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setTitle("Seleccionar fecha INICIO")
            show()
        }
    }

    // ✅ SELECTOR DE PERSONAS
    private fun showPersonSelector() {
        lifecycleScope.launch {
            try {
                val allPersons = withContext(Dispatchers.IO) {
                    repository.getAllPersons()
                }

                if (allPersons.isEmpty()) {
                    Toast.makeText(
                        this@ReportActivity,
                        "No hay personas registradas",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val personNames = allPersons.map { it.second }.toTypedArray()
                val personIds = allPersons.map { it.first }
                val checkedItems = BooleanArray(allPersons.size) { index ->
                    selectedPersonIds.contains(personIds[index])
                }

                AlertDialog.Builder(this@ReportActivity)
                    .setTitle("Seleccionar Personas")
                    .setMultiChoiceItems(personNames, checkedItems) { _, which, isChecked ->
                        if (isChecked) {
                            selectedPersonIds.add(personIds[which])
                        } else {
                            selectedPersonIds.remove(personIds[which])
                        }
                    }
                    .setPositiveButton("Aplicar") { _, _ ->
                        currentFilter = if (selectedPersonIds.isEmpty()) {
                            currentFilter.copy(personIds = null)
                        } else {
                            currentFilter.copy(personIds = selectedPersonIds.toList())
                        }
                        updateFilterUI(currentFilter)
                        Toast.makeText(this@ReportActivity, "Filtro de personas actualizado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .setNeutralButton("Todas") { _, _ ->
                        selectedPersonIds.clear()
                        currentFilter = currentFilter.copy(personIds = null)
                        updateFilterUI(currentFilter)
                        Toast.makeText(this@ReportActivity, "Todas las personas seleccionadas", Toast.LENGTH_SHORT).show()
                    }
                    .show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@ReportActivity,
                    "Error al cargar personas: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ✅ SELECTOR DE ZONAS
    private fun showZoneSelector() {
        lifecycleScope.launch {
            try {
                val allZones = withContext(Dispatchers.IO) {
                    repository.getAllZones()
                }

                if (allZones.isEmpty()) {
                    Toast.makeText(
                        this@ReportActivity,
                        "No hay zonas registradas",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val zoneNames = allZones.map { "${it.first} - ${it.second}" }.toTypedArray()
                val zoneIds = allZones.map { it.first }
                val checkedItems = BooleanArray(allZones.size) { index ->
                    selectedZoneIds.contains(zoneIds[index])
                }

                AlertDialog.Builder(this@ReportActivity)
                    .setTitle("Seleccionar Zonas")
                    .setMultiChoiceItems(zoneNames, checkedItems) { _, which, isChecked ->
                        if (isChecked) {
                            selectedZoneIds.add(zoneIds[which])
                        } else {
                            selectedZoneIds.remove(zoneIds[which])
                        }
                    }
                    .setPositiveButton("Aplicar") { _, _ ->
                        currentFilter = if (selectedZoneIds.isEmpty()) {
                            currentFilter.copy(zoneIds = null)
                        } else {
                            currentFilter.copy(zoneIds = selectedZoneIds.toList())
                        }
                        updateFilterUI(currentFilter)
                        Toast.makeText(this@ReportActivity, "Filtro de zonas actualizado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .setNeutralButton("Todas") { _, _ ->
                        selectedZoneIds.clear()
                        currentFilter = currentFilter.copy(zoneIds = null)
                        updateFilterUI(currentFilter)
                        Toast.makeText(this@ReportActivity, "Todas las zonas seleccionadas", Toast.LENGTH_SHORT).show()
                    }
                    .show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@ReportActivity,
                    "Error al cargar zonas: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateFilterUI(filter: ReportFilter) {
        tvDateRange.text = "Período: ${dateFormat.format(filter.startDate)} - ${dateFormat.format(filter.endDate)}"

        tvPersonsFilter.text = if (filter.includesAllPersons()) {
            "Personas: Todas las personas"
        } else {
            "Personas: ${filter.personIds?.size ?: 0} seleccionadas"
        }

        tvZonesFilter.text = if (filter.includesAllZones()) {
            "Zonas: Todas las zonas"
        } else {
            "Zonas: ${filter.zoneIds?.size ?: 0} seleccionadas"
        }
    }

    private fun generateReportFromTemplate(template: ReportTemplate) {
        showLoading(true, "Generando reporte ${template.templateName}...")

        lifecycleScope.launch {
            try {
                val attendances = repository.getAttendanceData(currentFilter)

                if (attendances.isEmpty()) {
                    showLoading(false)
                    Toast.makeText(
                        this@ReportActivity,
                        "No hay datos en el período seleccionado. Marca entradas/salidas primero.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }

                val report = generator.generateReport(attendances, currentFilter)
                lastGeneratedReport = report

                showLoading(false)
                Toast.makeText(
                    this@ReportActivity,
                    "✅ Reporte generado: ${report.totalRecords} registros",
                    Toast.LENGTH_SHORT
                ).show()
                exportReportToPDF(report)

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    this@ReportActivity,
                    "❌ Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun generateCustomReport() {
        generateReportFromTemplate(ReportTemplate.MONTHLY_BY_PERSON)
    }

    private fun exportReportToPDF(report: Report) {
        showLoading(true, "Exportando a PDF...")

        lifecycleScope.launch {
            try {
                val pdfFile = withContext(Dispatchers.IO) {
                    PDFGenerator.generatePDF(this@ReportActivity, report)
                }

                showLoading(false)
                Toast.makeText(
                    this@ReportActivity,
                    "✅ PDF guardado: ${pdfFile.name}",
                    Toast.LENGTH_LONG
                ).show()
                openPDF(pdfFile)

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    this@ReportActivity,
                    "❌ Error al exportar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun openPDF(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            startActivity(Intent.createChooser(intent, "Abrir PDF"))

        } catch (e: Exception) {
            Toast.makeText(this, "No hay app para abrir PDF", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadReportHistory() {
        showEmptyState(true)
    }

    private fun showLoading(show: Boolean, message: String = "Cargando...") {
        layoutLoading.visibility = if (show) View.VISIBLE else View.GONE
        tvLoadingMessage.text = message
    }

    private fun showEmptyState(show: Boolean) {
        layoutEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        rvReportHistory.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_export_pdf -> {
                lastGeneratedReport?.let {
                    exportReportToPDF(it)
                } ?: Toast.makeText(this, "Genera un reporte primero", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}