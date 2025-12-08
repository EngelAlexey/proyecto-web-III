package com.example.clocker.Firebase

import Entity.*
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class FirestoreDataManager {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirestoreDataManager"

    init {
        // Habilitar persistencia offline
        db.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    // ============================================================
    // PERSON (Personas)
    // ============================================================

    suspend fun addPerson(person: Person): Result<Unit> {
        return try {
            db.collection("persons")
                .document(person.ID)
                .set(personToMap(person))
                .await()

            Log.d(TAG, "✅ Person added: ${person.ID}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error adding person", e)
            Result.failure(e)
        }
    }

    suspend fun updatePerson(person: Person): Result<Unit> {
        return try {
            db.collection("persons")
                .document(person.ID)
                .set(personToMap(person))
                .await()

            Log.d(TAG, "✅ Person updated: ${person.ID}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating person", e)
            Result.failure(e)
        }
    }

    suspend fun removePerson(id: String): Result<Unit> {
        return try {
            db.collection("persons")
                .document(id)
                .delete()
                .await()

            Log.d(TAG, "✅ Person removed: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error removing person", e)
            Result.failure(e)
        }
    }

    suspend fun getAllPerson(): List<Person> {
        return try {
            val snapshot = db.collection("persons")
                .orderBy("name")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToPerson(doc.data ?: return@mapNotNull null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting persons", e)
            emptyList()
        }
    }

    suspend fun getByIdPerson(id: String): Person? {
        return try {
            val doc = db.collection("persons")
                .document(id)
                .get()
                .await()

            if (doc.exists()) {
                mapToPerson(doc.data ?: return null)
            } else null

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting person by ID", e)
            null
        }
    }

    suspend fun getByFullNamePerson(fullName: String): Person? {
        return try {
            val snapshot = db.collection("persons")
                .whereEqualTo("name", fullName.split(" ")[0])
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                mapToPerson(doc.data ?: return null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting person by name", e)
            null
        }
    }

    suspend fun getByIdDocumentPerson(idDocument: String): Person? {
        return try {
            val snapshot = db.collection("persons")
                .whereEqualTo("idDocument", idDocument)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                mapToPerson(doc.data ?: return null)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting person by document ID", e)
            null
        }
    }

    // ============================================================
    // ZONE (Zonas)
    // ============================================================

    suspend fun addZone(zone: Zone): Result<Unit> {
        return try {
            db.collection("zones")
                .document(zone.ID)
                .set(zoneToMap(zone))
                .await()

            Log.d(TAG, "✅ Zone added: ${zone.ID}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error adding zone", e)
            Result.failure(e)
        }
    }

    suspend fun updateZone(zone: Zone): Result<Unit> {
        return try {
            db.collection("zones")
                .document(zone.ID)
                .set(zoneToMap(zone))
                .await()

            Log.d(TAG, "✅ Zone updated: ${zone.ID}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating zone", e)
            Result.failure(e)
        }
    }

    suspend fun removeZone(id: String): Result<Unit> {
        return try {
            db.collection("zones")
                .document(id)
                .delete()
                .await()

            Log.d(TAG, "✅ Zone removed: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error removing zone", e)
            Result.failure(e)
        }
    }

    suspend fun getAllZone(): List<Zone> {
        return try {
            val snapshot = db.collection("zones")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToZone(doc.data ?: return@mapNotNull null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting zones", e)
            emptyList()
        }
    }

    suspend fun getByIdZone(id: String): Zone? {
        return try {
            val doc = db.collection("zones")
                .document(id)
                .get()
                .await()

            if (doc.exists()) {
                mapToZone(doc.data ?: return null)
            } else null

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting zone by ID", e)
            null
        }
    }

    suspend fun getActiveZones(): List<Zone> {
        return try {
            val snapshot = db.collection("zones")
                .whereEqualTo("status", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToZone(doc.data ?: return@mapNotNull null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting active zones", e)
            emptyList()
        }
    }

    suspend fun getByCodeZone(code: String): Zone? {
        return try {
            val snapshot = db.collection("zones")
                .whereEqualTo("code", code)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                mapToZone(doc.data ?: return null)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting zone by code", e)
            null
        }
    }

    // ============================================================
    // CLOCK (Marcas de Reloj)
    // ============================================================

    suspend fun addClock(clock: Clock, photoUrl: String): Result<Unit> {
        return try {
            val clockMap = clockToMap(clock).toMutableMap()
            clockMap["photoUrl"] = photoUrl

            db.collection("clocks")
                .document(clock.IDClock)
                .set(clockMap)
                .await()

            Log.d(TAG, "✅ Clock added: ${clock.IDClock}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error adding clock", e)
            Result.failure(e)
        }
    }

    suspend fun updateClock(clock: Clock): Result<Unit> {
        return try {
            db.collection("clocks")
                .document(clock.IDClock)
                .set(clockToMap(clock))
                .await()

            Log.d(TAG, "✅ Clock updated: ${clock.IDClock}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating clock", e)
            Result.failure(e)
        }
    }

    suspend fun removeClock(id: String): Result<Unit> {
        return try {
            db.collection("clocks")
                .document(id)
                .delete()
                .await()

            Log.d(TAG, "✅ Clock removed: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error removing clock", e)
            Result.failure(e)
        }
    }

    suspend fun getAllClock(): List<Clock> {
        return try {
            val snapshot = db.collection("clocks")
                .orderBy("dateClock", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToClock(doc.data ?: return@mapNotNull null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting clocks", e)
            emptyList()
        }
    }

    suspend fun getByIdClock(id: String): Clock? {
        return try {
            val doc = db.collection("clocks")
                .document(id)
                .get()
                .await()

            if (doc.exists()) {
                mapToClock(doc.data ?: return null)
            } else null

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting clock by ID", e)
            null
        }
    }

    suspend fun getByIdPersonClock(idPerson: String): List<Clock> {
        return try {
            val snapshot = db.collection("clocks")
                .whereEqualTo("idPerson", idPerson)
                .orderBy("dateClock", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToClock(doc.data ?: return@mapNotNull null)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting clocks by person", e)
            emptyList()
        }
    }

    suspend fun getByDateClock(dateClock: java.util.Date): Clock? {
        return try {
            val dateMillis = dateClock.time

            val snapshot = db.collection("clocks")
                .whereEqualTo("dateClock", dateMillis)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                mapToClock(doc.data ?: return null)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting clock by date", e)
            null
        }
    }

    suspend fun getByTypeClock(type: String): List<Clock> {
        return try {
            val snapshot = db.collection("clocks")
                .whereEqualTo("type", type)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToClock(doc.data ?: return@mapNotNull null)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting clocks by type", e)
            emptyList()
        }
    }

    // ============================================================
    // ATTENDANCE (Asistencias)
    // ============================================================

    suspend fun addAttendance(attendance: Attendances): Result<Unit> {
        return try {
            db.collection("attendances")
                .document(attendance.idAttendance)
                .set(attendanceToMap(attendance))
                .await()

            Log.d(TAG, "✅ Attendance added: ${attendance.idAttendance}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error adding attendance", e)
            Result.failure(e)
        }
    }

    suspend fun updateAttendance(attendance: Attendances): Result<Unit> {
        return try {
            db.collection("attendances")
                .document(attendance.idAttendance)
                .set(attendanceToMap(attendance))
                .await()

            Log.d(TAG, "✅ Attendance updated: ${attendance.idAttendance}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating attendance", e)
            Result.failure(e)
        }
    }

    suspend fun removeAttendance(id: String): Result<Unit> {
        return try {
            db.collection("attendances")
                .document(id)
                .delete()
                .await()

            Log.d(TAG, "✅ Attendance removed: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error removing attendance", e)
            Result.failure(e)
        }
    }

    suspend fun getAllAttendance(): List<Attendances> {
        return try {
            val snapshot = db.collection("attendances")
                .orderBy("dateAttendance", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToAttendance(doc.data ?: return@mapNotNull null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting attendances", e)
            emptyList()
        }
    }

    suspend fun getByIdAttendance(id: String): Attendances? {
        return try {
            val doc = db.collection("attendances")
                .document(id)
                .get()
                .await()

            if (doc.exists()) {
                mapToAttendance(doc.data ?: return null)
            } else null

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting attendance by ID", e)
            null
        }
    }

    suspend fun getByDateAttendance(dateAttendance: Date): Attendances? {
        return try {
            val snapshot = db.collection("attendances")
                .whereEqualTo("dateAttendance", dateAttendance.time)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                mapToAttendance(doc.data ?: return null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting attendance by date", e)
            null
        }
    }

    suspend fun getByIdPersonAttendance(idPerson: String): List<Attendances> {
        return try {
            val snapshot = db.collection("attendances")
                .whereEqualTo("idPerson", idPerson)
                .orderBy("dateAttendance", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToAttendance(doc.data ?: return@mapNotNull null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting attendances by person", e)
            emptyList()
        }
    }

    suspend fun getByDateRangeAttendance(startDate: java.util.Date, endDate: java.util.Date): List<Attendances> {
        return try {
            val snapshot = db.collection("attendances")
                .whereGreaterThanOrEqualTo("dateAttendance", startDate.time)
                .whereLessThanOrEqualTo("dateAttendance", endDate.time)
                .orderBy("dateAttendance", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapToAttendance(doc.data ?: return@mapNotNull null)
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting attendances by date range", e)
            emptyList()
        }
    }

    // ============================================================
    // FUNCIONES DE CONVERSIÓN (Entity <-> Map)
    // ============================================================

    private fun personToMap(person: Person): Map<String, Any?> {
        return mapOf(
            "id" to person.ID,
            "name" to person.Name,
            "fLastName" to person.FLastName,
            "sLastName" to person.SLastName,
            "nationality" to person.Nationality,
            "idDocument" to person.IDDocument,
            "zoneCode" to person.ZoneCode,
            "status" to person.Status
        )
    }

    private fun mapToPerson(map: Map<String, Any?>): Person {
        return Person(
            id = map["id"] as? String ?: "",
            name = map["name"] as? String ?: "",
            fLastName = map["fLastName"] as? String ?: "",
            sLastName = map["sLastName"] as? String ?: "",
            nationality = map["nationality"] as? String ?: "",
            idDocument = map["idDocument"] as? String ?: "",
            zoneCode = map["zoneCode"] as? String ?: "",
            status = map["status"] as? Boolean ?: false
        )
    }

    private fun zoneToMap(zone: Zone): Map<String, Any?> {
        return mapOf(
            "id" to zone.ID,
            "code" to zone.Code,
            "name" to zone.Name,
            "description" to zone.Description,
            "startTime" to zone.StartTime.toString(),
            "endTime" to zone.EndTime.toString(),
            "days" to zone.Days.map { it.name },
            "status" to zone.Status
        )
    }

    private fun mapToZone(map: Map<String, Any?>): Zone {
        val daysNames = map["days"] as? List<String> ?: emptyList()
        val days = daysNames.mapNotNull { dayName ->
            try {
                java.time.DayOfWeek.valueOf(dayName)
            } catch (e: Exception) {
                null
            }
        }.toMutableList()

        return Zone(
            id = map["id"] as? String ?: "",
            code = map["code"] as? String ?: "",
            name = map["name"] as? String ?: "",
            description = map["description"] as? String ?: "",
            startTime = java.time.LocalTime.parse(map["startTime"] as? String ?: "08:00"),
            endTime = java.time.LocalTime.parse(map["endTime"] as? String ?: "17:00"),
            days = days,
            status = map["status"] as? Boolean ?: false
        )
    }

    private fun clockToMap(clock: Clock): Map<String, Any?> {
        val dateMillis = clock.DateClock
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return mapOf(
            "idClock" to clock.IDClock,
            "idPerson" to clock.IDPerson,
            "dateClock" to dateMillis,
            "type" to clock.Type,
            "address" to clock.Address,
            "latitude" to clock.Latitude,
            "longitude" to clock.Longitude
        )
    }

    private fun mapToClock(map: Map<String, Any?>): Clock {
        val dateMillis = map["dateClock"] as? Long ?: System.currentTimeMillis()
        val localDateTime = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(dateMillis),
            ZoneId.systemDefault()
        )

        return Clock(
            idClock = map["idClock"] as? String ?: "",
            idPerson = map["idPerson"] as? String ?: "",
            dateClock = localDateTime,
            type = map["type"] as? String ?: "",
            address = map["address"] as? String ?: "",
            latitude = (map["latitude"] as? Long)?.toInt() ?: 0,
            longitude = (map["longitude"] as? Long)?.toInt() ?: 0,
            photo = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        )
    }

    private fun attendanceToMap(attendance: Attendances): Map<String, Any?> {
        return mapOf(
            "idAttendance" to attendance.idAttendance,
            "dateAttendance" to attendance.dateAttendance.time,
            "idPerson" to attendance.idPerson,
            "timeEntry" to attendance.timeEntry?.time,
            "timeExit" to attendance.timeExit?.time,
            "entryID" to attendance.entryID,
            "exitID" to attendance.exitID
        )
    }

    private fun mapToAttendance(map: Map<String, Any?>): Attendances {
        val dateMillis = map["dateAttendance"] as? Long ?: System.currentTimeMillis()
        val entryMillis = map["timeEntry"] as? Long
        val exitMillis = map["timeExit"] as? Long

        return Attendances(
            IDAttendance = map["idAttendance"] as? String ?: "",
            DateAttendance = Date(dateMillis),
            IDPerson = map["idPerson"] as? String ?: "",
            TimeEntry = entryMillis?.let { Date(it) },
            TimeExit = exitMillis?.let { Date(it) },
            EntryID = map["entryID"] as? String ?: "",
            ExitID = map["exitID"] as? String ?: ""
        )
    }
}