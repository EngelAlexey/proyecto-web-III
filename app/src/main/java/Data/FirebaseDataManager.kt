package Data

import Entity.*
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.util.Date

object FirebaseDataManager : IDataManager {

    // Asegúrate de usar el nombre correcto de tu BD si no es la default.
    // Si ya creaste la (default), usa: getInstance()
    // Si sigues usando webIII, usa: getInstance("webIII")
    private val db = FirebaseFirestore.getInstance()

    private var personList = mutableListOf<Person>()
    private var zoneList = mutableListOf<Zone>()
    private var clockList = mutableListOf<Clock>()
    private var attendanceList = mutableListOf<Attendances>()

    init {
        // --- Escuchar Personas ---
        db.collection("persons").addSnapshotListener { value, _ ->
            personList.clear()
            value?.forEach { doc -> personList.add(doc.toObject(Person::class.java)) }
        }

        // --- Escuchar Zonas ---
        db.collection("zones").addSnapshotListener { value, _ ->
            zoneList.clear()
            value?.forEach { doc ->
                try {
                    val z = Zone()
                    z.ID = doc.id
                    z.Code = doc.getString("code") ?: ""
                    z.Name = doc.getString("name") ?: ""
                    z.Description = doc.getString("description") ?: ""
                    z.StartTime = FirebaseConverters.stringToTime(doc.getString("startTime") ?: "")
                    z.EndTime = FirebaseConverters.stringToTime(doc.getString("endTime") ?: "")

                    val daysStrings = doc.get("days") as? List<String> ?: emptyList()
                    z.Days = daysStrings.map { DayOfWeek.valueOf(it) }.toMutableList()

                    z.Status = doc.getBoolean("status") ?: true
                    zoneList.add(z)
                } catch (e: Exception) {
                    Log.e("FirebaseData", "Error al leer zona", e)
                }
            }
        }

        // --- Escuchar Clocks (VERSIÓN BASE64 - SIN STORAGE) ---
        db.collection("clocks").addSnapshotListener { value, _ ->
            clockList.clear()
            value?.forEach { doc ->
                try {
                    // 1. Leemos el texto largo (Base64) de la base de datos
                    val photoBase64 = doc.getString("photoBase64") ?: ""

                    // 2. Lo convertimos a imagen (Bitmap)
                    val bitmap = FirebaseConverters.stringToBitmap(photoBase64)
                        ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

                    val c = Clock(
                        idClock = doc.id,
                        idPerson = doc.getString("idPerson") ?: "",
                        dateClock = try {
                            java.time.LocalDateTime.parse(doc.getString("dateClock"))
                        } catch (e: Exception) { java.time.LocalDateTime.now() },
                        type = doc.getString("type") ?: "",
                        address = "",
                        latitude = 0, longitude = 0,
                        photo = bitmap // Asignamos la imagen convertida
                    )
                    clockList.add(c)
                } catch (e: Exception) {
                    Log.e("FirebaseData", "Error al leer clock", e)
                }
            }
        }

        // --- Escuchar Asistencias ---
        db.collection("attendances").addSnapshotListener { value, _ ->
            attendanceList.clear()
            value?.forEach { doc -> attendanceList.add(doc.toObject(Attendances::class.java)) }
        }
    }

    // --- MÉTODOS DE ESCRITURA ---

    // VERSIÓN SIN STORAGE (Guarda la foto como texto)
    override fun addClock(clock: Clock) {
        val data = hashMapOf(
            "idPerson" to clock.IDPerson,
            "dateClock" to clock.DateClock.toString(),
            "type" to clock.Type,
            // Convertimos la imagen a texto Base64 aquí mismo
            "photoBase64" to FirebaseConverters.bitmapToString(clock.Photo)
        )

        db.collection("clocks").document(clock.IDClock).set(data)
            .addOnSuccessListener { Log.d("FirebaseData", "Marca guardada (Base64)") }
            .addOnFailureListener { Log.e("FirebaseData", "Error guardando marca", it) }
    }

    // --- Resto de implementaciones estándar ---

    override fun addPerson(person: Person) { db.collection("persons").document(person.ID).set(person) }
    override fun updatePerson(person: Person) { addPerson(person) }
    override fun removePerson(id: String) { db.collection("persons").document(id).delete() }
    override fun getAllPerson() = personList
    override fun getByIdPerson(id: String) = personList.find { it.ID == id }
    override fun getByFullNamePerson(fullName: String): Person? = personList.find { it.FullName() == fullName }
    override fun getByIdDocumentPerson(idDocument: String): Person? = personList.find { it.IDDocument == idDocument }

    override fun addZone(zone: Zone) {
        val data = hashMapOf(
            "code" to zone.Code,
            "name" to zone.Name,
            "description" to zone.Description,
            "startTime" to FirebaseConverters.timeToString(zone.StartTime),
            "endTime" to FirebaseConverters.timeToString(zone.EndTime),
            "days" to zone.Days.map { it.name },
            "status" to zone.Status
        )
        db.collection("zones").document(zone.ID).set(data)
    }
    override fun updateZone(zone: Zone) { addZone(zone) }
    override fun removeZone(id: String) { db.collection("zones").document(id).delete() }
    override fun getAllZone() = zoneList
    override fun getByIdZone(id: String) = zoneList.find { it.ID == id }
    override fun getByCodeZone(code: String): Zone? = zoneList.find { it.Code == code }
    override fun getActiveZones(): List<Zone> = zoneList.filter { it.Status }

    override fun updateClock(clock: Clock) {}
    override fun removeClock(id: String) { db.collection("clocks").document(id).delete() }
    override fun getAllClock() = clockList
    override fun getByIdClock(id: String) = clockList.find { it.IDClock == id }
    override fun getByDate(dateClock: Date): Clock? = null
    override fun getByType(typeClock: String): Clock? = clockList.find { it.Type == typeClock }
    override fun getByIdPersonClock(idPerson: String): Clock? = clockList.find { it.IDPerson == idPerson }

    override fun addAttendance(attendance: Attendances) { db.collection("attendances").document(attendance.idAttendance).set(attendance) }
    override fun updateAttendance(attendance: Attendances) { addAttendance(attendance) }
    override fun removeAttendance(id: String) { db.collection("attendances").document(id).delete() }
    override fun getAllAttendance() = attendanceList
    override fun getByIdAttendance(id: String) = attendanceList.find { it.idAttendance == id }
    override fun getByDateAttendance(dateAttendance: Date): Attendances? = attendanceList.find { it.dateAttendance == dateAttendance }
    override fun getByIdPersonAttendance(idPerson: String): Attendances? = attendanceList.find { it.idPerson == idPerson }

    override fun addUser(user: User) {}
    override fun updateUser(user: User) {}
    override fun removeUser(id: String) {}
    override fun getAllUser(): List<User> = emptyList()
    override fun getByIdUser(id: String): User? = null
    override fun getByUserName(userName: String): User? = null
}