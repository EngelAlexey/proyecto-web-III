package com.example.clocker.Data

/**
 * ReportTemplate - Plantillas predefinidas de reportes
 *
 * Este enum define los tipos de reportes predefinidos que el usuario
 * puede seleccionar rápidamente sin tener que configurar todos los filtros.
 */
enum class ReportTemplate(
    val templateName: String,
    val description: String,
    val icon: String, // Nombre del icono drawable
    val defaultGroupBy: String,
    val includeHours: Boolean,
    val includeLateArrivals: Boolean,
    val includeAbsences: Boolean
) {
    /**
     * Reporte Mensual por Persona
     * Muestra un resumen completo de cada persona durante todo el mes
     */
    MONTHLY_BY_PERSON(
        templateName = "Reporte Mensual por Persona",
        description = "Resumen completo de asistencias del mes, agrupado por persona. Incluye horas trabajadas, atrasos y ausencias.",
        icon = "ic_calendar_month",
        defaultGroupBy = "PERSON",
        includeHours = true,
        includeLateArrivals = true,
        includeAbsences = true
    ),

    /**
     * Resumen Semanal
     * Muestra un resumen de la semana con estadísticas generales
     */
    WEEKLY_SUMMARY(
        templateName = "Resumen Semanal",
        description = "Resumen de asistencias de la semana con estadísticas generales y totales de horas trabajadas.",
        icon = "ic_calendar_week",
        defaultGroupBy = "SUMMARY",
        includeHours = true,
        includeLateArrivals = true,
        includeAbsences = true
    ),

    /**
     * Detalle Diario
     * Muestra el detalle de asistencias día por día
     */
    DAILY_DETAIL(
        templateName = "Detalle Diario",
        description = "Listado detallado de todas las marcas de entrada y salida, día por día.",
        icon = "ic_calendar_today",
        defaultGroupBy = "DAY",
        includeHours = true,
        includeLateArrivals = false,
        includeAbsences = false
    ),

    /**
     * Total de Horas Trabajadas
     * Enfocado en mostrar las horas trabajadas por cada persona
     */
    HOURS_WORKED(
        templateName = "Total de Horas Trabajadas",
        description = "Reporte enfocado en el total de horas trabajadas por cada colaborador en el periodo seleccionado.",
        icon = "ic_schedule",
        defaultGroupBy = "PERSON",
        includeHours = true,
        includeLateArrivals = false,
        includeAbsences = false
    ),

    /**
     * Reporte de Atrasos
     * Muestra solo los atrasos y minutos acumulados
     */
    LATE_ARRIVALS(
        templateName = "Reporte de Atrasos",
        description = "Listado de todos los atrasos registrados, con detalle de minutos de retraso por persona.",
        icon = "ic_warning",
        defaultGroupBy = "PERSON",
        includeHours = false,
        includeLateArrivals = true,
        includeAbsences = false
    );

    /**
     * Obtiene el tipo de reporte asociado a esta plantilla
     */
    fun getReportType(): String {
        return when (this) {
            MONTHLY_BY_PERSON -> "MONTHLY"
            WEEKLY_SUMMARY -> "WEEKLY"
            DAILY_DETAIL -> "DAILY"
            HOURS_WORKED -> "HOURS"
            LATE_ARRIVALS -> "LATE"
        }
    }

    /**
     * Obtiene el nombre corto de la plantilla
     */
    fun getShortName(): String {
        return when (this) {
            MONTHLY_BY_PERSON -> "Mensual"
            WEEKLY_SUMMARY -> "Semanal"
            DAILY_DETAIL -> "Diario"
            HOURS_WORKED -> "Horas"
            LATE_ARRIVALS -> "Atrasos"
        }
    }

    /**
     * Verifica si esta plantilla requiere cálculo de horas
     */
    fun requiresHoursCalculation(): Boolean {
        return includeHours
    }

    /**
     * Verifica si esta plantilla es de tipo resumen
     */
    fun isSummaryType(): Boolean {
        return this == WEEKLY_SUMMARY || this == MONTHLY_BY_PERSON
    }

    /**
     * Verifica si esta plantilla es de tipo detalle
     */
    fun isDetailType(): Boolean {
        return this == DAILY_DETAIL
    }

    /**
     * Obtiene el color asociado a la plantilla (para UI)
     */
    fun getColorResource(): String {
        return when (this) {
            MONTHLY_BY_PERSON -> "report_color_monthly"
            WEEKLY_SUMMARY -> "report_color_weekly"
            DAILY_DETAIL -> "report_color_daily"
            HOURS_WORKED -> "report_color_hours"
            LATE_ARRIVALS -> "report_color_late"
        }
    }

    companion object {
        /**
         * Obtiene todas las plantillas como lista
         */
        fun getAllTemplates(): List<ReportTemplate> {
            return values().toList()
        }

        /**
         * Obtiene una plantilla por su nombre
         */
        fun getByName(name: String): ReportTemplate? {
            return values().find { it.templateName == name }
        }

        /**
         * Obtiene las plantillas más usadas (para mostrar primero en UI)
         */
        fun getMostUsed(): List<ReportTemplate> {
            return listOf(
                MONTHLY_BY_PERSON,
                WEEKLY_SUMMARY,
                HOURS_WORKED
            )
        }

        /**
         * Obtiene las plantillas de resumen
         */
        fun getSummaryTemplates(): List<ReportTemplate> {
            return values().filter { it.isSummaryType() }
        }

        /**
         * Obtiene las plantillas de detalle
         */
        fun getDetailTemplates(): List<ReportTemplate> {
            return values().filter { it.isDetailType() }
        }
    }
}