package Controller

import Entity.Attendances
import Entity.Clock
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.clocker.Firebase.FirestoreDataManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class AttendanceController(private val context: Context) {

    private val firestoreManager = FirestoreDataManager()
    private val TAG = "AttendanceController"

    private val lifecycleOwner: LifecycleOwner?
        get() = context as? LifecycleOwner

    fun processClockMark(clock: Clock) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val dateAttendance = Date.from(
                    clock.DateClock.toLocalDate()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                )

                // Buscar asistencia existente para ese día
                val existingAttendance = firestoreManager.getByDateAttendance(dateAttendance)

                if (existingAttendance == null) {
                    // Primera marca del día → ENTRADA
                    createNewAttendance(clock, dateAttendance)
                } else {
                    // Segunda marca del día → SALIDA
                    updateAttendanceExit(existingAttendance, clock)
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing clock mark: ${e.message}")
            }
        }
    }

    private suspend fun createNewAttendance(clock: Clock, dateAttendance: Date) {
        val timeEntry = Date.from(clock.DateClock.atZone(ZoneId.systemDefault()).toInstant())

        val newAttendance = Attendances(
            IDAttendance = "ATT_${System.currentTimeMillis()}",
            DateAttendance = dateAttendance,
            IDPerson = clock.IDPerson,
            TimeEntry = timeEntry,
            TimeExit = null,
            EntryID = clock.IDClock,
            ExitID = ""
        )

        val result = firestoreManager.addAttendance(newAttendance)
        result.onSuccess {
            Log.d(TAG, "✅ Entrada registrada: ${clock.IDPerson} a las ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeEntry)}")
        }.onFailure { e ->
            Log.e(TAG, "❌ Error creating attendance: ${e.message}")
        }
    }

    private suspend fun updateAttendanceExit(attendance: Attendances, clock: Clock) {
        val timeExit = Date.from(clock.DateClock.atZone(ZoneId.systemDefault()).toInstant())

        attendance.timeExit = timeExit
        attendance.exitID = clock.IDClock

        val result = firestoreManager.updateAttendance(attendance)
        result.onSuccess {
            val horasMinutos = attendance.hoursAttendanceMinutes()
            Log.d(TAG, "✅ Salida registrada: ${clock.IDPerson} a las ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeExit)}")
            Log.d(TAG, "⏱️ Total trabajado: ${horasMinutos / 60}h ${horasMinutos % 60}m")
        }.onFailure { e ->
            Log.e(TAG, "❌ Error updating attendance: ${e.message}")
        }
    }

    fun addAttendance(attendance: Attendances, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.addAttendance(attendance)
            result.onSuccess {
                Log.d(TAG, "✅ Attendance added successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error adding attendance: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateAttendance(attendance: Attendances, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.updateAttendance(attendance)
            result.onSuccess {
                Log.d(TAG, "✅ Attendance updated successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error updating attendance: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun removeAttendance(id: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.removeAttendance(id)
            result.onSuccess {
                Log.d(TAG, "✅ Attendance removed successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error removing attendance: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getAllAttendance(onSuccess: (List<Attendances>) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val attendances = firestoreManager.getAllAttendance()
                Log.d(TAG, "✅ Retrieved ${attendances.size} attendances")
                onSuccess(attendances)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting attendances: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByIdAttendance(id: String, onSuccess: (Attendances?) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val attendance = firestoreManager.getByIdAttendance(id)
                onSuccess(attendance)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting attendance: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByIdPersonAttendance(idPerson: String, onSuccess: (List<Attendances>) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val attendances = firestoreManager.getByIdPersonAttendance(idPerson)
                Log.d(TAG, "✅ Retrieved ${attendances.size} attendances for person: $idPerson")
                onSuccess(attendances)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting attendances by person: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }
}