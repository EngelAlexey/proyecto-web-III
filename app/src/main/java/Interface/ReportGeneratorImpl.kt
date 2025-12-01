package com.example.clocker.Interface

import com.example.clocker.Data.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ReportGeneratorImpl : IReportGenerator {

    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    private val timeFmt = SimpleDateFormat("HH:mm", Locale("es", "ES"))

    override fun generateReport(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): Report {
        val reportName = generateReportName(filter)

        val totalRecords = attendanceData.size
        val totalPersons = attendanceData.map { it.personId }.distinct().size
        val totalHours = calculateTotalHours(attendanceData)
        val totalLate = calculateTotalLateArrivals(attendanceData)
        val totalAbsences = calculateTotalAbsences(attendanceData, filter)

        val summaryByPerson = calculatePersonSummary(attendanceData, filter)
        val summaryByDay = calculateDaySummary(attendanceData, filter)
        val summaryByZone = calculateZoneSummary(attendanceData, filter)

        return Report(
            id = UUID.randomUUID().toString(),
            name = reportName,
            generatedDate = Date(),
            filter = filter,
            companyName = "Mi Empresa",
            companyLogo = null,
            attendanceRecords = attendanceData,
            totalRecords = totalRecords,
            totalPersons = totalPersons,
            totalHoursWorked = totalHours,
            totalLateArrivals = totalLate,
            totalAbsences = totalAbsences,
            summaryByPerson = summaryByPerson,
            summaryByDay = summaryByDay,
            summaryByZone = summaryByZone,
            reportType = "GENERAL",
            status = ReportStatus.COMPLETED,
            pdfFilePath = null,
            pdfGeneratedDate = null,
            generatedBy = null,
            notes = null
        )
    }

    override fun calculatePersonSummary(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<PersonSummary> {
        // SIMPLIFICADO: Retorna lista vacía
        return emptyList()
    }

    override fun calculateDaySummary(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<DaySummary> {
        // SIMPLIFICADO: Retorna lista vacía
        return emptyList()
    }

    override fun calculateZoneSummary(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<ZoneSummary> {
        // SIMPLIFICADO: Retorna lista vacía
        return emptyList()
    }

    override fun calculateTotalHours(attendanceData: List<AttendanceRecord>): Double {
        return attendanceData.sumOf { it.hoursWorked }
    }

    override fun calculateTotalLateArrivals(attendanceData: List<AttendanceRecord>): Int {
        return attendanceData.count { it.isLateArrival }
    }

    override fun calculateTotalAbsences(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): Int {
        return 0
    }

    override fun calculateHoursBetween(clockIn: Date?, clockOut: Date?): Double {
        if (clockIn == null || clockOut == null) return 0.0
        val diffMs = clockOut.time - clockIn.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        return minutes / 60.0
    }

    override fun isLateArrival(clockIn: Date?, expectedTime: String): Pair<Boolean, Int> {
        if (clockIn == null) return Pair(false, 0)

        try {
            val calendar = Calendar.getInstance()
            calendar.time = clockIn

            val parts = expectedTime.split(":")
            val expectedHour = parts[0].toInt()
            val expectedMinute = parts[1].toInt()

            val expectedCal = Calendar.getInstance()
            expectedCal.time = clockIn
            expectedCal.set(Calendar.HOUR_OF_DAY, expectedHour)
            expectedCal.set(Calendar.MINUTE, expectedMinute)

            val diff = clockIn.time - expectedCal.time.time
            if (diff > 0) {
                val lateMinutes = TimeUnit.MILLISECONDS.toMinutes(diff).toInt()
                return Pair(true, lateMinutes)
            }
        } catch (e: Exception) {
            // Ignorar errores
        }

        return Pair(false, 0)
    }

    override fun filterAttendanceData(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<AttendanceRecord> {
        return attendanceData.filter { record ->
            val dateInRange = !record.date.before(filter.startDate) &&
                    !record.date.after(filter.endDate)

            val personMatch = if (filter.includesAllPersons()) {
                true
            } else {
                filter.personIds?.contains(record.personId) ?: true
            }

            val zoneMatch = if (filter.includesAllZones()) {
                true
            } else {
                filter.zoneIds?.contains(record.zoneId) ?: true
            }

            dateInRange && personMatch && zoneMatch
        }
    }

    override fun sortAttendanceData(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<AttendanceRecord> {
        return when (filter.groupBy) {
            "PERSON" -> attendanceData.sortedWith(compareBy({ it.personName }, { it.date }))
            "DAY" -> attendanceData.sortedBy { it.date }
            "ZONE" -> attendanceData.sortedWith(compareBy({ it.zoneName }, { it.date }))
            else -> attendanceData.sortedBy { it.date }
        }
    }

    override fun generateReportName(filter: ReportFilter): String {
        val dateRange = "${dateFmt.format(filter.startDate)} - ${dateFmt.format(filter.endDate)}"
        return "Reporte $dateRange"
    }

    override fun calculateAdditionalStats(attendanceData: List<AttendanceRecord>): Map<String, Any> {
        val stats = mutableMapOf<String, Any>()

        if (attendanceData.isNotEmpty()) {
            val avgHours = attendanceData.sumOf { it.hoursWorked } / attendanceData.size
            stats["averageHours"] = avgHours
            stats["totalDays"] = attendanceData.map { dateFmt.format(it.date) }.distinct().size
            stats["latePercentage"] = (attendanceData.count { it.isLateArrival }.toDouble() / attendanceData.size) * 100
        }

        return stats
    }
}