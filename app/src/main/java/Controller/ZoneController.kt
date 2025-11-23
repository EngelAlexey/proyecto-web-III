package Controller

import Data.IDataManager
import Data.MemoryDataManager
import Entity.Zone
import android.content.Context
import com.example.clocker.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ZoneController {

    private var dataManager: IDataManager = MemoryDataManager
    private var context: Context

    constructor(context: Context){
        this.context = context
    }

    fun addZone(zone: Zone){
        try {
            dataManager.addZone(zone)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    fun updateZone(zone: Zone){
        try {
            dataManager.updateZone(zone)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun removeZone(id: String) {
        try {
            dataManager.removeZone(id)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
    }

    fun getAllZone(): List<Zone>{
        try {
            return dataManager.getAllZone()
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetAll))
        }
    }

    fun getByIdZone(id: String): Zone? {
        try {
            return dataManager.getByIdZone(id)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

    fun getByCodeZone(code: String): Zone? {
        try {
            return dataManager.getByCodeZone(code)
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }

    fun getActiveZones(): List<Zone> {
        try {
            return dataManager.getActiveZones()
        } catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetAll))
        }
    }

    fun isClockValidInZone(zone: Zone, clockTime: String): Boolean {
        try {
            if (!zone.Status) return false

            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val currentTime = LocalTime.parse(clockTime, formatter)
            val startTime = LocalTime.parse(zone.StartTime, formatter)
            val endTime = LocalTime.parse(zone.EndTime, formatter)

            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime) ||
                    currentTime == startTime || currentTime == endTime
        } catch (e: Exception){
            return false
        }
    }
}