package Controller

import Entity.Zone
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.clocker.Firebase.FirestoreDataManager
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime

class ZoneController(private val context: Context) {

    private val firestoreManager = FirestoreDataManager()
    private val TAG = "ZoneController"

    private val lifecycleOwner: LifecycleOwner?
        get() = context as? LifecycleOwner

    fun addZone(zone: Zone, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.addZone(zone)
            result.onSuccess {
                Log.d(TAG, "✅ Zone added successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error adding zone: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateZone(zone: Zone, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.updateZone(zone)
            result.onSuccess {
                Log.d(TAG, "✅ Zone updated successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error updating zone: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun removeZone(id: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.removeZone(id)
            result.onSuccess {
                Log.d(TAG, "✅ Zone removed successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error removing zone: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getAllZone(onSuccess: (List<Zone>) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val zones = firestoreManager.getAllZone()
                Log.d(TAG, "✅ Retrieved ${zones.size} zones")
                onSuccess(zones)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting zones: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByIdZone(id: String, onSuccess: (Zone?) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val zone = firestoreManager.getByIdZone(id)
                onSuccess(zone)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting zone: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByCodeZone(code: String): Zone? {
        // Método síncrono temporal - en producción usar versión async
        Log.w(TAG, "⚠️ Using sync method - consider migrating to async version")
        return null
    }

    fun getActiveZones(onSuccess: (List<Zone>) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val zones = firestoreManager.getActiveZones()
                Log.d(TAG, "✅ Retrieved ${zones.size} active zones")
                onSuccess(zones)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting active zones: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun isClockValidInZone(zone: Zone, dateTime: LocalDateTime): Boolean {
        val currentDayOfWeek: DayOfWeek = dateTime.dayOfWeek
        val currentTime = dateTime.toLocalTime()

        // Verificar si el día está permitido
        if (!zone.Days.contains(currentDayOfWeek)) {
            Log.d(TAG, "❌ Day not allowed: $currentDayOfWeek")
            return false
        }

        // Verificar si está dentro del horario
        val isWithinTime = !currentTime.isBefore(zone.StartTime) && !currentTime.isAfter(zone.EndTime)

        if (!isWithinTime) {
            Log.d(TAG, "❌ Time not allowed: $currentTime (allowed: ${zone.StartTime} - ${zone.EndTime})")
        } else {
            Log.d(TAG, "✅ Clock valid in zone")
        }

        return isWithinTime
    }
}