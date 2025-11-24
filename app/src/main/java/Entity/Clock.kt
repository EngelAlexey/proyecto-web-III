package Entity

import android.graphics.Bitmap
import java.time.LocalDateTime

class Clock {

    private var idClock:        String        = ""
    private var idPerson:       String        = ""
    private var dateClock:      LocalDateTime
    private var type:           String        = ""
    private var address:        String        = ""
    private var latitude:       Int           = 0
    private var longitude:      Int           = 0
    private lateinit var photo: Bitmap

    constructor(
        idClock: String,
        idPerson: String,
        dateClock: LocalDateTime,
        type: String,
        address: String,
        latitude: Int,
        longitude: Int,
        photo: Bitmap)

    {
        this.idClock = idClock
        this.idPerson = idPerson
        this.dateClock = dateClock
        this.type = type
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.photo = photo
    }

    var IDClock: String
        get() = this.idClock
        set(value) { this.idClock = value }

    var IDPerson: String
        get() = this.idPerson
        set(value) { this.idPerson = value }

    var Type: String
        get() = this.type
        set(value) { this.type = value }

    var DateClock: LocalDateTime
        get() = this.dateClock
        set(value) { this.dateClock = value }

    var Address: String
        get() = this.address
        set(value) { this.address = value }

    var Latitude: Int
        get() = this.latitude
        set(value) { this.latitude = value }

    var Longitude: Int
        get() = this.longitude
        set(value) { this.longitude = value }

    var Photo: Bitmap
        get() = this.photo
        set(value) { this.photo = value }
}