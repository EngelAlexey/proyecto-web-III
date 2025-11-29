package Controller

import Data.IDataManager
import Entity.Attendances
import Entity.Person
import Entity.ReportRow
import Entity.ReportSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class ReportController(private val dataManager: IDataManager) {

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd")
    private val timeFmt = SimpleDateFormat("HH:mm")

    fun generarReporte(
        from: Date?,
        to: Date?,
        personIds: List<String>,
        zoneId: String?          // ← zona seleccionada
    ): List<ReportRow> {

        if (from == null || to == null)
            throw Exception("Debe seleccionar ambas fechas")

        if (from.after(to))
            throw Exception("La fecha 'Desde' no puede ser mayor que 'Hasta'")

        val persons = dataManager.getAllPerson()
        val attendances = dataManager.getAllAttendance()

        // Filtrar rango de fechas
        var filtrado = attendances.filter {
            !it.dateAttendance.before(from) && !it.dateAttendance.after(to)
        }

        // Filtrar por personas seleccionadas
        if (personIds.isNotEmpty()) {
            filtrado = filtrado.filter { personIds.contains(it.idPerson) }
        }

        // Filtrar por zona
        if (!zoneId.isNullOrBlank()) {
            filtrado = filtrado.filter { it.idZone == zoneId }
        }

        // Construcción del reporte
        return filtrado.mapNotNull { att ->
            val person = persons.find { it.ID == att.idPerson }
                ?: return@mapNotNull null

            buildRow(person, att)
        }
    }

    fun generarResumenMensual(
        rows: List<ReportRow>,
        horaEntradaOficial: String = "08:00"   // hora de entrada para medir atrasos
    ): List<ReportSummary> {

        // Convertir hora oficial
        val officialTime = timeFmt.parse(horaEntradaOficial)

        return rows
            .groupBy { it.personName }          // agrupar por persona
            .map { (personName, items) ->

                // Calcular horas totales
                var totalMinutes = 0L
                var atrasos = 0

                items.forEach { row ->

                    // Sumar horas trabajadas
                    if (!row.hoursWorked.isNullOrBlank()) {
                        val parts = row.hoursWorked.split(":")
                        val h = parts[0].toLong()
                        val m = parts[1].toLong()
                        totalMinutes += (h * 60) + m
                    }

                    // Calcular atrasos
                    if (!row.timeEntry.isNullOrBlank()) {
                        val entrada = timeFmt.parse(row.timeEntry)
                        if (entrada.after(officialTime)) {
                            atrasos++
                        }
                    }
                }

                // Convertir total a HH:mm
                val totalH = totalMinutes / 60
                val totalM = totalMinutes % 60
                val totalFormatted = "%02d:%02d".format(totalH, totalM)

                // Crear objeto resumen
                ReportSummary(
                    personId = "",            // opcional, si la ocupas la agregamos
                    personName = personName,
                    totalHours = totalFormatted,
                    totalDays = items.size,
                    totalLate = atrasos
                )
            }
    }

    private fun buildRow(person: Person, att: Attendances): ReportRow {

        val fecha = dateFmt.format(att.dateAttendance)

        // Variables locales para evitar problema de smart cast en Kotlin
        val timeEntry = att.timeEntry
        val timeExit = att.timeExit

        val entrada = timeEntry?.let { timeFmt.format(it) } ?: "-"
        val salida  = timeExit?.let { timeFmt.format(it) } ?: "-"

        val minutos = if (timeEntry != null && timeExit != null) {
            val diffMs = timeExit.time - timeEntry.time
            TimeUnit.MILLISECONDS.toMinutes(diffMs)
        } else 0L

        val horas = String.format(
            "%02d:%02d",
            minutos / 60,
            minutos % 60
        )

        return ReportRow(
            personName = "${person.Name} ${person.FLastName} ${person.SLastName}",
            date = fecha,
            timeEntry = entrada,
            timeExit = salida,
            hoursWorked = horas
        )
    }
}
