package Entity

import java.time.LocalDate
import java.util.Date

class Attendances {

    private var IDAttendance:       String = ""
    private var dateAttendance:     LocalDate
    private var IDPerson:           String = ""
    private var TimeEntry:          LocalDate
    private var TimeExit:           LocalDate
    private var EntryID:            String = ""
    private var ExitID:             String = ""

    constructor(
        IDAttendance: String,
        dateAttendance: LocalDate,
        IDPerson: String,
        TimeEntry: LocalDate,
        TimeExit: LocalDate,
        EntryID: String,
        ExitID: String)

    {
        this.IDAttendance = IDAttendance
        this.dateAttendance = dateAttendance
        this.IDPerson = IDPerson
        this.TimeEntry = TimeEntry
        this.TimeExit = TimeExit
        this.EntryID = EntryID
        this.ExitID = ExitID
    }

    var idAttendance: String
        get() = this.IDAttendance
        set(value) { this.IDAttendance = value }

    var DateAttendance: LocalDate
        get() = this.dateAttendance
        set(value) { this.dateAttendance = value }

    var idPerson: String
        get() = this.IDPerson
        set(value) { this.IDPerson = value }

    var timeEntry: LocalDate
        get() = this.TimeEntry
        set(value) { this.TimeEntry = value }

    var timeExit: LocalDate
        get() = this.TimeExit
        set(value) { this.TimeExit = value }

    var entryID: String
        get() = this.EntryID
        set(value) { this.EntryID = value }

    var exitID: String
        get() = this.ExitID
        set(value) { this.ExitID = value }

    /*fun HoursAttendance(): Long {
        return this.TimeExit.time - this.TimeEntry.time
    }*/

}