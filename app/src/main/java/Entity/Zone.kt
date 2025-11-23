package Entity

class Zone {
    private var id: String = ""
    private var code: String = ""
    private var name: String = ""
    private var description: String = ""
    private var startTime: String = ""
    private var endTime: String = ""
    private var status: Boolean = true

    constructor()

    constructor(
        id: String,
        code: String,
        name: String,
        description: String,
        startTime: String,
        endTime: String,
        status: Boolean
    ) {
        this.id = id
        this.code = code
        this.name = name
        this.description = description
        this.startTime = startTime
        this.endTime = endTime
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

    var StartTime: String
        get() = this.startTime
        set(value) { this.startTime = value }

    var EndTime: String
        get() = this.endTime
        set(value) { this.endTime = value }

    var Status: Boolean
        get() = this.status
        set(value) { this.status = value }
}