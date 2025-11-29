package Controller

import Data.IDataManager
import Entity.Attendances
import Entity.Person
import Entity.ReportRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class ReportController(private val dataManager: IDataManager) {

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd")
    private val timeFmt = SimpleDateFormat("HH:mm")

    fun generarReporte(
        from: Date?,
        to: Date?,
        personIds: List<String>
    ): List<ReportRow> {

        if (from == null || to == null)
            throw Exception("Debe seleccionar ambas fechas")

        if (from.after(to))
            throw Exception("La fecha 'Desde' no puede ser mayor que 'Hasta'")

        // ✔ Tus nombres reales del DataManager
        val persons = dataManager.getAllPerson()
        val attendances = dataManager.getAllAttendance()

        // ✔ Filtrar por rango usando Date
        val enRango = attendances.filter {
            !it.dateAttendance.before(from) && !it.dateAttendance.after(to)
        }

        // ✔ Filtrar por persona (ID)
        val filtrado =
            if (personIds.isEmpty()) enRango
            else enRango.filter { personIds.contains(it.idPerson) }

        // ✔ Construir filas
        return filtrado.mapNotNull { att ->
            val person = persons.find { it.ID == att.idPerson }
                ?: return@mapNotNull null

            buildRow(person, att)
        }
    }

    private fun buildRow(person: Person, att: Attendances): ReportRow {

        val fecha = dateFmt.format(att.dateAttendance)
        val entrada = att.timeEntry?.let { timeFmt.format(it) } ?: "-"
        val salida  = att.timeExit?.let { timeFmt.format(it) } ?: "-"

        val minutos = if (att.timeEntry != null && att.timeExit != null) {
            val diffMs = att.timeExit!!.time - att.timeEntry!!.time
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
