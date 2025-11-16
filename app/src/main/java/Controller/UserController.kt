package Controller

import Data.IDataManager
import Data.MemoryDataManager
import Entity.User
import android.content.Context
import com.example.clocker.R

class UserController {

    private var dataManager: IDataManager = MemoryDataManager
    private var context: Context

    constructor(context: Context){
        this.context=context
    }

    fun addUser(user: User){
        try {
            dataManager.addUser(user)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    fun updatePerson(user: User){
        try {
            dataManager.updateUser(user)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun removePerson(id: String) {
        try {
            dataManager.removeUser(id)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
    }

    fun getAllPerson(): List<User>{
        try {
            return dataManager.getAllUser()
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetAll))
        }
    }

    fun getByIdPerson(id: String): User? {
        try {
            return dataManager.getByIdUser(id)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

    fun getByFullName(fullname: String): User? {
        try {
            return dataManager.getByUserName(fullname)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

}