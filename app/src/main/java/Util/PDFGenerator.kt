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
    private const val LINE_HEIGHT = 20f

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

        // Dibujar resumen general
        yPosition = drawSummary(canvas, report, yPosition)

        // Determinar qué tipo de contenido mostrar según el tipo de reporte
        when (report.reportType) {
            "MONTHLY", "GENERAL" -> {
                // Mostrar resumen por persona
                if (report.summaryByPerson.isNotEmpty()) {
                    yPosition = drawPersonSummaryTable(canvas, report.summaryByPerson, yPosition)
                }
            }
            "WEEKLY" -> {
                // Mostrar resumen por día
                if (report.summaryByDay.isNotEmpty()) {
                    yPosition = drawDaySummaryTable(canvas, report.summaryByDay, yPosition)
                }
            }
            "DAILY" -> {
                // Mostrar detalle de asistencias
                if (report.attendanceRecords.isNotEmpty()) {
                    yPosition = drawAttendanceDetailTable(canvas, report.attendanceRecords, yPosition)
                }
            }
            "HOURS" -> {
                // Mostrar solo horas trabajadas por persona
                if (report.summaryByPerson.isNotEmpty()) {
                    yPosition = drawHoursTable(canvas, report.summaryByPerson, yPosition)
                }
            }
            "LATE" -> {
                // Mostrar solo atrasos
                val lateRecords = report.attendanceRecords.filter { it.isLateArrival }
                if (lateRecords.isNotEmpty()) {
                    yPosition = drawLateArrivalsTable(canvas, lateRecords, yPosition)
                }
            }
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
     * Dibuja el resumen general del reporte
     */
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

        // Título de sección
        canvas.drawText("RESUMEN GENERAL:", MARGIN_LEFT, y, titlePaint)
        y += LINE_HEIGHT

        // Total de registros
        canvas.drawText("Total de registros:", MARGIN_LEFT + 10f, y, valuePaint)
        canvas.drawText(report.totalRecords.toString(), MARGIN_LEFT + 200f, y, valuePaint)
        y += LINE_HEIGHT

        // Total de personas
        canvas.drawText("Total de personas:", MARGIN_LEFT + 10f, y, valuePaint)
        canvas.drawText(report.totalPersons.toString(), MARGIN_LEFT + 200f, y, valuePaint)
        y += LINE_HEIGHT

        // Total de horas trabajadas
        if (report.filter.includeHours) {
            canvas.drawText("Total horas trabajadas:", MARGIN_LEFT + 10f, y, valuePaint)
            canvas.drawText(formatHours(report.totalHoursWorked), MARGIN_LEFT + 200f, y, valuePaint)
            y += LINE_HEIGHT
        }

        // Total de atrasos
        if (report.filter.includeLateArrivals) {
            canvas.drawText("Total de atrasos:", MARGIN_LEFT + 10f, y, valuePaint)
            canvas.drawText(report.totalLateArrivals.toString(), MARGIN_LEFT + 200f, y, valuePaint)
            y += LINE_HEIGHT
        }

        // Total de ausencias
        if (report.filter.includeAbsences) {
            canvas.drawText("Total de ausencias:", MARGIN_LEFT + 10f, y, valuePaint)
            canvas.drawText(report.totalAbsences.toString(), MARGIN_LEFT + 200f, y, valuePaint)
            y += LINE_HEIGHT
        }

        y += 20f

        return y
    }

    /**
     * Dibuja tabla de resumen por persona
     */
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

        // Título de sección
        canvas.drawText("RESUMEN POR PERSONA:", MARGIN_LEFT, y, headerPaint)
        y += LINE_HEIGHT

        // Encabezados de tabla
        canvas.drawText("Nombre", MARGIN_LEFT, y, headerPaint)
        canvas.drawText("Días", MARGIN_LEFT + 180f, y, headerPaint)
        canvas.drawText("Presente", MARGIN_LEFT + 230f, y, headerPaint)
        canvas.drawText("Ausente", MARGIN_LEFT + 300f, y, headerPaint)
        canvas.drawText("Atrasos", MARGIN_LEFT + 370f, y, headerPaint)
        canvas.drawText("Horas", MARGIN_LEFT + 430f, y, headerPaint)
        y += LINE_HEIGHT

        // Línea separadora
        canvas.drawLine(MARGIN_LEFT, y, PAGE_WIDTH - MARGIN_RIGHT, y, Paint().apply {
            strokeWidth = 1f
            color = Color.LTGRAY
        })
        y += 5f

        // Datos
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

    /**
     * Dibuja tabla de resumen por día
     */
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

        // Título
        canvas.drawText("RESUMEN POR DÍA:", MARGIN_LEFT, y, headerPaint)
        y += LINE_HEIGHT

        // Encabezados
        canvas.drawText("Fecha", MARGIN_LEFT, y, headerPaint)
        canvas.drawText("Total", MARGIN_LEFT + 150f, y, headerPaint)
        canvas.drawText("Presente", MARGIN_LEFT + 220f, y, headerPaint)
        canvas.drawText("Ausente", MARGIN_LEFT + 300f, y, headerPaint)
        canvas.drawText("Atrasos", MARGIN_LEFT + 380f, y, headerPaint)
        y += LINE_HEIGHT

        // Datos
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

    /**
     * Dibuja tabla de detalle de asistencias
     */
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

        // Título
        canvas.drawText("DETALLE DE ASISTENCIAS:", MARGIN_LEFT, y, headerPaint)
        y += LINE_HEIGHT

        // Encabezados
        canvas.drawText("Fecha", MARGIN_LEFT, y, headerPaint)
        canvas.drawText("Persona", MARGIN_LEFT + 100f, y, headerPaint)
        canvas.drawText("Entrada", MARGIN_LEFT + 250f, y, headerPaint)
        canvas.drawText("Salida", MARGIN_LEFT + 330f, y, headerPaint)
        canvas.drawText("Horas", MARGIN_LEFT + 410f, y, headerPaint)
        y += LINE_HEIGHT

        // Datos
        records.take(30).forEach { record -> // Límite de 30 registros por página
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

    /**
     * Dibuja tabla de solo horas trabajadas
     */
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

    /**
     * Dibuja tabla de atrasos
     */
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
}