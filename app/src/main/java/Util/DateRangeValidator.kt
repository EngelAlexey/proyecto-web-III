package com.example.clocker.Util

import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * DateRangeValidator - Utilidad para validar rangos de fechas
 *
 * Proporciona métodos estáticos para validar y trabajar con rangos de fechas
 * en el contexto de generación de reportes.
 */
object DateRangeValidator {

    /**
     * Valida que un rango de fechas sea válido
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return true si el rango es válido
     */
    fun isValidRange(startDate: Date, endDate: Date): Boolean {
        // La fecha de inicio debe ser anterior o igual a la fecha de fin
        return !startDate.after(endDate)
    }

    /**
     * Valida que la fecha de inicio no sea posterior a la fecha de fin
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return true si startDate <= endDate
     */
    fun isStartBeforeOrEqualEnd(startDate: Date, endDate: Date): Boolean {
        return startDate.time <= endDate.time
    }

    /**
     * Valida que ninguna fecha sea futura
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return true si ambas fechas no son futuras
     */
    fun areNotFutureDates(startDate: Date, endDate: Date): Boolean {
        val now = Date()
        return !startDate.after(now) && !endDate.after(now)
    }

    /**
     * Valida que el rango no exceda un número máximo de días
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param maxDays Días máximos permitidos
     * @return true si el rango no excede el máximo
     */
    fun isWithinMaxDays(startDate: Date, endDate: Date, maxDays: Int): Boolean {
        val days = getDaysBetween(startDate, endDate)
        return days <= maxDays
    }

    /**
     * Calcula la cantidad de días entre dos fechas
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Cantidad de días (incluyendo ambos extremos)
     */
    fun getDaysBetween(startDate: Date, endDate: Date): Int {
        val diffInMillis = endDate.time - startDate.time
        return (TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1).toInt()
    }

    /**
     * Calcula la cantidad de horas entre dos fechas
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Cantidad de horas
     */
    fun getHoursBetween(startDate: Date, endDate: Date): Long {
        val diffInMillis = endDate.time - startDate.time
        return TimeUnit.MILLISECONDS.toHours(diffInMillis)
    }

    /**
     * Calcula la cantidad de minutos entre dos fechas
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Cantidad de minutos
     */
    fun getMinutesBetween(startDate: Date, endDate: Date): Long {
        val diffInMillis = endDate.time - startDate.time
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    }

    /**
     * Valida que el rango esté dentro de un año calendario
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return true si ambas fechas están en el mismo año
     */
    fun isWithinSameYear(startDate: Date, endDate: Date): Boolean {
        val calStart = Calendar.getInstance().apply { time = startDate }
        val calEnd = Calendar.getInstance().apply { time = endDate }
        return calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR)
    }

    /**
     * Valida que el rango esté dentro del mismo mes
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return true si ambas fechas están en el mismo mes y año
     */
    fun isWithinSameMonth(startDate: Date, endDate: Date): Boolean {
        val calStart = Calendar.getInstance().apply { time = startDate }
        val calEnd = Calendar.getInstance().apply { time = endDate }
        return calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR) &&
                calStart.get(Calendar.MONTH) == calEnd.get(Calendar.MONTH)
    }

    /**
     * Valida que el rango esté dentro de la misma semana
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return true si ambas fechas están en la misma semana
     */
    fun isWithinSameWeek(startDate: Date, endDate: Date): Boolean {
        val calStart = Calendar.getInstance().apply { time = startDate }
        val calEnd = Calendar.getInstance().apply { time = endDate }
        return calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR) &&
                calStart.get(Calendar.WEEK_OF_YEAR) == calEnd.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Verifica si una fecha está dentro de un rango
     *
     * @param date Fecha a verificar
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return true si la fecha está dentro del rango
     */
    fun isDateInRange(date: Date, startDate: Date, endDate: Date): Boolean {
        return !date.before(startDate) && !date.after(endDate)
    }

    /**
     * Obtiene el primer día del mes para una fecha
     *
     * @param date Fecha de referencia
     * @return Fecha del primer día del mes a las 00:00:00
     */
    fun getFirstDayOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    /**
     * Obtiene el último día del mes para una fecha
     *
     * @param date Fecha de referencia
     * @return Fecha del último día del mes a las 23:59:59
     */
    fun getLastDayOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.time
    }

    /**
     * Obtiene el primer día de la semana para una fecha
     *
     * @param date Fecha de referencia
     * @return Fecha del primer día de la semana a las 00:00:00
     */
    fun getFirstDayOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    /**
     * Obtiene el último día de la semana para una fecha
     *
     * @param date Fecha de referencia
     * @return Fecha del último día de la semana a las 23:59:59
     */
    fun getLastDayOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            add(Calendar.DAY_OF_WEEK, 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.time
    }

    /**
     * Normaliza una fecha al inicio del día (00:00:00)
     *
     * @param date Fecha a normalizar
     * @return Fecha normalizada al inicio del día
     */
    fun normalizeToStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    /**
     * Normaliza una fecha al fin del día (23:59:59)
     *
     * @param date Fecha a normalizar
     * @return Fecha normalizada al fin del día
     */
    fun normalizeToEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.time
    }

    /**
     * Valida completamente un rango de fechas para reportes
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param maxDays Días máximos permitidos (default: 365)
     * @return Pair<Boolean, String> - (es válido, mensaje de error si no es válido)
     */
    fun validateReportDateRange(
        startDate: Date,
        endDate: Date,
        maxDays: Int = 365
    ): Pair<Boolean, String> {

        // Verificar que el rango sea válido
        if (!isValidRange(startDate, endDate)) {
            return Pair(false, "La fecha de inicio debe ser anterior o igual a la fecha de fin")
        }

        // Verificar que no sean fechas futuras
        if (!areNotFutureDates(startDate, endDate)) {
            return Pair(false, "Las fechas no pueden ser futuras")
        }

        // Verificar que no exceda el máximo de días
        if (!isWithinMaxDays(startDate, endDate, maxDays)) {
            return Pair(false, "El rango no puede exceder $maxDays días")
        }

        return Pair(true, "")
    }

    /**
     * Obtiene una lista de todas las fechas en un rango
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de fechas
     */
    fun getAllDatesInRange(startDate: Date, endDate: Date): List<Date> {
        val dates = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = normalizeToStartOfDay(startDate)
        }
        val endCalendar = Calendar.getInstance().apply {
            time = normalizeToStartOfDay(endDate)
        }

        while (!calendar.time.after(endCalendar.time)) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    /**
     * Formatea un rango de fechas como String
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param pattern Patrón de formato (default: "dd/MM/yyyy")
     * @return String formateado "dd/MM/yyyy - dd/MM/yyyy"
     */
    fun formatDateRange(
        startDate: Date,
        endDate: Date,
        pattern: String = "dd/MM/yyyy"
    ): String {
        val dateFormat = java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault())
        return "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
    }
}