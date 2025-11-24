package Entity

class Person {
    private var id:             String = ""
    private var name:           String = ""
    private var fLastName:      String = ""
    private var sLastName:      String = ""
    private var nationality:    String = ""
    private var idDocument:     String = ""
    private var zoneCode:       String = ""
    private var status:         Boolean = true

    constructor()

    constructor(
        id: String,
        name: String,
        fLastName: String,
        sLastName: String,
        nationality: String,
        idDocument: String,
        zoneCode: String,
        status: Boolean
    ) {
        this.id = id
        this.name = name
        this.fLastName = fLastName
        this.sLastName = sLastName
        this.nationality = nationality
        this.idDocument = idDocument
        this.zoneCode = zoneCode
        this.status = status
    }

    var ID: String
        get() = this.id
        set(value) { this.id = value }

    var Name: String
        get() = this.name
        set(value) { this.name = value }

    var FLastName: String
        get() = this.fLastName
        set(value) { this.fLastName = value }

    var SLastName: String
        get() = this.sLastName
        set(value) { this.sLastName = value }

    var Nationality: String
        get() = this.nationality
        set(value) { this.nationality = value }

    var IDDocument: String
        get() = this.idDocument
        set(value) { this.idDocument = value }

    var ZoneCode: String
        get() = this.zoneCode
        set(value) { this.zoneCode = value }

    var Status: Boolean
        get() = this.status
        set(value) { this.status = value }

    fun FullName() = "$this.name $this.fLastName $this.sLastName"
}