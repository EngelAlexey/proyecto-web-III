package Entity

class User {
    private var id:             String = ""
    private var name:           String = ""
    private var type:           String = ""
    private var email:          String = ""
    private var password:       String = ""
    private var status:         Boolean = true

    constructor()

    constructor(
        id: String,
        name: String,
        type: String,
        email: String,
        password: String,
        status: Boolean
    ) {
        this.id = id
        this.name = name
        this.type = type
        this.email = email
        this.password = password
        this.status = status
    }

    var ID: String
        get() = this.id
        set(value) { this.id = value }

    var Name: String
        get() = this.name
        set(value) { this.name = value }

    var Type: String
        get() = this.type
        set(value) { this.type = value }

    var Email: String
        get() = this.email
        set(value) { this.email = value }

    var Password: String
        get() = this.password
        set(value) { this.password = value }

    var Status: Boolean
        get() = this.status
        set(value) { this.status = value }

}