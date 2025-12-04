package com.example.clocker.Interface

import Data.MemoryDataManager
import com.example.clocker.Data.*
import com.example.clocker.Entity.ReportEntity
import Controller.PersonController
import Controller.ZoneController
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class ReportRepositoryImpl(
    private val context: Context,
    private val personController: PersonController,
    private val zoneController: ZoneController
) : IReportRepository {

    override suspend fun getAttendanceData(filter: ReportFilter): List<AttendanceRecord> = withContext(Dispatchers.IO) {

        // ✅ LEER MARCAS DE CLOCK
        val allClocks = MemoryDataManager.getAllClock()

        Log.d("ReportRepository", "Total clocks en memoria: ${allClocks.size}")

        if (allClocks.isEmpty()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "⚠️ No hay marcas registradas. Las personas deben marcar entrada/salida primero.",
                    Toast.LENGTH_LONG
                ).show()
            }
            return@withContext emptyList()
        }

        // Agrupar marcas por persona y fecha
        val attendanceRecords = mutableListOf<AttendanceRecord>()

        // Agrupar por persona y día
        val groupedByPersonAndDate = allClocks.groupBy { clock ->
            val personId = clock.IDPerson
            // ✅ CONVERTIR A DATE sin importar el tipo
            val dateAsDate = convertToDate(clock.DateClock)
            val calendar = Calendar.getInstance()
            calendar.time = dateAsDate
            // Resetear hora para agrupar por día
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            Pair(personId, calendar.time)
        }

        Log.d("ReportRepository", "Grupos encontrados: ${groupedByPersonAndDate.size}")

        groupedByPersonAndDate.forEach { (key, clocks) ->
            val (personId, dateOnly) = key

            // Verificar si está en el rango del filtro
            val dateInRange = !dateOnly.before(filter.startDate) && !dateOnly.after(filter.endDate)

            if (!dateInRange) {
                Log.d("ReportRepository", "Fecha fuera de rango: $dateOnly")
                return@forEach
            }

            // Obtener información de la persona
            val person = try {
                personController.getByIdPerson(personId)
            } catch (e: Exception) {
                Log.e("ReportRepository", "Error obteniendo persona $personId: ${e.message}")
                null
            }

            val personName = person?.let {
                "${it.Name} ${it.FLastName} ${it.SLastName}".trim()
            } ?: "Desconocido"

            // Obtener zona de la persona
            val zoneCode = person?.ZoneCode ?: ""
            val zone = if (zoneCode.isNotEmpty()) {
                try {
                    zoneController.getByCodeZone(zoneCode)
                } catch (e: Exception) {
                    Log.e("ReportRepository", "Error obteniendo zona $zoneCode: ${e.message}")
                    null
                }
            } else {
                null
            }

            val zoneName = zone?.Description ?: "Sin zona"
            val zoneId = zone?.Code ?: ""

            // Ordenar marcas por hora
            val sortedClocks = clocks.sortedBy {
                convertToDate(it.DateClock).time
            }

            // ✅ PRIMERA MARCA = ENTRADA (con su fecha y hora real)
            val clockInFull = sortedClocks.firstOrNull()?.let { convertToDate(it.DateClock) }

            // ✅ ÚLTIMA MARCA = SALIDA (con su fecha y hora real)
            val clockOutFull = if (sortedClocks.size > 1) {
                sortedClocks.lastOrNull()?.let { convertToDate(it.DateClock) }
            } else {
                null
            }

            // ✅ CALCULAR HORAS TRABAJADAS
            val hoursWorked = if (clockInFull != null && clockOutFull != null) {
                calculateHoursBetweenDates(clockInFull, clockOutFull)
            } else {
                0.0
            }

            // ✅ DETERMINAR SI HUBO ATRASO (usando la hora real de entrada)
            val (isLate, lateMinutes) = if (clockInFull != null && zone != null) {
                checkIfLateDate(clockInFull, zone.StartTime.toString())
            } else {
                Pair(false, 0)
            }

            // ✅ CREAR REGISTRO CON DATOS REALES
            val record = AttendanceRecord(
                id = UUID.randomUUID().toString(),
                personId = personId,
                personName = personName,
                date = dateOnly, // Fecha sin hora (para agrupación)
                clockIn = clockInFull, // ✅ FECHA Y HORA REAL DE ENTRADA
                clockOut = clockOutFull, // ✅ FECHA Y HORA REAL DE SALIDA
                hoursWorked = hoursWorked,
                isLateArrival = isLate,
                lateMinutes = lateMinutes,
                zoneId = zoneId,
                zoneName = zoneName
            )

            // Aplicar filtros de persona y zona
            val personMatch = if (filter.includesAllPersons()) {
                true
            } else {
                filter.personIds?.contains(personId) ?: true
            }

            val zoneMatch = if (filter.includesAllZones()) {
                true
            } else {
                filter.zoneIds?.contains(zoneId) ?: true
            }

            if (personMatch && zoneMatch) {
                attendanceRecords.add(record)
                Log.d("ReportRepository", "✅ Registro: $personName - Entrada: $clockInFull - Salida: $clockOutFull")
            }
        }

        Log.d("ReportRepository", "Total registros generados: ${attendanceRecords.size}")

        // Informar al usuario
        withContext(Dispatchers.Main) {
            if (attendanceRecords.isNotEmpty()) {
                Toast.makeText(
                    context,
                    "✅ Encontrados ${attendanceRecords.size} registros de asistencia",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "⚠️ No hay datos en el período seleccionado",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return@withContext attendanceRecords
    }

    // ✅ CONVERTIR CUALQUIER TIPO A DATE
    private fun convertToDate(dateTime: Any): Date {
        return when (dateTime) {
            is Date -> dateTime
            is LocalDateTime -> Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant())
            is LocalDate -> Date.from(dateTime.atStartOfDay(ZoneId.systemDefault()).toInstant())
            else -> {
                Log.e("ReportRepository", "Tipo de fecha desconocido: ${dateTime::class.java}")
                Date()
            }
        }
    }

    // Calcular horas entre entrada y salida
    private fun calculateHoursBetweenDates(clockIn: Date, clockOut: Date): Double {
        val diffMs = clockOut.time - clockIn.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        return minutes / 60.0
    }

    // Verificar si llegó tarde
    private fun checkIfLateDate(clockIn: Date, expectedTime: String): Pair<Boolean, Int> {
        try {
            val parts = expectedTime.split(":")
            val expectedHour = parts[0].toInt()
            val expectedMinute = parts[1].toInt()

            val calendar = Calendar.getInstance()
            calendar.time = clockIn

            val expectedCal = Calendar.getInstance()
            expectedCal.time = clockIn
            expectedCal.set(Calendar.HOUR_OF_DAY, expectedHour)
            expectedCal.set(Calendar.MINUTE, expectedMinute)
            expectedCal.set(Calendar.SECOND, 0)

            if (clockIn.after(expectedCal.time)) {
                val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(
                    clockIn.time - expectedCal.time.time
                ).toInt()
                return Pair(true, diffMinutes)
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error calculando atraso: ${e.message}")
        }

        return Pair(false, 0)
    }

    override suspend fun getAttendanceByPerson(
        personId: String,
        startDate: Date,
        endDate: Date
    ): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        val filter = ReportFilter(
            startDate = startDate,
            endDate = endDate,
            personIds = listOf(personId)
        )
        getAttendanceData(filter)
    }

    override suspend fun getAttendanceByZone(
        zoneId: String,
        startDate: Date,
        endDate: Date
    ): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        val filter = ReportFilter(
            startDate = startDate,
            endDate = endDate,
            zoneIds = listOf(zoneId)
        )
        getAttendanceData(filter)
    }

    override suspend fun getAttendanceByDate(date: Date): List<AttendanceRecord> = withContext(Dispatchers.IO) {
        val filter = ReportFilter(
            startDate = date,
            endDate = date
        )
        getAttendanceData(filter)
    }

    override suspend fun getAllPersons(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        personController.getAllPerson().map { person ->
            val fullName = "${person.Name} ${person.FLastName} ${person.SLastName}".trim()
            Pair(person.ID, fullName)
        }
    }

    override suspend fun getAllZones(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        zoneController.getAllZone().map { zone ->
            Pair(zone.Code, zone.Description)
        }
    }

    override suspend fun saveReport(report: Report): String = withContext(Dispatchers.IO) {
        report.id
    }

    override suspend fun updateReport(report: Report): Boolean = withContext(Dispatchers.IO) {
        false
    }

    override suspend fun deleteReport(reportId: String): Boolean = withContext(Dispatchers.IO) {
        false
    }

    override suspend fun getReportById(reportId: String): Report? = withContext(Dispatchers.IO) {
        null
    }

    override suspend fun getReportHistory(limit: Int): List<Report> = withContext(Dispatchers.IO) {
        emptyList()
    }

    override suspend fun getReportsByDateRange(startDate: Date, endDate: Date): List<Report> = withContext(Dispatchers.IO) {
        emptyList()
    }

    override suspend fun getReportsByType(reportType: String): List<Report> = withContext(Dispatchers.IO) {
        emptyList()
    }

    override suspend fun searchReports(query: String): List<Report> = withContext(Dispatchers.IO) {
        emptyList()
    }

    override suspend fun deleteOldReports(olderThan: Date): Int = withContext(Dispatchers.IO) {
        0
    }

    override suspend fun getReportsCount(): Int = withContext(Dispatchers.IO) {
        0
    }

    override suspend fun updateReportPDFPath(reportId: String, pdfFilePath: String): Boolean = withContext(Dispatchers.IO) {
        false
    }

    override suspend fun getReportsWithPDF(): List<Report> = withContext(Dispatchers.IO) {
        emptyList()
    }

    override suspend fun hasPDF(reportId: String): Boolean = withContext(Dispatchers.IO) {
        false
    }

    override fun toEntity(report: Report): ReportEntity {
        return ReportEntity.fromReport(report, "", "")
    }

    override fun fromEntity(entity: ReportEntity): Report {
        return entity.toReport(
            filter = ReportFilter.currentMonth(),
            attendanceRecords = emptyList(),
            summaryByPerson = emptyList(),
            summaryByDay = emptyList(),
            summaryByZone = emptyList()
        )
    }

    override suspend fun getReportStatistics(): Map<String, Any> = withContext(Dispatchers.IO) {
        emptyMap()
    }

    override suspend fun getMostGeneratedReportTypes(limit: Int): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        emptyList()
    }
}