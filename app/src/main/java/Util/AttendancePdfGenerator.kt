package Util

import android.content.Context
import android.graphics.*
import android.os.Environment
import Entity.Attendances
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object AttendancePdfGenerator {

    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    private val timeFmt = SimpleDateFormat("HH:mm", Locale("es", "ES"))

    fun generate(
        context: Context,
        list: List<Attendances>,
        personName: String?,
        zoneName: String?,
        startDate: Date?,
        endDate: Date?
    ): File {

        val pdf = android.graphics.pdf.PdfDocument()

        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdf.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()
        val titlePaint = Paint().apply {
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
        }

        var y = 60

        // ENCABEZADO
        paint.textSize = 14f
        canvas.drawText("Reporte de Asistencias", 200f, y.toFloat(), titlePaint)
        y += 40

        paint.textSize = 12f
        canvas.drawText("Persona: ${personName ?: "Todos"}", 20f, y.toFloat(), paint)
        y += 20

        canvas.drawText("Zona: ${zoneName ?: "Todas"}", 20f, y.toFloat(), paint)
        y += 20

        if (startDate != null && endDate != null) {
            canvas.drawText(
                "Rango: ${dateFmt.format(startDate)} - ${dateFmt.format(endDate)}",
                20f, y.toFloat(), paint
            )
        } else {
            canvas.drawText("Rango: No especificado", 20f, y.toFloat(), paint)
        }
        y += 40

        // CABECERA TABLA
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Fecha", 20f, y.toFloat(), paint)
        canvas.drawText("Entrada", 150f, y.toFloat(), paint)
        canvas.drawText("Salida", 260f, y.toFloat(), paint)
        canvas.drawText("Horas", 360f, y.toFloat(), paint)

        paint.typeface = Typeface.DEFAULT
        y += 20

        // FILAS
        var totalMinutes = 0L

        list.forEach { att ->

            canvas.drawText(dateFmt.format(att.dateAttendance), 20f, y.toFloat(), paint)

            canvas.drawText(att.timeEntry?.let { timeFmt.format(it) } ?: "--", 150f, y.toFloat(), paint)
            canvas.drawText(att.timeExit?.let { timeFmt.format(it) } ?: "--", 260f, y.toFloat(), paint)

            val minutes = att.hoursAttendanceMinutes()
            totalMinutes += minutes

            canvas.drawText("${minutes / 60}h ${minutes % 60}m", 360f, y.toFloat(), paint)

            y += 20
        }

        y += 30

        // TOTAL GENERAL
        paint.typeface = Typeface.DEFAULT_BOLD
        val th = totalMinutes / 60
        val tm = totalMinutes % 60

        canvas.drawText("Total horas trabajadas: ${th}h ${tm}m", 20f, y.toFloat(), paint)

        pdf.finishPage(page)

        // GUARDAR ARCHIVO
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, "reporte_asistencias_${System.currentTimeMillis()}.pdf")

        FileOutputStream(file).use {
            pdf.writeTo(it)
        }

        pdf.close()

        return file
    }
}
