package Data

import Entity.Clock
import Entity.Person
import Entity.Attendances
import Entity.User
import java.util.Date

interface IDataManager {
    //Person
    fun addPerson(person: Person)
    fun updatePerson(person: Person)
    fun removePerson(id: String)
    fun getAllPerson(): List<Person>
    fun getByIdPerson(id: String): Person?
    fun getByFullNamePerson(fullName: String): Person?
    fun getByIdDocumentPerson(idDocument: String): Person?

    //User
    fun addUser(user: User)
    fun updateUser(user: User)
    fun removeUser(user: User)
    fun getAllUser(): List<User>
    fun getByIdUser(id: String): User?
    fun getByUserName(userName: String): User?

    //Clock
    fun addClock(clock: Clock)
    fun updateClock(clock: Clock)
    fun removeClock(id: String)
    fun getAllClock(): List<Clock>
    fun getByIdClock(id: String): Clock?
    fun getByDate(dateClock: Date): Clock?
    fun getByType(typeClock: String): Clock?
    fun getByIdPersonClock(idPerson: String): Clock?

    //Attendance
    fun addAttendance(attendance: Attendances)
    fun updateAttendance(attendance: Attendances)
    fun removeAttendance(id: String)
    fun getAllAttendance(): List<Attendances>
    fun getByIdAttendance(id: String): Attendances?
    fun getByDateAttendance(dateAttendance: Date): Attendances?
    fun getByIdPersonAttendance(idPerson: String): Attendances?

}