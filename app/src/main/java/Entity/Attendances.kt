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

    constructor()

    constructor(
        IDAttendance: String,
        DateAttendance: Date,
        IDPerson: String,
        TimeEntry: Date?,
        TimeExit: Date?,
        EntryID: String,
        ExitID: String
    ) {
        this.IDAttendance = IDAttendance
        this.DateAttendance = DateAttendance
        this.IDPerson = IDPerson
        this.TimeEntry = TimeEntry
        this.TimeExit = TimeExit
        this.EntryID = EntryID
        this.ExitID = ExitID
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

    /**
     * Minutos trabajados entre la entrada y la salida.
     * Si no hay ambos valores, regresa 0.
     */
    fun hoursAttendanceMinutes(): Long {
        val start = this.TimeEntry
        val end = this.TimeExit
        if (start == null || end == null) return 0L
        val diffMs = end.time - start.time
        return TimeUnit.MILLISECONDS.toMinutes(diffMs)
    }
}
