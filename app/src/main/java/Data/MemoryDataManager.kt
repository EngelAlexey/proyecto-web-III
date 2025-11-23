package Data

import Entity.Attendances
import Entity.Clock
import Entity.Person
import Entity.User
import Entity.Zone
import java.util.Date

object MemoryDataManager: IDataManager {

    private var personList      = mutableListOf<Person>()
    private var userList        = mutableListOf<User>()
    private var clockList       = mutableListOf<Clock>()
    private var attendancesList  = mutableListOf<Attendances>()
    private var zoneList        = mutableListOf<Zone>()


    //Person
    override fun addPerson(person: Person) {
        personList.add(person)
    }

    override fun updatePerson(person: Person) {
        removePerson(person.ID)
        personList.add(person)
    }

    override fun removePerson(id: String) {
        personList.removeIf { it.ID.trim() == id.trim() }
    }

    override fun getAllPerson() = personList

    override fun getByIdPerson(id: String): Person? {
        try {
            var result = personList.filter { it.ID.trim() == id.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByFullNamePerson(fullName: String): Person? {
        try {
            var result = personList.filter { it.FullName().trim() == fullName.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByIdDocumentPerson(idDocument: String): Person? {
        try {
            var result = personList.filter { it.IDDocument.trim() == idDocument.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    //Users
    override fun addUser(user: User) {
        userList.add(user)
    }

    override fun updateUser(user: User) {
        removeUser(user.ID)
        userList.add(user)
    }

    override fun removeUser(id: String) {
        userList.removeIf { it.ID.trim() == id.trim() }
    }

    override fun getAllUser() = userList

    override fun getByIdUser(id: String): User? {
        try {
            var result = userList.filter { it.ID.trim() == id.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByUserName(userName: String): User? {
        try {
            var result = userList.filter { it.Name.trim() == userName.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    //Clock
    override fun addClock(clock: Clock) {
        clockList.add(clock)
    }

    override fun updateClock(clock: Clock) {
        removeClock(clock.IDClock)
        clockList.add(clock)
    }

    override fun removeClock(id: String) {
        clockList.removeIf { it.IDClock.trim() == id.trim() }
    }

    override fun getAllClock() = clockList

    override fun getByIdClock(id: String): Clock? {
        try {
            var result = clockList.filter { it.IDClock.trim() == id.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByDate(dateClock: Date): Clock? {
        try {
            var result = clockList.filter { it.DateClock == dateClock }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByType(typeClock: String): Clock? {
        try {
            var result = clockList.filter { it.Type.trim() == typeClock.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByIdPersonClock(idPerson: String): Clock? {
        try {
            var result = clockList.filter { it.IDPerson.trim() == idPerson.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    //Attendances
    override fun addAttendance(attendance: Attendances) {
        attendancesList.add(attendance)
    }

    override fun updateAttendance(attendance: Attendances) {
        removeAttendance(attendance.idAttendance)
        attendancesList.add(attendance)
    }

    override fun removeAttendance(id: String) {
        attendancesList.removeIf { it.idAttendance.trim() == id.trim() }
    }

    override fun getAllAttendance() = attendancesList

    override fun getByIdAttendance(id: String): Attendances? {
        try {
            var result = attendancesList.filter { it.idAttendance.trim() == id.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByDateAttendance(dateAttendance: Date): Attendances? {
        try {
            var result = attendancesList.filter { it.DateAttendance == dateAttendance }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByIdPersonAttendance(idPerson: String): Attendances? {
        try {
            var result = attendancesList.filter { it.idPerson.trim() == idPerson.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    //Zone
    override fun addZone(zone: Zone) {
        zoneList.add(zone)
    }

    override fun updateZone(zone: Zone) {
        removeZone(zone.ID)
        zoneList.add(zone)
    }

    override fun removeZone(id: String) {
        zoneList.removeIf { it.ID.trim() == id.trim() }
    }

    override fun getAllZone() = zoneList

    override fun getByIdZone(id: String): Zone? {
        try {
            var result = zoneList.filter { it.ID.trim() == id.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getByCodeZone(code: String): Zone? {
        try {
            var result = zoneList.filter { it.Code.trim() == code.trim() }
            return if (result.any()) result[0] else null
        } catch (e: Exception){
            throw e
        }
    }

    override fun getActiveZones(): List<Zone> {
        try {
            return zoneList.filter { it.Status }
        } catch (e: Exception){
            throw e
        }
    }
}