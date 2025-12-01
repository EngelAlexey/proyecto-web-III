package Controller

import Data.IDataManager
import Data.MemoryDataManager
import Entity.Clock
import android.content.Context
import com.example.clocker.R
import java.util.Date

class ClockController {

    private var dataManager: IDataManager = MemoryDataManager
    private var context: Context

    constructor(context: Context) {
        this.context = context
    }

    fun addClock(clock: Clock) {
        try {
            dataManager.addClock(clock)
            // TODO: Implementar procesamiento de marcas de asistencia
            // AttendanceController.processClockMark(clock)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    fun updateClock(clock: Clock) {
        try {
            dataManager.updateClock(clock)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun removeClock(clock: Clock): Clock? {
        try {
            dataManager.removeClock(clock.IDClock)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
        return clock
    }

    fun getAllClock(): List<Clock> {
        try {
            return dataManager.getAllClock()
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetAll))
        }
    }

    fun getByIdClock(id: String): Clock? {
        try {
            return dataManager.getByIdClock(id)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

    fun getByDate(dateClock: Date): Clock? {
        try {
            return dataManager.getByDate(dateClock)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

    fun getByType(typeClock: String): Clock? {
        try {
            return dataManager.getByType(typeClock)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

    fun getByIdPersonClock(idPerson: String): Clock? {
        try {
            return dataManager.getByIdPersonClock(idPerson)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }
}