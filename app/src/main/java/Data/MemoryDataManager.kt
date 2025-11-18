package Data

import Entity.Attendances
import Entity.Clock
import Entity.Person
import java.util.Date

object MemoryDataManager: IDataManager {

    private var personList      = mutableListOf<Person>()
    private var clockList       = mutableListOf<Clock>()
    private var attendancesList  = mutableListOf<Attendances>()


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

}