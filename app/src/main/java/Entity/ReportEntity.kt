package com.example.clocker.Entity

import com.example.clocker.Data.Report
import com.example.clocker.Data.ReportFilter
import com.example.clocker.Data.ReportStatus
import com.example.clocker.Data.AttendanceRecord
import com.example.clocker.Data.PersonSummary
import com.example.clocker.Data.DaySummary
import com.example.clocker.Data.ZoneSummary
import java.util.Date

/**
 * ReportEntity - Entidad para guardar reportes generados
 *
 * VERSIÓN SIN ROOM DATABASE
 * Esta es una clase de datos simple que puede guardarse en SharedPreferences
 * o en archivos JSON si decides implementar persistencia más adelante.
 */
data class ReportEntity(
    val id: String,

    // Información básica del reporte
    val name: String,
    val generatedDate: Long, // Date convertido a timestamp

    // Filtros aplicados (guardados como JSON string)
    val filterJson: String,

    // Información de la empresa
    val companyName: String = "Mi Empresa",
    val companyLogo: String? = null,

    // Datos del reporte (guardados como JSON string para flexibilidad)
    val reportDataJson: String,

    // Totales calculados (para consultas rápidas sin parsear JSON)
    val totalRecords: Int = 0,
    val totalPersons: Int = 0,
    val totalHoursWorked: Double = 0.0,
    val totalLateArrivals: Int = 0,
    val totalAbsences: Int = 0,

    // Tipo de reporte
    val reportType: String = "GENERAL",

    // Archivo PDF
    val pdfFilePath: String? = null,
    val pdfGeneratedDate: Long? = null, // Date convertido a timestamp

    // Estado del reporte
    val status: String = "COMPLETED", // GENERATING, COMPLETED, FAILED, EXPORTED

    // Metadatos
    val generatedBy: String? = null,
    val notes: String? = null,

    // Fechas del filtro (para búsquedas rápidas)
    val filterStartDate: Long,
    val filterEndDate: Long,

    // Campos de auditoría
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Convierte la entidad a objeto Report
     */
    fun toReport(
        filter: ReportFilter,
        attendanceRecords: List<AttendanceRecord>,
        summaryByPerson: List<PersonSummary>,
        summaryByDay: List<DaySummary>,
        summaryByZone: List<ZoneSummary>
    ): Report {
        return Report(
            id = id,
            name = name,
            generatedDate = Date(generatedDate),
            filter = filter,
            companyName = companyName,
            companyLogo = companyLogo,
            attendanceRecords = attendanceRecords,
            totalRecords = totalRecords,
            totalPersons = totalPersons,
            totalHoursWorked = totalHoursWorked,
            totalLateArrivals = totalLateArrivals,
            totalAbsences = totalAbsences,
            summaryByPerson = summaryByPerson,
            summaryByDay = summaryByDay,
            summaryByZone = summaryByZone,
            reportType = reportType,
            status = ReportStatus.valueOf(status),
            pdfFilePath = pdfFilePath,
            pdfGeneratedDate = pdfGeneratedDate?.let { Date(it) },
            generatedBy = generatedBy,
            notes = notes
        )
    }

    /**
     * Verifica si el reporte tiene PDF generado
     */
    fun hasPDF(): Boolean {
        return !pdfFilePath.isNullOrEmpty()
    }

    /**
     * Obtiene la fecha de generación como Date
     */
    fun getGeneratedDateAsDate(): Date {
        return Date(generatedDate)
    }

    /**
     * Obtiene el rango de fechas del filtro
     */
    fun getFilterDateRange(): Pair<Date, Date> {
        return Pair(Date(filterStartDate), Date(filterEndDate))
    }

    companion object {
        /**
         * Crea una entidad desde un objeto Report
         */
        fun fromReport(report: Report, filterJson: String, reportDataJson: String): ReportEntity {
            return ReportEntity(
                id = report.id,
                name = report.name,
                generatedDate = report.generatedDate.time,
                filterJson = filterJson,
                companyName = report.companyName,
                companyLogo = report.companyLogo,
                reportDataJson = reportDataJson,
                totalRecords = report.totalRecords,
                totalPersons = report.totalPersons,
                totalHoursWorked = report.totalHoursWorked,
                totalLateArrivals = report.totalLateArrivals,
                totalAbsences = report.totalAbsences,
                reportType = report.reportType,
                pdfFilePath = report.pdfFilePath,
                pdfGeneratedDate = report.pdfGeneratedDate?.time,
                status = report.status.name,
                generatedBy = report.generatedBy,
                notes = report.notes,
                filterStartDate = report.filter.startDate.time,
                filterEndDate = report.filter.endDate.time
            )
        }
    }
}