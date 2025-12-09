package Controller

import Data.IDataManager
import Data.MemoryDataManager
import Entity.Person
import android.content.Context
import com.example.clocker.R

class PersonController {

    private var dataManager: IDataManager = Data.FirebaseDataManager
    private var context: Context

    constructor(context: Context){
        this.context=context
    }

    fun addPerson(person: Person){
        try {
            dataManager.addPerson(person)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    fun updatePerson(person: Person){
        try {
            dataManager.updatePerson(person)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun removePerson(person: Person): Person? {
        try {
            dataManager.removePerson(person.ID)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
        return person
    }

    fun getAllPerson(): List<Person>{
        try {
            return dataManager.getAllPerson()
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetAll))
        }
    }

    fun getByIdPerson(id: String): Person? {
        try {
            return dataManager.getByIdPerson(id)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

    fun getByFullName(fullname: String): Person? {
        try {
            return dataManager.getByFullNamePerson(fullname)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

}