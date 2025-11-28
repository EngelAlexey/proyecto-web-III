package Controller

import Data.MemoryDataManager
import Entity.Attendances
import Entity.Clock
import java.time.ZoneId
import java.util.*

object AttendanceController {

    fun processClockMark(clock: Clock) {

        val zoneId = ZoneId.systemDefault()

        val clockDate: Date = Date.from(clock.DateClock.atZone(zoneId).toInstant())

        val calendar = Calendar.getInstance()
        calendar.time = clockDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dateOnly = calendar.time

        val existing = MemoryDataManager.getAllAttendance()
            .firstOrNull {
                it.idPerson.trim() == clock.IDPerson.trim() &&
                        isSameDay(it.dateAttendance, dateOnly)
            }

        if (existing == null) {
            val newAttendance = Attendances(
                IDAttendance = UUID.randomUUID().toString(),
                DateAttendance = dateOnly,
                IDPerson = clock.IDPerson,
                TimeEntry = clockDate,
                TimeExit = null,
                EntryID = clock.IDClock,
                ExitID = ""
            )

            MemoryDataManager.addAttendance(newAttendance)
            return
        }

        if (existing.timeExit == null) {
            existing.timeExit = clockDate
            existing.exitID = clock.IDClock

            MemoryDataManager.updateAttendance(existing)
            return
        }
    }

    private fun isSameDay(d1: Date, d2: Date): Boolean {
        val c1 = Calendar.getInstance()
        c1.time = d1

        val c2 = Calendar.getInstance()
        c2.time = d2

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }
}
