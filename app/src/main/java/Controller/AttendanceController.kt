package Controller

import Data.MemoryDataManager
import Entity.Attendances
import Entity.Clock
import android.content.Context
import android.util.Log
import com.example.clocker.R
import java.time.ZoneId
import java.util.*

/**
 * AttendanceController - Procesa marcas de Clock y las agrupa en Attendances
 *
 * Flujo:
 * 1. Primera marca del día = Entrada (crea nueva Attendance)
 * 2. Segunda marca del día = Salida (actualiza Attendance existente)
 */
class AttendanceController(private val context: Context) {

    private val dataManager = MemoryDataManager

    /**
     * Procesa una marca de reloj y la convierte en asistencia
     *
     * @param clock Marca de reloj (entrada o salida)
     */
    fun processClockMark(clock: Clock) {
        try {
            Log.d("AttendanceController", "🔄 Procesando marca de ${clock.IDPerson}")

            // Convertir fecha del Clock a Date
            val clockDate = Date.from(clock.DateClock.atZone(ZoneId.systemDefault()).toInstant())

            // ✅ BUSCAR ASISTENCIA ABIERTA (sin salida) DE ESTA PERSONA
            val openAttendance = dataManager.getAllAttendance()
                .firstOrNull { attendance ->
                    attendance.idPerson == clock.IDPerson &&
                            attendance.timeExit == null // Sin salida registrada
                }

            if (openAttendance == null) {
                // ✅ PRIMERA MARCA = ENTRADA (crear nueva asistencia)
                createNewAttendance(clock, clockDate)
                Log.d("AttendanceController", "✅ Nueva asistencia creada (ENTRADA)")
            } else {
                // ✅ SEGUNDA MARCA = SALIDA (actualizar asistencia existente)
                updateAttendanceExit(openAttendance, clock, clockDate)
                Log.d("AttendanceController", "✅ Asistencia actualizada (SALIDA)")
            }

        } catch (e: Exception) {
            Log.e("AttendanceController", "❌ Error procesando marca: ${e.message}", e)
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    /**
     * Crea nueva asistencia con marca de entrada
     */
    private fun createNewAttendance(clock: Clock, clockDateTime: Date) {
        // Usar fecha de la marca como fecha de asistencia (sin resetear hora)
        val newAttendance = Attendances(
            IDAttendance = UUID.randomUUID().toString(),
            DateAttendance = clockDateTime, // ✅ Fecha REAL de entrada
            IDPerson = clock.IDPerson,
            TimeEntry = clockDateTime,
            TimeExit = null,
            EntryID = clock.IDClock,
            ExitID = ""
        )

        dataManager.addAttendance(newAttendance)

        Log.d("AttendanceController", """
            📥 ENTRADA REGISTRADA:
            - Persona: ${clock.IDPerson}
            - Fecha/Hora: $clockDateTime
        """.trimIndent())
    }

    /**
     * Actualiza asistencia existente con marca de salida
     */
    private fun updateAttendanceExit(attendance: Attendances, clock: Clock, clockDateTime: Date) {
        attendance.timeExit = clockDateTime
        attendance.exitID = clock.IDClock

        dataManager.updateAttendance(attendance)

        val hoursWorked = attendance.hoursAttendanceMinutes() / 60.0

        Log.d("AttendanceController", """
            📤 SALIDA REGISTRADA:
            - Persona: ${clock.IDPerson}
            - Hora entrada: ${attendance.timeEntry}
            - Hora salida: $clockDateTime
            - Horas trabajadas: ${hoursWorked}h
        """.trimIndent())
    }

    /**
     * Verifica si dos fechas son del mismo día
     */
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1

        val cal2 = Calendar.getInstance()
        cal2.time = date2

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Obtiene todas las asistencias
     */
    fun getAllAttendances(): List<Attendances> {
        return dataManager.getAllAttendance()
    }

    /**
     * Obtiene asistencias de una persona
     */
    fun getAttendancesByPerson(personId: String): List<Attendances> {
        return dataManager.getAllAttendance().filter { it.idPerson == personId }
    }

    /**
     * Obtiene asistencias de un rango de fechas
     */
    fun getAttendancesByDateRange(startDate: Date, endDate: Date): List<Attendances> {
        return dataManager.getAllAttendance().filter { attendance ->
            !attendance.dateAttendance.before(startDate) &&
                    !attendance.dateAttendance.after(endDate)
        }
    }

    /**
     * Elimina una asistencia
     */
    fun deleteAttendance(attendanceId: String) {
        try {
            dataManager.removeAttendance(attendanceId)
            Log.d("AttendanceController", "🗑️ Asistencia eliminada: $attendanceId")
        } catch (e: Exception) {
            Log.e("AttendanceController", "❌ Error eliminando asistencia: ${e.message}", e)
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
    }
}