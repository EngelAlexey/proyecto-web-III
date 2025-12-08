package Controller

import Entity.Clock
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.clocker.Firebase.FirestoreDataManager
import com.example.clocker.Firebase.FirebaseStorageManager
import kotlinx.coroutines.launch

class ClockController(private val context: Context) {

    private val firestoreManager = FirestoreDataManager()
    private val storageManager = FirebaseStorageManager()
    private val TAG = "ClockController"

    private val lifecycleOwner: LifecycleOwner?
        get() = context as? LifecycleOwner

    fun addClock(
        clock: Clock,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {},
        onProgress: (String) -> Unit = {}
    ) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                // 1. Subir foto a Firebase Storage
                onProgress("Subiendo foto...")
                val photoResult = storageManager.subirFotoClock(clock.IDClock, clock.Photo)

                photoResult.onSuccess { photoUrl ->
                    Log.d(TAG, "✅ Photo uploaded: $photoUrl")

                    // 2. Guardar Clock en Firestore con URL de la foto
                    onProgress("Guardando marca...")
                    val clockResult = firestoreManager.addClock(clock, photoUrl)

                    clockResult.onSuccess {
                        Log.d(TAG, "✅ Clock saved successfully")

                        // 3. Procesar asistencia
                        val attendanceController = AttendanceController(context)
                        attendanceController.processClockMark(clock)

                        onSuccess()
                    }.onFailure { e ->
                        Log.e(TAG, "❌ Error saving clock: ${e.message}")
                        onFailure("Error guardando marca: ${e.message}")
                    }

                }.onFailure { e ->
                    Log.e(TAG, "❌ Error uploading photo: ${e.message}")
                    onFailure("Error subiendo foto: ${e.message}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Unexpected error: ${e.message}")
                onFailure("Error inesperado: ${e.message}")
            }
        }
    }

    fun updateClock(clock: Clock, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.updateClock(clock)
            result.onSuccess {
                Log.d(TAG, "✅ Clock updated successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error updating clock: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun removeClock(id: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.removeClock(id)
            result.onSuccess {
                Log.d(TAG, "✅ Clock removed successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error removing clock: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getAllClock(onSuccess: (List<Clock>) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val clocks = firestoreManager.getAllClock()
                Log.d(TAG, "✅ Retrieved ${clocks.size} clocks")
                onSuccess(clocks)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting clocks: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByIdClock(id: String, onSuccess: (Clock?) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val clock = firestoreManager.getByIdClock(id)
                onSuccess(clock)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting clock: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByIdPersonClock(idPerson: String, onSuccess: (List<Clock>) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val clocks = firestoreManager.getByIdPersonClock(idPerson)
                Log.d(TAG, "✅ Retrieved ${clocks.size} clocks for person: $idPerson")
                onSuccess(clocks)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting clocks by person: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }
}