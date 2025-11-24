package Entity

import java.time.DayOfWeek
import java.time.LocalTime

class Zone {
    private var id: String = ""
    private var code: String = ""
    private var name: String = ""
    private var description: String = ""
    private var startTime: LocalTime
    private var endTime: LocalTime
    private var days: MutableList<DayOfWeek> = mutableListOf()
    private var status: Boolean = true

    constructor() {
        this.startTime = LocalTime.MIN
        this.endTime = LocalTime.MAX
    }

    constructor(
        id: String,
        code: String,
        name: String,
        description: String,
        startTime: LocalTime,
        endTime: LocalTime,
        days: MutableList<DayOfWeek>,
        status: Boolean
    ) {
        this.id = id
        this.code = code
        this.name = name
        this.description = description
        this.startTime = startTime
        this.endTime = endTime
        this.days = days
        this.status = status
    }

    var ID: String
        get() = this.id
        set(value) { this.id = value }

    var Code: String
        get() = this.code
        set(value) { this.code = value }

    var Name: String
        get() = this.name
        set(value) { this.name = value }

    var Description: String
        get() = this.description
        set(value) { this.description = value }

    var StartTime: LocalTime
        get() = this.startTime
        set(value) { this.startTime = value }

    var EndTime: LocalTime
        get() = this.endTime
        set(value) { this.endTime = value }

    var Days: MutableList<DayOfWeek>
        get() = this.days
        set(value) { this.days = value }

    var Status: Boolean
        get() = this.status
        set(value) { this.status = value }
}