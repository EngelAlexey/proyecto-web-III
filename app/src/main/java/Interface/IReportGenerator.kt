package com.example.clocker.Interface

import com.example.clocker.Data.AttendanceRecord
import com.example.clocker.Data.DaySummary
import com.example.clocker.Data.PersonSummary
import com.example.clocker.Data.Report
import com.example.clocker.Data.ReportFilter
import com.example.clocker.Data.ZoneSummary

/**
 * IReportGenerator - Interfaz para la generación de reportes
 *
 * Define los métodos necesarios para generar reportes de asistencias
 * con diferentes formatos y cálculos.
 */
interface IReportGenerator {

    /**
     * Genera un reporte completo basado en datos de asistencia y filtros
     *
     * @param attendanceData Lista de registros de asistencia
     * @param filter Filtros aplicados al reporte
     * @return Report completo con todos los cálculos
     */
    fun generateReport(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): Report

    /**
     * Calcula el resumen por persona
     *
     * @param attendanceData Lista de registros de asistencia
     * @param filter Filtros aplicados
     * @return Lista de resúmenes por persona
     */
    fun calculatePersonSummary(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<PersonSummary>

    /**
     * Calcula el resumen por día
     *
     * @param attendanceData Lista de registros de asistencia
     * @param filter Filtros aplicados
     * @return Lista de resúmenes por día
     */
    fun calculateDaySummary(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<DaySummary>

    /**
     * Calcula el resumen por zona
     *
     * @param attendanceData Lista de registros de asistencia
     * @param filter Filtros aplicados
     * @return Lista de resúmenes por zona
     */
    fun calculateZoneSummary(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<ZoneSummary>

    /**
     * Calcula el total de horas trabajadas
     *
     * @param attendanceData Lista de registros de asistencia
     * @return Total de horas trabajadas
     */
    fun calculateTotalHours(attendanceData: List<AttendanceRecord>): Double

    /**
     * Calcula el total de atrasos
     *
     * @param attendanceData Lista de registros de asistencia
     * @return Cantidad total de atrasos
     */
    fun calculateTotalLateArrivals(attendanceData: List<AttendanceRecord>): Int

    /**
     * Calcula el total de ausencias
     *
     * @param attendanceData Lista de registros de asistencia
     * @param filter Filtros aplicados (para saber el rango de fechas esperado)
     * @return Cantidad total de ausencias
     */
    fun calculateTotalAbsences(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): Int

    /**
     * Calcula las horas trabajadas entre dos fechas
     *
     * @param clockIn Hora de entrada
     * @param clockOut Hora de salida
     * @return Horas trabajadas (en formato decimal, ej: 8.5)
     */
    fun calculateHoursBetween(clockIn: java.util.Date?, clockOut: java.util.Date?): Double

    /**
     * Verifica si una marca es un atraso
     *
     * @param clockIn Hora de entrada
     * @param expectedTime Hora esperada de entrada (ej: 08:00)
     * @return Pair<Boolean, Int> - (es atraso, minutos de retraso)
     */
    fun isLateArrival(clockIn: java.util.Date?, expectedTime: String = "08:00"): Pair<Boolean, Int>

    /**
     * Filtra los datos de asistencia según los criterios del filtro
     *
     * @param attendanceData Lista completa de registros
     * @param filter Filtros a aplicar
     * @return Lista filtrada
     */
    fun filterAttendanceData(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<AttendanceRecord>

    /**
     * Ordena los datos de asistencia según el criterio del filtro
     *
     * @param attendanceData Lista de registros
     * @param filter Filtros con criterio de ordenamiento
     * @return Lista ordenada
     */
    fun sortAttendanceData(
        attendanceData: List<AttendanceRecord>,
        filter: ReportFilter
    ): List<AttendanceRecord>

    /**
     * Genera el nombre sugerido para el reporte
     *
     * @param filter Filtros aplicados
     * @return Nombre sugerido
     */
    fun generateReportName(filter: ReportFilter): String

    /**
     * Calcula estadísticas adicionales para el reporte
     *
     * @param attendanceData Lista de registros
     * @return Map con estadísticas adicionales (ej: promedio de horas, etc.)
     */
    fun calculateAdditionalStats(attendanceData: List<AttendanceRecord>): Map<String, Any>
}