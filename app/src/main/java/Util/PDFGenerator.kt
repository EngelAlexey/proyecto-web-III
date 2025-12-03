package com.example.clocker.Util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.clocker.Data.Report
import com.example.clocker.Data.AttendanceRecord
import com.example.clocker.Data.PersonSummary
import com.example.clocker.Data.DaySummary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * PDFGenerator - Generador de archivos PDF para reportes
 *
 * Crea documentos PDF profesionales con encabezado de empresa,
 * filtros aplicados y datos del reporte formateados.
 */
object PDFGenerator {

    // Formatos de fecha y hora
    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    private val timeFmt = SimpleDateFormat("HH:mm", Locale("es", "ES"))
    private val dateTimeFmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))

    // Configuración de página (A4: 595x842 puntos)
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_LEFT = 40f
    private const val MARGIN_RIGHT = 40f
    private const val MARGIN_TOP = 60f
    private const val LINE_HEIGHT = 18f

    /**
     * Genera un archivo PDF completo del reporte
     *
     * @param context Contexto de Android
     * @param report Reporte a exportar
     * @return File del PDF generado
     */
    fun generatePDF(context: Context, report: Report): File {
        val pdf = PdfDocument()
        var currentPage = 1
        var yPosition = MARGIN_TOP

        // Crear primera página
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPage).create()
        var page = pdf.startPage(pageInfo)
        var canvas = page.canvas

        // Dibujar encabezado de empresa
        yPosition = drawHeader(canvas, report, yPosition)

        // Dibujar filtros aplicados
        yPosition = drawFilters(canvas, report, yPosition)

        // ✅ NUEVA TABLA DETALLADA - Reemplaza el resumen general
        if (report.attendanceRecords.isNotEmpty()) {
            yPosition = drawDetailedAttendanceTable(canvas, report.attendanceRecords, yPosition)
        } else {
            // Si no hay registros, mostrar mensaje
            canvas.drawText(
                "No hay registros de asistencia en el período seleccionado",
                MARGIN_LEFT,
                yPosition,
                Paint().apply {
                    textSize = 12f
                    color = Color.GRAY
                }
            )
        }

        // Dibujar pie de página
        drawFooter(canvas, currentPage, report.generatedDate)

        pdf.finishPage(page)

        // Guardar archivo
        val fileName = report.getSuggestedPDFFileName()
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, fileName)

        FileOutputStream(file).use {
            pdf.writeTo(it)
        }

        pdf.close()
        return file
    }

    /**
     * Dibuja el encabezado del documento
     */
    private fun drawHeader(canvas: Canvas, report: Report, startY: Float): Float {
        var y = startY

        val titlePaint = Paint().apply {
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val subtitlePaint = Paint().apply {
            textSize = 14f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        // Nombre de la empresa
        canvas.drawText(report.companyName, MARGIN_LEFT, y, titlePaint)
        y += 30f

        // Título del reporte
        canvas.drawText(report.name, MARGIN_LEFT, y, titlePaint)
        y += 25f

        // Fecha de generación
        canvas.drawText(
            "Generado: ${dateTimeFmt.format(report.generatedDate)}",
            MARGIN_LEFT,
            y,
            subtitlePaint
        )
        y += 30f

        // Línea separadora
        canvas.drawLine(MARGIN_LEFT, y, PAGE_WIDTH - MARGIN_RIGHT, y, Paint().apply {
            strokeWidth = 2f
            color = Color.LTGRAY
        })
        y += 20f

        return y
    }

    /**
     * Dibuja los filtros aplicados al reporte
     */
    private fun drawFilters(canvas: Canvas, report: Report, startY: Float): Float {
        var y = startY

        val labelPaint = Paint().apply {
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val valuePaint = Paint().apply {
            textSize = 11f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        // Título de sección
        canvas.drawText("FILTROS APLICADOS:", MARGIN_LEFT, y, labelPaint)
        y += LINE_HEIGHT

        // Rango de fechas
        canvas.drawText("Período:", MARGIN_LEFT + 10f, y, labelPaint)
        canvas.drawText(report.getDateRangeString(), MARGIN_LEFT + 100f, y, valuePaint)
        y += LINE_HEIGHT

        // Personas
        canvas.drawText("Personas:", MARGIN_LEFT + 10f, y, labelPaint)
        val personsText = if (report.filter.includesAllPersons()) "Todas"
        else "${report.totalPersons} seleccionadas"
        canvas.drawText(personsText, MARGIN_LEFT + 100f, y, valuePaint)
        y += LINE_HEIGHT

        // Zonas
        canvas.drawText("Zonas:", MARGIN_LEFT + 10f, y, labelPaint)
        val zonesText = if (report.filter.includesAllZones()) "Todas"
        else report.filter.getZonesText()
        canvas.drawText(zonesText, MARGIN_LEFT + 100f, y, valuePaint)
        y += 25f

        return y
    }

    /**
     * ✅ NUEVA TABLA DETALLADA CON TODA LA INFORMACIÓN
     * Muestra: Nombre, Fecha Entrada, Hora Entrada, Distancia, Biometría,
     *          Fecha Salida, Hora Salida, Horas Trabajadas
     */
    private fun drawDetailedAttendanceTable(
        canvas: Canvas,
        records: List<AttendanceRecord>,
        startY: Float
    ): Float {
        var y = startY

        val titlePaint = Paint().apply {
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val headerPaint = Paint().apply {
            textSize = 8f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.WHITE
        }

        val cellPaint = Paint().apply {
            textSize = 7f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#673AB7") // Color morado
        }

        val linePaint = Paint().apply {
            strokeWidth = 1f
            color = Color.LTGRAY
        }

        // Título de sección
        canvas.drawText("DETALLE DE ASISTENCIAS:", MARGIN_LEFT, y, titlePaint)
        y += LINE_HEIGHT + 5f

        // Dibujar fondo de encabezado
        canvas.drawRect(
            MARGIN_LEFT,
            y - 12f,
            PAGE_WIDTH - MARGIN_RIGHT,
            y + 5f,
            backgroundPaint
        )

        // Encabezados de tabla
        canvas.drawText("Nombre", MARGIN_LEFT + 2f, y, headerPaint)
        canvas.drawText("F.Entrada", MARGIN_LEFT + 100f, y, headerPaint)
        canvas.drawText("H.Entrada", MARGIN_LEFT + 180f, y, headerPaint)
        canvas.drawText("F.Salida", MARGIN_LEFT + 250f, y, headerPaint)
        canvas.drawText("H.Salida", MARGIN_LEFT + 330f, y, headerPaint)
        canvas.drawText("Horas", MARGIN_LEFT + 400f, y, headerPaint)
        canvas.drawText("Estado", MARGIN_LEFT + 460f, y, headerPaint)
        y += 18f

        // Línea separadora después del encabezado
        canvas.drawLine(MARGIN_LEFT, y, PAGE_WIDTH - MARGIN_RIGHT, y, linePaint)
        y += 5f

        // Datos de cada registro
        records.forEach { record ->
            // Nombre (truncado si es muy largo)
            val displayName = if (record.personName.length > 15) {
                record.personName.substring(0, 15) + "..."
            } else {
                record.personName
            }
            canvas.drawText(displayName, MARGIN_LEFT + 2f, y, cellPaint)

            // Fecha de entrada (REAL de clockIn)
            val entryDate = if (record.clockIn != null) {
                SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")).format(record.clockIn)
            } else {
                "--/--/----"
            }
            canvas.drawText(entryDate, MARGIN_LEFT + 100f, y, cellPaint)

            // Hora de entrada
            canvas.drawText(record.getFormattedClockIn(), MARGIN_LEFT + 180f, y, cellPaint)

            // Fecha de salida (REAL de clockOut)
            val exitDate = if (record.clockOut != null) {
                SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")).format(record.clockOut)
            } else {
                "--/--/----"
            }
            canvas.drawText(exitDate, MARGIN_LEFT + 250f, y, cellPaint)

            // Hora de salida
            canvas.drawText(record.getFormattedClockOut(), MARGIN_LEFT + 330f, y, cellPaint)

            // Horas trabajadas
            canvas.drawText(record.getFormattedHours(), MARGIN_LEFT + 400f, y, cellPaint)

            // Estado (A tiempo / Tarde)
            val statusText = if (record.isLateArrival) "Tarde" else "A tiempo"
            val statusColor = if (record.isLateArrival) {
                Color.parseColor("#F44336") // Rojo
            } else {
                Color.parseColor("#4CAF50") // Verde
            }
            canvas.drawText(statusText, MARGIN_LEFT + 460f, y, Paint().apply {
                textSize = 7f
                color = statusColor
                typeface = Typeface.DEFAULT_BOLD
            })

            y += LINE_HEIGHT

            // Línea separadora entre registros
            canvas.drawLine(MARGIN_LEFT, y, PAGE_WIDTH - MARGIN_RIGHT, y, Paint().apply {
                strokeWidth = 0.5f
                color = Color.LTGRAY
            })
            y += 3f

            // Si llegamos al final de la página, aquí podrías crear una nueva página
            // (por ahora limitado a una página)
        }

        y += 20f
        return y
    }

    /**
     * Dibuja el pie de página
     */
    private fun drawFooter(canvas: Canvas, pageNumber: Int, generatedDate: Date) {
        val footerPaint = Paint().apply {
            textSize = 9f
            typeface = Typeface.DEFAULT
            color = Color.GRAY
        }

        val footerY = PAGE_HEIGHT - 30f

        // Número de página
        canvas.drawText("Página $pageNumber", MARGIN_LEFT, footerY, footerPaint)

        // Fecha de generación
        canvas.drawText(
            "Generado: ${dateTimeFmt.format(generatedDate)}",
            PAGE_WIDTH - 200f,
            footerY,
            footerPaint
        )
    }

    /**
     * Formatea horas en formato "Xh Ym"
     */
    private fun formatHours(hours: Double): String {
        val h = hours.toInt()
        val m = ((hours - h) * 60).toInt()
        return "${h}h ${m}m"
    }

    // ========== MÉTODOS ANTIGUOS (por si los necesitas después) ==========

    private fun drawSummary(canvas: Canvas, report: Report, startY: Float): Float {
        var y = startY

        val titlePaint = Paint().apply {
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val valuePaint = Paint().apply {
            textSize = 11f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        canvas.drawText("RESUMEN GENERAL:", MARGIN_LEFT, y, titlePaint)
        y += LINE_HEIGHT

        canvas.drawText("Total de registros:", MARGIN_LEFT + 10f, y, valuePaint)
        canvas.drawText(report.totalRecords.toString(), MARGIN_LEFT + 200f, y, valuePaint)
        y += LINE_HEIGHT

        canvas.drawText("Total de personas:", MARGIN_LEFT + 10f, y, valuePaint)
        canvas.drawText(report.totalPersons.toString(), MARGIN_LEFT + 200f, y, valuePaint)
        y += LINE_HEIGHT

        if (report.filter.includeHours) {
            canvas.drawText("Total horas trabajadas:", MARGIN_LEFT + 10f, y, valuePaint)
            canvas.drawText(formatHours(report.totalHoursWorked), MARGIN_LEFT + 200f, y, valuePaint)
            y += LINE_HEIGHT
        }

        if (report.filter.includeLateArrivals) {
            canvas.drawText("Total de atrasos:", MARGIN_LEFT + 10f, y, valuePaint)
            canvas.drawText(report.totalLateArrivals.toString(), MARGIN_LEFT + 200f, y, valuePaint)
            y += LINE_HEIGHT
        }

        if (report.filter.includeAbsences) {
            canvas.drawText("Total de ausencias:", MARGIN_LEFT + 10f, y, valuePaint)
            canvas.drawText(report.totalAbsences.toString(), MARGIN_LEFT + 200f, y, valuePaint)
            y += LINE_HEIGHT
        }

        y += 20f
        return y
    }

    private fun drawPersonSummaryTable(canvas: Canvas, summaries: List<PersonSummary>, startY: Float): Float {
        var y = startY

        val headerPaint = Paint().apply {
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val cellPaint = Paint().apply {
            textSize = 9f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        canvas.drawText("RESUMEN POR PERSONA:", MARGIN_LEFT, y, headerPaint)
        y += LINE_HEIGHT

        canvas.drawText("Nombre", MARGIN_LEFT, y, headerPaint)
        canvas.drawText("Días", MARGIN_LEFT + 180f, y, headerPaint)
        canvas.drawText("Presente", MARGIN_LEFT + 230f, y, headerPaint)
        canvas.drawText("Ausente", MARGIN_LEFT + 300f, y, headerPaint)
        canvas.drawText("Atrasos", MARGIN_LEFT + 370f, y, headerPaint)
        canvas.drawText("Horas", MARGIN_LEFT + 430f, y, headerPaint)
        y += LINE_HEIGHT

        canvas.drawLine(MARGIN_LEFT, y, PAGE_WIDTH - MARGIN_RIGHT, y, Paint().apply {
            strokeWidth = 1f
            color = Color.LTGRAY
        })
        y += 5f

        summaries.forEach { summary ->
            canvas.drawText(summary.personName, MARGIN_LEFT, y, cellPaint)
            canvas.drawText(summary.totalDays.toString(), MARGIN_LEFT + 180f, y, cellPaint)
            canvas.drawText(summary.presentDays.toString(), MARGIN_LEFT + 230f, y, cellPaint)
            canvas.drawText(summary.absentDays.toString(), MARGIN_LEFT + 300f, y, cellPaint)
            canvas.drawText(summary.lateArrivals.toString(), MARGIN_LEFT + 370f, y, cellPaint)
            canvas.drawText(summary.getFormattedTotalHours(), MARGIN_LEFT + 430f, y, cellPaint)
            y += LINE_HEIGHT
        }

        y += 20f
        return y
    }

    private fun drawDaySummaryTable(canvas: Canvas, summaries: List<DaySummary>, startY: Float): Float {
        var y = startY

        val headerPaint = Paint().apply {
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val cellPaint = Paint().apply {
            textSize = 9f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        canvas.drawText("RESUMEN POR DÍA:", MARGIN_LEFT, y, headerPaint)
        y += LINE_HEIGHT

        canvas.drawText("Fecha", MARGIN_LEFT, y, headerPaint)
        canvas.drawText("Total", MARGIN_LEFT + 150f, y, headerPaint)
        canvas.drawText("Presente", MARGIN_LEFT + 220f, y, headerPaint)
        canvas.drawText("Ausente", MARGIN_LEFT + 300f, y, headerPaint)
        canvas.drawText("Atrasos", MARGIN_LEFT + 380f, y, headerPaint)
        y += LINE_HEIGHT

        summaries.forEach { summary ->
            canvas.drawText(summary.getFormattedDate(), MARGIN_LEFT, y, cellPaint)
            canvas.drawText(summary.totalPersons.toString(), MARGIN_LEFT + 150f, y, cellPaint)
            canvas.drawText(summary.presentPersons.toString(), MARGIN_LEFT + 220f, y, cellPaint)
            canvas.drawText(summary.absentPersons.toString(), MARGIN_LEFT + 300f, y, cellPaint)
            canvas.drawText(summary.lateArrivals.toString(), MARGIN_LEFT + 380f, y, cellPaint)
            y += LINE_HEIGHT
        }

        y += 20f
        return y
    }

    private fun drawAttendanceDetailTable(canvas: Canvas, records: List<AttendanceRecord>, startY: Float): Float {
        var y = startY

        val headerPaint = Paint().apply {
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val cellPaint = Paint().apply {
            textSize = 9f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        canvas.drawText("DETALLE DE ASISTENCIAS:", MARGIN_LEFT, y, headerPaint)
        y += LINE_HEIGHT

        canvas.drawText("Fecha", MARGIN_LEFT, y, headerPaint)
        canvas.drawText("Persona", MARGIN_LEFT + 100f, y, headerPaint)
        canvas.drawText("Entrada", MARGIN_LEFT + 250f, y, headerPaint)
        canvas.drawText("Salida", MARGIN_LEFT + 330f, y, headerPaint)
        canvas.drawText("Horas", MARGIN_LEFT + 410f, y, headerPaint)
        y += LINE_HEIGHT

        records.take(30).forEach { record ->
            canvas.drawText(record.getFormattedDate(), MARGIN_LEFT, y, cellPaint)
            canvas.drawText(record.personName, MARGIN_LEFT + 100f, y, cellPaint)
            canvas.drawText(record.getFormattedClockIn(), MARGIN_LEFT + 250f, y, cellPaint)
            canvas.drawText(record.getFormattedClockOut(), MARGIN_LEFT + 330f, y, cellPaint)
            canvas.drawText(record.getFormattedHours(), MARGIN_LEFT + 410f, y, cellPaint)
            y += LINE_HEIGHT
        }

        y += 20f
        return y
    }

    private fun drawHoursTable(canvas: Canvas, summaries: List<PersonSummary>, startY: Float): Float {
        var y = startY

        val headerPaint = Paint().apply {
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val cellPaint = Paint().apply {
            textSize = 9f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        canvas.drawText("HORAS TRABAJADAS POR PERSONA:", MARGIN_LEFT, y, headerPaint)
        y += LINE_HEIGHT

        canvas.drawText("Persona", MARGIN_LEFT, y, headerPaint)
        canvas.drawText("Total Horas", MARGIN_LEFT + 250f, y, headerPaint)
        canvas.drawText("Promedio/Día", MARGIN_LEFT + 370f, y, headerPaint)
        y += LINE_HEIGHT

        summaries.forEach { summary ->
            canvas.drawText(summary.personName, MARGIN_LEFT, y, cellPaint)
            canvas.drawText(summary.getFormattedTotalHours(), MARGIN_LEFT + 250f, y, cellPaint)
            canvas.drawText(formatHours(summary.averageHoursPerDay), MARGIN_LEFT + 370f, y, cellPaint)
            y += LINE_HEIGHT
        }

        y += 20f
        return y
    }

    private fun drawLateArrivalsTable(canvas: Canvas, records: List<AttendanceRecord>, startY: Float): Float {
        var y = startY

        val headerPaint = Paint().apply {
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.BLACK
        }

        val cellPaint = Paint().apply {
            textSize = 9f
            typeface = Typeface.DEFAULT
            color = Color.DKGRAY
        }

        canvas.drawText("ATRASOS REGISTRADOS:", MARGIN_LEFT, y, headerPaint)
        y += LINE_HEIGHT

        canvas.drawText("Fecha", MARGIN_LEFT, y, headerPaint)
        canvas.drawText("Persona", MARGIN_LEFT + 100f, y, headerPaint)
        canvas.drawText("Entrada", MARGIN_LEFT + 250f, y, headerPaint)
        canvas.drawText("Retraso", MARGIN_LEFT + 350f, y, headerPaint)
        y += LINE_HEIGHT

        records.forEach { record ->
            canvas.drawText(record.getFormattedDate(), MARGIN_LEFT, y, cellPaint)
            canvas.drawText(record.personName, MARGIN_LEFT + 100f, y, cellPaint)
            canvas.drawText(record.getFormattedClockIn(), MARGIN_LEFT + 250f, y, cellPaint)
            canvas.drawText("${record.lateMinutes} min", MARGIN_LEFT + 350f, y, cellPaint)
            y += LINE_HEIGHT
        }

        y += 20f
        return y
    }
}