package com.example.clocker.Controller

import android.content.Context
import android.widget.Toast
import com.example.clocker.Data.Report
import com.example.clocker.Data.ReportFilter
import com.example.clocker.Data.ReportTemplate
import com.example.clocker.Interface.IReportGenerator
import com.example.clocker.Interface.IReportRepository
import com.example.clocker.Util.DateRangeValidator
import com.example.clocker.Util.PDFGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

/**
 * ReportController - Controlador principal del módulo de reportes
 *
 * Responsabilidades:
 * - Coordinar la generación de reportes
 * - Validar filtros y parámetros
 * - Gestionar la exportación a PDF
 * - Manejar plantillas predefinidas
 */
class ReportController(
    private val context: Context,
    private val reportRepository: IReportRepository,
    private val reportGenerator: IReportGenerator
) {

    // Scope para operaciones asíncronas
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Genera un reporte con los filtros especificados
     * @param filter Filtros aplicados al reporte
     * @param onSuccess Callback cuando el reporte se genera exitosamente
     * @param onError Callback cuando hay un error
     */
    fun generateReport(
        filter: ReportFilter,
        onSuccess: (Report) -> Unit,
        onError: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                // 1. Validar filtros
                if (!validateFilters(filter)) {
                    onError("Los filtros seleccionados no son válidos")
                    return@launch
                }

                // 2. Obtener datos del repositorio
                val attendanceData = withContext(Dispatchers.IO) {
                    reportRepository.getAttendanceData(filter)
                }

                // 3. Generar el reporte
                val report = withContext(Dispatchers.Default) {
                    reportGenerator.generateReport(attendanceData, filter)
                }

                // 4. Guardar en base de datos (opcional)
                withContext(Dispatchers.IO) {
                    reportRepository.saveReport(report)
                }

                // 5. Notificar éxito
                onSuccess(report)
                showToast("Reporte generado exitosamente")

            } catch (e: Exception) {
                onError("Error al generar reporte: ${e.message}")
            }
        }
    }

    /**
     * Genera un reporte usando una plantilla predefinida
     * @param template Plantilla a usar
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @param personIds IDs de personas (null = todas)
     * @param zoneIds IDs de zonas (null = todas)
     */
    fun generateReportFromTemplate(
        template: ReportTemplate,
        startDate: Date,
        endDate: Date,
        personIds: List<String>? = null,
        zoneIds: List<String>? = null,
        onSuccess: (Report) -> Unit,
        onError: (String) -> Unit
    ) {
        // Crear filtro basado en la plantilla
        val filter = when (template) {
            ReportTemplate.MONTHLY_BY_PERSON -> ReportFilter(
                startDate = startDate,
                endDate = endDate,
                personIds = personIds,
                zoneIds = zoneIds,
                reportType = "MONTHLY",
                groupBy = "PERSON",
                includeHours = true,
                includeLateArrivals = true
            )
            ReportTemplate.WEEKLY_SUMMARY -> ReportFilter(
                startDate = startDate,
                endDate = endDate,
                personIds = personIds,
                zoneIds = zoneIds,
                reportType = "WEEKLY",
                groupBy = "SUMMARY",
                includeHours = true,
                includeLateArrivals = false
            )
            ReportTemplate.DAILY_DETAIL -> ReportFilter(
                startDate = startDate,
                endDate = endDate,
                personIds = personIds,
                zoneIds = zoneIds,
                reportType = "DAILY",
                groupBy = "DAY",
                includeHours = false,
                includeLateArrivals = false
            )
            ReportTemplate.HOURS_WORKED -> ReportFilter(
                startDate = startDate,
                endDate = endDate,
                personIds = personIds,
                zoneIds = zoneIds,
                reportType = "HOURS",
                groupBy = "PERSON",
                includeHours = true,
                includeLateArrivals = false
            )
            ReportTemplate.LATE_ARRIVALS -> ReportFilter(
                startDate = startDate,
                endDate = endDate,
                personIds = personIds,
                zoneIds = zoneIds,
                reportType = "LATE",
                groupBy = "PERSON",
                includeHours = false,
                includeLateArrivals = true
            )
        }

        // Generar reporte con el filtro creado
        generateReport(filter, onSuccess, onError)
    }

    /**
     * Exporta un reporte a PDF
     * @param report Reporte a exportar
     * @param onSuccess Callback con el archivo PDF generado
     * @param onError Callback de error
     */
    fun exportToPDF(
        report: Report,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val pdfFile = withContext(Dispatchers.IO) {
                    PDFGenerator.generatePDF(context, report)
                }

                onSuccess(pdfFile)
                showToast("PDF exportado exitosamente")

            } catch (e: Exception) {
                onError("Error al exportar PDF: ${e.message}")
            }
        }
    }

    /**
     * Obtiene el historial de reportes generados
     * @param limit Cantidad máxima de reportes (default: 20)
     */
    fun getReportHistory(
        limit: Int = 20,
        onSuccess: (List<Report>) -> Unit,
        onError: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val reports = withContext(Dispatchers.IO) {
                    reportRepository.getReportHistory(limit)
                }
                onSuccess(reports)
            } catch (e: Exception) {
                onError("Error al cargar historial: ${e.message}")
            }
        }
    }

    /**
     * Elimina un reporte del historial
     * @param reportId ID del reporte a eliminar
     */
    fun deleteReport(
        reportId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    reportRepository.deleteReport(reportId)
                }
                onSuccess()
                showToast("Reporte eliminado")
            } catch (e: Exception) {
                onError("Error al eliminar reporte: ${e.message}")
            }
        }
    }

    /**
     * Valida que los filtros sean correctos
     * @param filter Filtros a validar
     * @return true si son válidos, false si no
     */
    private fun validateFilters(filter: ReportFilter): Boolean {
        // Validar rango de fechas
        if (!DateRangeValidator.isValidRange(filter.startDate, filter.endDate)) {
            showToast("El rango de fechas no es válido")
            return false
        }

        // Validar que el rango no sea mayor a 1 año
        if (DateRangeValidator.getDaysBetween(filter.startDate, filter.endDate) > 365) {
            showToast("El rango no puede ser mayor a 1 año")
            return false
        }

        // Validar que la fecha de inicio no sea futura
        if (filter.startDate.after(Date())) {
            showToast("La fecha de inicio no puede ser futura")
            return false
        }

        return true
    }

    /**
     * Muestra un mensaje Toast
     */
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Obtiene las plantillas disponibles
     */
    fun getAvailableTemplates(): List<ReportTemplate> {
        return ReportTemplate.values().toList()
    }

    /**
     * Cancela todas las operaciones en curso
     */
    fun cancelOperations() {
        // Las coroutines se cancelarán automáticamente cuando el scope sea destruido
    }
}