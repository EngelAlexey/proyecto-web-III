package com.example.clocker.Data

import Entity.Attendances
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * AttendanceRecord - Adaptador de Attendances para reportes
 * Mapea la clase Entity.Attendances al formato esperado por PDFGenerator
 */
data class AttendanceRecord(
    val id: String,
    val personId: String,
    val personName: String,
    val date: Date,
    val clockIn: Date?,
    val clockOut: Date?,
    val zoneId: String,
    val zoneName: String,
    val hoursWorked: Double,
    val isLateArrival: Boolean,
    val lateMinutes: Int
) {
    companion object {
        private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
        private val timeFmt = SimpleDateFormat("HH:mm", Locale("es", "ES"))

        /**
         * Convierte Entity.Attendances a AttendanceRecord
         */
        fun fromAttendances(
            attendance: Attendances,
            personName: String,
            zoneName: String,
            expectedClockIn: Date? = null
        ): AttendanceRecord {
            // Calcular horas trabajadas
            val minutes = attendance.hoursAttendanceMinutes()
            val hoursWorked = minutes / 60.0

            // Calcular si llegó tarde
            var isLate = false
            var lateMinutes = 0

            if (expectedClockIn != null && attendance.timeEntry != null) {
                val diff = attendance.timeEntry!!.time - expectedClockIn.time
                if (diff > 0) {
                    isLate = true
                    lateMinutes = TimeUnit.MILLISECONDS.toMinutes(diff).toInt()
                }
            }

            return AttendanceRecord(
                id = attendance.idAttendance,
                personId = attendance.idPerson,
                personName = personName,
                date = attendance.dateAttendance,
                clockIn = attendance.timeEntry,
                clockOut = attendance.timeExit,
                zoneId = attendance.entryID,
                zoneName = zoneName,
                hoursWorked = hoursWorked,
                isLateArrival = isLate,
                lateMinutes = lateMinutes
            )
        }
    }

    fun getFormattedDate(): String = dateFmt.format(date)

    fun getFormattedClockIn(): String = clockIn?.let { timeFmt.format(it) } ?: "--:--"

    fun getFormattedClockOut(): String = clockOut?.let { timeFmt.format(it) } ?: "--:--"

    fun getFormattedHours(): String {
        val h = hoursWorked.toInt()
        val m = ((hoursWorked - h) * 60).toInt()
        return "${h}h ${m}m"
    }
}