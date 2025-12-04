package com.example.clocker.Data

import java.text.SimpleDateFormat
import java.util.*

data class Report(
    val id: String,
    val name: String,
    val generatedDate: Date,
    val filter: ReportFilter,
    val companyName: String,
    val companyLogo: String?,
    val attendanceRecords: List<AttendanceRecord>,
    val totalRecords: Int,
    val totalPersons: Int,
    val totalHoursWorked: Double,
    val totalLateArrivals: Int,
    val totalAbsences: Int,
    val summaryByPerson: List<PersonSummary>,
    val summaryByDay: List<DaySummary>,
    val summaryByZone: List<ZoneSummary>,
    val reportType: String,
    val status: ReportStatus,
    val pdfFilePath: String?,
    val pdfGeneratedDate: Date?,
    val generatedBy: String?,
    val notes: String?
) {
    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))

    fun getDateRangeString(): String {
        return "${dateFmt.format(filter.startDate)} - ${dateFmt.format(filter.endDate)}"
    }

    fun getSuggestedPDFFileName(): String {
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(generatedDate)
        return "reporte_${reportType.lowercase()}_$dateStr.pdf"
    }

    fun getFormattedGeneratedDate(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(generatedDate)
    }
}

enum class ReportStatus {
    PENDING,
    GENERATING,
    COMPLETED,
    ERROR,
    EXPORTED
}

data class PersonSummary(
    val personId: String,
    val personName: String,
    val totalDays: Int,
    val presentDays: Int,
    val absentDays: Int,
    val lateArrivals: Int,
    val totalHours: Double,
    val averageHoursPerDay: Double
) {
    fun getFormattedTotalHours(): String {
        val h = totalHours.toInt()
        val m = ((totalHours - h) * 60).toInt()
        return "${h}h ${m}m"
    }
}

data class DaySummary(
    val date: Date,
    val totalPersons: Int,
    val presentPersons: Int,
    val absentPersons: Int,
    val lateArrivals: Int,
    val totalHours: Double
) {
    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))

    fun getFormattedDate(): String = dateFmt.format(date)
}

data class ZoneSummary(
    val zoneId: String,
    val zoneName: String,
    val totalRecords: Int,
    val totalPersons: Int,
    val totalHours: Double,
    val averageHoursPerRecord: Double
) {
    fun getFormattedTotalHours(): String {
        val h = totalHours.toInt()
        val m = ((totalHours - h) * 60).toInt()
        return "${h}h ${m}m"
    }
}