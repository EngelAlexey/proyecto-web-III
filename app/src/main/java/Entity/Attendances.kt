package Entity

import java.util.Date
import java.util.concurrent.TimeUnit

class Attendances {

    private var IDAttendance: String = ""
    private var DateAttendance: Date = Date()
    private var IDPerson: String = ""
    private var TimeEntry: Date? = null
    private var TimeExit: Date? = null
    private var EntryID: String = ""
    private var ExitID: String = ""

    // 🔥 Nuevo campo → Zona de la asistencia
    private var IDZone: String = ""

    constructor(
        IDAttendance: String,
        DateAttendance: Date,
        IDPerson: String,
        TimeEntry: Date?,
        TimeExit: Date?,
        EntryID: String,
        ExitID: String,
        IDZone: String        // ← 🔥 agregar al constructor
    ) {
        this.IDAttendance = IDAttendance
        this.DateAttendance = DateAttendance
        this.IDPerson = IDPerson
        this.TimeEntry = TimeEntry
        this.TimeExit = TimeExit
        this.EntryID = EntryID
        this.ExitID = ExitID
        this.IDZone = IDZone     // ← 🔥 nuevo
    }

    var idAttendance: String
        get() = this.IDAttendance
        set(value) { this.IDAttendance = value }

    var dateAttendance: Date
        get() = this.DateAttendance
        set(value) { this.DateAttendance = value }

    var idPerson: String
        get() = this.IDPerson
        set(value) { this.IDPerson = value }

    var timeEntry: Date?
        get() = this.TimeEntry
        set(value) { this.TimeEntry = value }

    var timeExit: Date?
        get() = this.TimeExit
        set(value) { this.TimeExit = value }

    var entryID: String
        get() = this.EntryID
        set(value) { this.EntryID = value }

    var exitID: String
        get() = this.ExitID
        set(value) { this.ExitID = value }

    // 🔥 Getter/Setter de zona
    var idZone: String
        get() = this.IDZone
        set(value) { this.IDZone = value }


    fun hoursAttendanceMinutes(): Long {
        val start = this.TimeEntry
        val end = this.TimeExit
        if (start == null || end == null) return 0L
        val diffMs = end.time - start.time
        return TimeUnit.MILLISECONDS.toMinutes(diffMs)
    }
}
