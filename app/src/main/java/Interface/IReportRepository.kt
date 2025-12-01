package com.example.clocker.Interface

import com.example.clocker.Data.AttendanceRecord
import com.example.clocker.Data.Report
import com.example.clocker.Data.ReportFilter
import com.example.clocker.Entity.ReportEntity

/**
 * IReportRepository - Interfaz para operaciones de datos de reportes
 *
 * Define los métodos necesarios para acceder, guardar y gestionar
 * datos relacionados con reportes, tanto en base de datos local
 * como obtener datos de asistencias.
 */
interface IReportRepository {

    // ============================================================
    // OPERACIONES DE DATOS DE ASISTENCIA
    // ============================================================

    /**
     * Obtiene los datos de asistencia según los filtros aplicados
     *
     * @param filter Filtros para consultar asistencias
     * @return Lista de registros de asistencia filtrados
     */
    suspend fun getAttendanceData(filter: ReportFilter): List<AttendanceRecord>

    /**
     * Obtiene los datos de asistencia de una persona específica
     *
     * @param personId ID de la persona
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @return Lista de registros de asistencia de la persona
     */
    suspend fun getAttendanceByPerson(
        personId: String,
        startDate: java.util.Date,
        endDate: java.util.Date
    ): List<AttendanceRecord>

    /**
     * Obtiene los datos de asistencia de una zona específica
     *
     * @param zoneId ID de la zona
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @return Lista de registros de asistencia de la zona
     */
    suspend fun getAttendanceByZone(
        zoneId: String,
        startDate: java.util.Date,
        endDate: java.util.Date
    ): List<AttendanceRecord>

    /**
     * Obtiene los datos de asistencia de un día específico
     *
     * @param date Fecha específica
     * @return Lista de registros de asistencia del día
     */
    suspend fun getAttendanceByDate(date: java.util.Date): List<AttendanceRecord>

    /**
     * Obtiene todas las personas registradas
     *
     * @return Lista de IDs y nombres de personas
     */
    suspend fun getAllPersons(): List<Pair<String, String>> // Pair<ID, Nombre>

    /**
     * Obtiene todas las zonas registradas
     *
     * @return Lista de IDs y nombres de zonas
     */
    suspend fun getAllZones(): List<Pair<String, String>> // Pair<ID, Nombre>

    // ============================================================
    // OPERACIONES DE REPORTES (BASE DE DATOS LOCAL)
    // ============================================================

    /**
     * Guarda un reporte generado en la base de datos
     *
     * @param report Reporte a guardar
     * @return ID del reporte guardado
     */
    suspend fun saveReport(report: Report): String

    /**
     * Actualiza un reporte existente
     *
     * @param report Reporte actualizado
     * @return true si se actualizó correctamente
     */
    suspend fun updateReport(report: Report): Boolean

    /**
     * Elimina un reporte de la base de datos
     *
     * @param reportId ID del reporte a eliminar
     * @return true si se eliminó correctamente
     */
    suspend fun deleteReport(reportId: String): Boolean

    /**
     * Obtiene un reporte específico por su ID
     *
     * @param reportId ID del reporte
     * @return Report si existe, null si no
     */
    suspend fun getReportById(reportId: String): Report?

    /**
     * Obtiene el historial de reportes generados
     *
     * @param limit Cantidad máxima de reportes a retornar
     * @return Lista de reportes ordenados por fecha (más recientes primero)
     */
    suspend fun getReportHistory(limit: Int = 20): List<Report>

    /**
     * Obtiene reportes filtrados por rango de fechas
     *
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @return Lista de reportes en ese rango
     */
    suspend fun getReportsByDateRange(
        startDate: java.util.Date,
        endDate: java.util.Date
    ): List<Report>

    /**
     * Obtiene reportes por tipo
     *
     * @param reportType Tipo de reporte (MONTHLY, WEEKLY, etc.)
     * @return Lista de reportes de ese tipo
     */
    suspend fun getReportsByType(reportType: String): List<Report>

    /**
     * Busca reportes por nombre
     *
     * @param query Texto a buscar
     * @return Lista de reportes que coincidan
     */
    suspend fun searchReports(query: String): List<Report>

    /**
     * Elimina todos los reportes antiguos (limpieza)
     *
     * @param olderThan Fecha límite (eliminar reportes más antiguos)
     * @return Cantidad de reportes eliminados
     */
    suspend fun deleteOldReports(olderThan: java.util.Date): Int

    /**
     * Obtiene la cantidad total de reportes guardados
     *
     * @return Cantidad de reportes
     */
    suspend fun getReportsCount(): Int

    // ============================================================
    // OPERACIONES DE EXPORTACIÓN
    // ============================================================

    /**
     * Actualiza la ruta del PDF de un reporte
     *
     * @param reportId ID del reporte
     * @param pdfFilePath Ruta del archivo PDF
     * @return true si se actualizó correctamente
     */
    suspend fun updateReportPDFPath(reportId: String, pdfFilePath: String): Boolean

    /**
     * Obtiene reportes que tienen PDF generado
     *
     * @return Lista de reportes con PDF
     */
    suspend fun getReportsWithPDF(): List<Report>

    /**
     * Verifica si un reporte tiene PDF generado
     *
     * @param reportId ID del reporte
     * @return true si tiene PDF
     */
    suspend fun hasPDF(reportId: String): Boolean

    // ============================================================
    // OPERACIONES DE CONVERSIÓN
    // ============================================================

    /**
     * Convierte un Report a ReportEntity para guardar en BD
     *
     * @param report Reporte a convertir
     * @return ReportEntity listo para guardar
     */
    fun toEntity(report: Report): ReportEntity

    /**
     * Convierte un ReportEntity a Report para usar en la app
     *
     * @param entity Entidad de base de datos
     * @return Report completo
     */
    fun fromEntity(entity: ReportEntity): Report

    // ============================================================
    // OPERACIONES DE ESTADÍSTICAS
    // ============================================================

    /**
     * Obtiene estadísticas generales de reportes
     *
     * @return Map con estadísticas (total reportes, tipos más usados, etc.)
     */
    suspend fun getReportStatistics(): Map<String, Any>

    /**
     * Obtiene los reportes más generados (por plantilla)
     *
     * @param limit Cantidad máxima
     * @return Lista de tipos de reportes más usados
     */
    suspend fun getMostGeneratedReportTypes(limit: Int = 5): List<Pair<String, Int>> // Pair<Tipo, Cantidad>
}