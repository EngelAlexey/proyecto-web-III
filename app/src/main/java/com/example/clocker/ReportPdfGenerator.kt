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
        toDate: Date?
    ) {
        if (rows.isEmpty()) {
            Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val pdf = PdfDocument()
            val paint = Paint()
            val titlePaint = Paint()

            titlePaint.textSize = 18f
            titlePaint.isFakeBoldText = true

            paint.textSize = 14f

            // Crear página 1
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdf.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            var y = 50f

            // Encabezado
            canvas.drawText("Reporte de Asistencias", 200f, y, titlePaint)
            y += 35

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            canvas.drawText("Desde: ${fromDate?.let { sdf.format(it) } ?: "-"}", 20f, y, paint)
            y += 20
            canvas.drawText("Hasta: ${toDate?.let { sdf.format(it) } ?: "-"}", 20f, y, paint)
            y += 30

            // Encabezados de tabla
            titlePaint.textSize = 14f
            canvas.drawText("Persona", 20f, y, titlePaint)
            canvas.drawText("Fecha", 180f, y, titlePaint)
            canvas.drawText("Entrada", 300f, y, titlePaint)
            canvas.drawText("Salida", 390f, y, titlePaint)
            canvas.drawText("Horas", 480f, y, titlePaint)

            y += 25

            paint.textSize = 12f

            // Filas
            rows.forEach { row ->
                if (y > 800) {
                    // Nueva página si se llena
                    pdf.finishPage(page)

                    val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                    val newPage = pdf.startPage(newPageInfo)
                    y = 50f
                }

                canvas.drawText(row.personName, 20f, y, paint)
                canvas.drawText(row.date, 180f, y, paint)
                canvas.drawText(row.timeEntry ?: "-", 300f, y, paint)
                canvas.drawText(row.timeExit ?: "-", 390f, y, paint)
                canvas.drawText(row.hoursWorked ?: "-", 480f, y, paint)

                y += 20
            }

            pdf.finishPage(page)

            // Guardar PDF en carpeta Downloads
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloads, "reporte_asistencias_${System.currentTimeMillis()}.pdf")

            val fos = FileOutputStream(file)
            pdf.writeTo(fos)

            fos.close()
            pdf.close()

            Toast.makeText(context, "PDF generado en Descargas", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
