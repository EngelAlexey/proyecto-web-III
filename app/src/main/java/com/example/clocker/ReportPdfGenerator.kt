package com.example.clocker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import Entity.ReportRow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ReportPdfGenerator {

    fun generatePdf(
        context: Context,
        rows: List<ReportRow>,
        fromDate: Date?,
        toDate: Date?,
        zone: String?,
        personNames: List<String>,
        totalHours: String
    ) {
        if (rows.isEmpty()) {
            Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val pdf = PdfDocument()
            val paint = Paint().apply {
                textSize = 12f
                isAntiAlias = true
            }
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                isAntiAlias = true
            }

            val pageWidth = 595
            val pageHeight = 842
            val marginLeft = 20f
            var yPosition = 50f
            val lineHeight = 20f

            // Crear página
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            var page = pdf.startPage(pageInfo)
            var canvas: Canvas = page.canvas

            // Título
            canvas.drawText("Reporte de Asistencias", pageWidth / 2f - 80f, yPosition, titlePaint)
            yPosition += lineHeight * 2

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Filtros
            canvas.drawText("Desde: ${fromDate?.let { sdf.format(it) } ?: "-"}", marginLeft, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("Hasta: ${toDate?.let { sdf.format(it) } ?: "-"}", marginLeft, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("Zona: ${zone ?: "Todas"}", marginLeft, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("Personas: ${if (personNames.isNotEmpty()) personNames.joinToString(", ") else "Todas"}", marginLeft, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("Horas Totales Trabajadas: $totalHours", marginLeft, yPosition, paint)
            yPosition += lineHeight * 2

            // Encabezados tabla
            val colPersona = marginLeft
            val colFecha = 180f
            val colEntrada = 300f
            val colSalida = 390f
            val colHoras = 480f

            val headerPaint = Paint(paint).apply { textSize = 14f; isFakeBoldText = true }

            canvas.drawText("Persona", colPersona, yPosition, headerPaint)
            canvas.drawText("Fecha", colFecha, yPosition, headerPaint)
            canvas.drawText("Entrada", colEntrada, yPosition, headerPaint)
            canvas.drawText("Salida", colSalida, yPosition, headerPaint)
            canvas.drawText("Horas", colHoras, yPosition, headerPaint)

            yPosition += lineHeight

            paint.textSize = 12f

            for ((index, row) in rows.withIndex()) {

                // Si se pasa de la página, crear nueva página
                if (yPosition + lineHeight > pageHeight - 50) {
                    pdf.finishPage(page)
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index / 40 + 2).create()
                    page = pdf.startPage(pageInfo)
                    canvas = page.canvas
                    yPosition = 50f
                }

                canvas.drawText(row.personName, colPersona, yPosition, paint)
                canvas.drawText(row.date, colFecha, yPosition, paint)
                canvas.drawText(row.timeEntry ?: "-", colEntrada, yPosition, paint)
                canvas.drawText(row.timeExit ?: "-", colSalida, yPosition, paint)
                canvas.drawText(row.hoursWorked ?: "-", colHoras, yPosition, paint)

                yPosition += lineHeight
            }

            pdf.finishPage(page)

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val fileName = "reporte_asistencias_${System.currentTimeMillis()}.pdf"
            val file = File(downloadsDir, fileName)

            FileOutputStream(file).use { output ->
                pdf.writeTo(output)
            }

            pdf.close()

            Toast.makeText(context, "PDF generado en Descargas: $fileName", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
