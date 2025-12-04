package com.example.clocker.Util

import com.example.clocker.Data.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * ReportFormatter - Utilidad para formatear datos de reportes
 *
 * Proporciona métodos estáticos para formatear fechas, horas, números
 * y otros datos para mostrar en la UI de manera consistente.
 */
object ReportFormatter {

    // Formatos de fecha y hora
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("es", "ES"))
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
    private val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))

    // Formato de números
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val percentFormat = DecimalFormat("#0.0")

    /**
     * Formatea una fecha como String
     * @param date Fecha a formatear
     * @param pattern Patrón personalizado (opcional)
     * @return String formateado "dd/MM/yyyy"
     */
    fun formatDate(date: Date?, pattern: String? = null): String {
        if (date == null) return "--/--/----"

        return if (pattern != null) {
            SimpleDateFormat(pattern, Locale("es", "ES")).format(date)
        } else {
            dateFormat.format(date)
        }
    }

    /**
     * Formatea una hora como String
     * @param time Hora a formatear
     * @return String formateado "HH:mm"
     */
    fun formatTime(time: Date?): String {
        return time?.let { timeFormat.format(it) } ?: "--:--"
    }

    /**
     * Formatea fecha y hora juntos
     * @param dateTime Fecha y hora a formatear
     * @return String formateado "dd/MM/yyyy HH:mm"
     */
    fun formatDateTime(dateTime: Date?): String {
        return dateTime?.let { dateTimeFormat.format(it) } ?: "--/--/---- --:--"
    }

    /**
     * Formatea el día de la semana
     * @param date Fecha
     * @return String "Lunes", "Martes", etc.
     */
    fun formatDayOfWeek(date: Date): String {
        return dayOfWeekFormat.format(date).capitalize()
    }

    /**
     * Formatea mes y año
     * @param date Fecha
     * @return String "Abril 2025"
     */
    fun formatMonthYear(date: Date): String {
        return monthYearFormat.format(date).capitalize()
    }

    /**
     * Formatea un rango de fechas
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @return String "01/04/2025 - 30/04/2025"
     */
    fun formatDateRange(startDate: Date, endDate: Date): String {
        return "${formatDate(startDate)} - ${formatDate(endDate)}"
    }

    /**
     * Formatea horas en formato "Xh Ym"
     * @param hours Horas en formato decimal (ej: 8.5)
     * @return String "8h 30m"
     */
    fun formatHours(hours: Double): String {
        val h = hours.toInt()
        val m = ((hours - h) * 60).toInt()
        return "${h}h ${m.toString().padStart(2, '0')}m"
    }

    /**
     * Formatea minutos en formato "Xh Ym"
     * @param minutes Minutos totales
     * @return String "8h 30m"
     */
    fun formatMinutesToHours(minutes: Long): String {
        val h = minutes / 60
        val m = minutes % 60
        return "${h}h ${m.toString().padStart(2, '0')}m"
    }

    /**
     * Formatea minutos como decimal de horas
     * @param minutes Minutos totales
     * @return Double (ej: 510 minutos = 8.5 horas)
     */
    fun minutesToHours(minutes: Long): Double {
        return minutes / 60.0
    }

    /**
     * Formatea porcentaje
     * @param value Valor del porcentaje (0-100)
     * @return String "85.5%"
     */
    fun formatPercentage(value: Double): String {
        return "${percentFormat.format(value)}%"
    }

    /**
     * Formatea número decimal
     * @param value Valor numérico
     * @return String "1,234.56"
     */
    fun formatDecimal(value: Double): String {
        return decimalFormat.format(value)
    }

    /**
     * Formatea el estado de un reporte
     * @param status Estado del reporte
     * @return String traducido
     */
    fun formatReportStatus(status: ReportStatus): String {
        return when (status) {
            ReportStatus.PENDING -> "Pendiente"
            ReportStatus.GENERATING -> "Generando..."
            ReportStatus.COMPLETED -> "Completado"
            ReportStatus.ERROR -> "Error"
            ReportStatus.EXPORTED -> "Exportado"
        }
    }

    /**
     * Formatea el tipo de reporte
     * @param reportType Tipo de reporte
     * @return String traducido y formateado
     */
    fun formatReportType(reportType: String): String {
        return when (reportType.uppercase()) {
            "MONTHLY" -> "Mensual"
            "WEEKLY" -> "Semanal"
            "DAILY" -> "Diario"
            "HOURS" -> "Horas Trabajadas"
            "LATE" -> "Atrasos"
            "GENERAL" -> "General"
            else -> reportType
        }
    }

    /**
     * Formatea una plantilla de reporte
     * @param template Plantilla
     * @return String del nombre de la plantilla
     */
    fun formatTemplate(template: ReportTemplate): String {
        return template.templateName
    }

    /**
     * Formatea un registro de asistencia para mostrar en lista
     * @param record Registro de asistencia
     * @return String formateado "Juan Pérez - 01/04/2025 - 8h 30m"
     */
    fun formatAttendanceRecord(record: AttendanceRecord): String {
        return "${record.personName} - ${formatDate(record.date)} - ${formatHours(record.hoursWorked)}"
    }

    /**
     * Formatea un resumen de persona para mostrar en lista
     * @param summary Resumen de persona
     * @return String formateado "Juan Pérez - 20 días - 160h 30m"
     */
    fun formatPersonSummary(summary: PersonSummary): String {
        return "${summary.personName} - ${summary.totalDays} días - ${summary.getFormattedTotalHours()}"
    }

    /**
     * Formatea un resumen de día para mostrar en lista
     * @param summary Resumen de día
     * @return String formateado "Lunes 01/04/2025 - 15 presentes"
     */
    fun formatDaySummary(summary: DaySummary): String {
        return "${formatDayOfWeek(summary.date)} ${formatDate(summary.date)} - ${summary.presentPersons} presentes"
    }

    /**
     * Obtiene un resumen corto del reporte
     * @param report Reporte
     * @return String "Reporte Mensual - Abril 2025 - 15 personas"
     */
    fun getReportShortSummary(report: Report): String {
        val type = formatReportType(report.reportType)
        val range = formatDateRange(report.filter.startDate, report.filter.endDate)
        return "$type - $range - ${report.totalPersons} personas"
    }

    /**
     * Obtiene un resumen detallado del reporte
     * @param report Reporte
     * @return String multilínea con detalles
     */
    fun getReportDetailedSummary(report: Report): String {
        return buildString {
            appendLine("Tipo: ${formatReportType(report.reportType)}")
            appendLine("Período: ${formatDateRange(report.filter.startDate, report.filter.endDate)}")
            appendLine("Registros: ${report.totalRecords}")
            appendLine("Personas: ${report.totalPersons}")

            if (report.filter.includeHours) {
                appendLine("Horas totales: ${formatHours(report.totalHoursWorked)}")
            }

            if (report.filter.includeLateArrivals) {
                appendLine("Atrasos: ${report.totalLateArrivals}")
            }

            if (report.filter.includeAbsences) {
                appendLine("Ausencias: ${report.totalAbsences}")
            }
        }.trim()
    }

    /**
     * Formatea el estado de asistencia
     * @param isPresent Si está presente
     * @param isLate Si llegó tarde
     * @return String "Presente", "Tarde", "Ausente"
     */
    fun formatAttendanceStatus(isPresent: Boolean, isLate: Boolean = false): String {
        return when {
            !isPresent -> "Ausente"
            isLate -> "Tarde"
            else -> "Presente"
        }
    }

    /**
     * Formatea minutos de retraso
     * @param lateMinutes Minutos de retraso
     * @return String "30 min" o "1h 15m"
     */
    fun formatLateMinutes(lateMinutes: Int): String {
        return if (lateMinutes < 60) {
            "$lateMinutes min"
        } else {
            val hours = lateMinutes / 60
            val mins = lateMinutes % 60
            "${hours}h ${mins}m"
        }
    }

    /**
     * Obtiene el color para el estado de asistencia (nombre del recurso)
     * @param isPresent Si está presente
     * @param isLate Si llegó tarde
     * @return String nombre del color "green", "orange", "red"
     */
    fun getAttendanceStatusColor(isPresent: Boolean, isLate: Boolean = false): String {
        return when {
            !isPresent -> "red"
            isLate -> "orange"
            else -> "green"
        }
    }

    /**
     * Formatea el tamaño de archivo
     * @param bytes Tamaño en bytes
     * @return String "1.5 MB", "250 KB", etc.
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    /**
     * Formatea tiempo relativo
     * @param date Fecha
     * @return String "Hace 2 horas", "Hace 3 días", etc.
     */
    fun formatRelativeTime(date: Date): String {
        val now = Date()
        val diffMillis = now.time - date.time
        val diffSeconds = diffMillis / 1000
        val diffMinutes = diffSeconds / 60
        val diffHours = diffMinutes / 60
        val diffDays = diffHours / 24

        return when {
            diffSeconds < 60 -> "Hace un momento"
            diffMinutes < 60 -> "Hace ${diffMinutes} min"
            diffHours < 24 -> "Hace ${diffHours} horas"
            diffDays < 7 -> "Hace ${diffDays} días"
            diffDays < 30 -> "Hace ${diffDays / 7} semanas"
            diffDays < 365 -> "Hace ${diffDays / 30} meses"
            else -> "Hace ${diffDays / 365} años"
        }
    }

    /**
     * Capitaliza la primera letra de un String
     * @param text Texto a capitalizar
     * @return String capitalizado
     */
    fun capitalize(text: String): String {
        return text.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString()
        }
    }

    /**
     * Trunca un texto largo
     * @param text Texto
     * @param maxLength Longitud máxima
     * @return String truncado con "..."
     */
    fun truncate(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            "${text.take(maxLength - 3)}..."
        } else {
            text
        }
    }

    /**
     * Formatea una lista de nombres
     * @param names Lista de nombres
     * @param maxItems Cantidad máxima a mostrar
     * @return String "Juan, María y 3 más"
     */
    fun formatNameList(names: List<String>, maxItems: Int = 3): String {
        return when {
            names.isEmpty() -> "Ninguno"
            names.size <= maxItems -> names.joinToString(", ")
            else -> {
                val visible = names.take(maxItems).joinToString(", ")
                val remaining = names.size - maxItems
                "$visible y $remaining más"
            }
        }
    }

    /**
     * Formatea un mapa de estadísticas
     * @param stats Mapa de estadísticas
     * @return String multilínea formateado
     */
    fun formatStatistics(stats: Map<String, Any>): String {
        return stats.entries.joinToString("\n") { (key, value) ->
            val formattedKey = key.replace("_", " ").capitalize()
            val formattedValue = when (value) {
                is Double -> formatDecimal(value)
                is Float -> formatDecimal(value.toDouble())
                is Date -> formatDateTime(value)
                else -> value.toString()
            }
            "$formattedKey: $formattedValue"
        }
    }
}