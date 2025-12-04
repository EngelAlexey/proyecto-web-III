package com.example.clocker.Data

import java.util.Date
import java.util.Calendar

/**
 * ReportFilter - Clase de datos que contiene los filtros y criterios para generar un reporte
 *
 * Esta clase define todos los parámetros que el usuario puede seleccionar
 * antes de generar un reporte de asistencias.
 */
data class ReportFilter(
    // Rango de fechas (OBLIGATORIO)
    val startDate: Date,
    val endDate: Date,

    // Filtro por personas
    val personIds: List<String>? = null, // null = todas las personas
    val personNames: List<String>? = null, // Para mostrar en UI

    // Filtro por zonas
    val zoneIds: List<String>? = null, // null = todas las zonas
    val zoneNames: List<String>? = null, // Para mostrar en UI

    // Tipo de reporte
    val reportType: String = "GENERAL", // MONTHLY, WEEKLY, DAILY, HOURS, LATE, GENERAL

    // Agrupación de datos
    val groupBy: String = "PERSON", // PERSON, DAY, ZONE, NONE

    // Opciones de contenido
    val includeHours: Boolean = true,
    val includeLateArrivals: Boolean = true,
    val includeAbsences: Boolean = true,
    val includePhotos: Boolean = false,
    val includeNotes: Boolean = false,

    // Ordenamiento
    val sortBy: String = "DATE", // DATE, PERSON, ZONE, HOURS
    val sortOrder: String = "ASC", // ASC, DESC

    // Filtros adicionales
    val onlyLateArrivals: Boolean = false, // Solo mostrar atrasos
    val onlyAbsences: Boolean = false, // Solo mostrar ausencias
    val minHoursWorked: Double? = null, // Filtrar por horas mínimas
    val maxHoursWorked: Double? = null, // Filtrar por horas máximas

    // Plantilla usada (si aplica)
    val templateUsed: ReportTemplate? = null,

    // Metadatos
    val createdDate: Date = Date(),
    val filterName: String? = null // Nombre personalizado del filtro
) {

    /**
     * Verifica si el filtro incluye todas las personas
     */
    fun includesAllPersons(): Boolean {
        return personIds == null || personIds.isEmpty()
    }

    /**
     * Verifica si el filtro incluye todas las zonas
     */
    fun includesAllZones(): Boolean {
        return zoneIds == null || zoneIds.isEmpty()
    }

    /**
     * Obtiene el rango de fechas en formato legible
     */
    fun getDateRangeText(): String {
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
    }

    /**
     * Obtiene la cantidad de días en el rango
     */
    fun getDaysInRange(): Int {
        val diffInMillis = endDate.time - startDate.time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    /**
     * Obtiene el texto de personas seleccionadas
     */
    fun getPersonsText(): String {
        return when {
            includesAllPersons() -> "Todas las personas"
            personNames != null && personNames.size == 1 -> personNames[0]
            personNames != null -> "${personNames.size} personas seleccionadas"
            personIds != null -> "${personIds.size} personas seleccionadas"
            else -> "Todas las personas"
        }
    }

    /**
     * Obtiene el texto de zonas seleccionadas
     */
    fun getZonesText(): String {
        return when {
            includesAllZones() -> "Todas las zonas"
            zoneNames != null && zoneNames.size == 1 -> zoneNames[0]
            zoneNames != null -> "${zoneNames.size} zonas seleccionadas"
            zoneIds != null -> "${zoneIds.size} zonas seleccionadas"
            else -> "Todas las zonas"
        }
    }

    /**
     * Obtiene el tipo de reporte en texto legible
     */
    fun getReportTypeText(): String {
        return when (reportType) {
            "MONTHLY" -> "Reporte Mensual"
            "WEEKLY" -> "Reporte Semanal"
            "DAILY" -> "Reporte Diario"
            "HOURS" -> "Reporte de Horas"
            "LATE" -> "Reporte de Atrasos"
            else -> "Reporte General"
        }
    }

    /**
     * Obtiene la agrupación en texto legible
     */
    fun getGroupByText(): String {
        return when (groupBy) {
            "PERSON" -> "Por Persona"
            "DAY" -> "Por Día"
            "ZONE" -> "Por Zona"
            else -> "Sin agrupar"
        }
    }

    /**
     * Verifica si el filtro es válido
     */
    fun isValid(): Boolean {
        // La fecha de inicio debe ser antes o igual a la fecha de fin
        if (startDate.after(endDate)) {
            return false
        }

        // El rango no debe ser mayor a 1 año
        if (getDaysInRange() > 365) {
            return false
        }

        return true
    }

    /**
     * Crea una copia del filtro con un rango de fechas diferente
     */
    fun withDateRange(newStartDate: Date, newEndDate: Date): ReportFilter {
        return this.copy(startDate = newStartDate, endDate = newEndDate)
    }

    /**
     * Crea una copia del filtro para un mes específico
     */
    fun forMonth(year: Int, month: Int): ReportFilter {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endDate = calendar.time

        return this.copy(
            startDate = startDate,
            endDate = endDate,
            reportType = "MONTHLY"
        )
    }

    /**
     * Crea una copia del filtro para una semana específica
     */
    fun forWeek(startOfWeek: Date): ReportFilter {
        val calendar = Calendar.getInstance()
        calendar.time = startOfWeek
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endDate = calendar.time

        return this.copy(
            startDate = startDate,
            endDate = endDate,
            reportType = "WEEKLY"
        )
    }

    /**
     * Obtiene un resumen del filtro como Map para debugging o logging
     */
    fun toSummaryMap(): Map<String, Any?> {
        return mapOf(
            "dateRange" to getDateRangeText(),
            "daysInRange" to getDaysInRange(),
            "persons" to getPersonsText(),
            "zones" to getZonesText(),
            "reportType" to getReportTypeText(),
            "groupBy" to getGroupByText(),
            "includeHours" to includeHours,
            "includeLateArrivals" to includeLateArrivals,
            "includeAbsences" to includeAbsences
        )
    }

    companion object {
        /**
         * Crea un filtro por defecto para el mes actual
         */
        fun currentMonth(): ReportFilter {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            calendar.set(year, month, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.time

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endDate = calendar.time

            return ReportFilter(
                startDate = startDate,
                endDate = endDate,
                reportType = "MONTHLY",
                groupBy = "PERSON"
            )
        }

        /**
         * Crea un filtro por defecto para la semana actual
         */
        fun currentWeek(): ReportFilter {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, 6)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endDate = calendar.time

            return ReportFilter(
                startDate = startDate,
                endDate = endDate,
                reportType = "WEEKLY",
                groupBy = "DAY"
            )
        }

        /**
         * Crea un filtro por defecto para el día actual
         */
        fun today(): ReportFilter {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.time

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endDate = calendar.time

            return ReportFilter(
                startDate = startDate,
                endDate = endDate,
                reportType = "DAILY",
                groupBy = "PERSON"
            )
        }
    }
}