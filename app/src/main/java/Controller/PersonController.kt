package Controller

import Entity.Person
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.clocker.Firebase.FirestoreDataManager
import kotlinx.coroutines.launch

class PersonController(private val context: Context) {

    private val firestoreManager = FirestoreDataManager()
    private val TAG = "PersonController"

    // Obtener LifecycleOwner del contexto
    private val lifecycleOwner: LifecycleOwner?
        get() = context as? LifecycleOwner

    fun addPerson(person: Person, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.addPerson(person)
            result.onSuccess {
                Log.d(TAG, "✅ Person added successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error adding person: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun updatePerson(person: Person, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.updatePerson(person)
            result.onSuccess {
                Log.d(TAG, "✅ Person updated successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error updating person: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun removePerson(id: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            val result = firestoreManager.removePerson(id)
            result.onSuccess {
                Log.d(TAG, "✅ Person removed successfully")
                onSuccess()
            }.onFailure { e ->
                Log.e(TAG, "❌ Error removing person: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getAllPerson(onSuccess: (List<Person>) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val persons = firestoreManager.getAllPerson()
                Log.d(TAG, "✅ Retrieved ${persons.size} persons")
                onSuccess(persons)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting persons: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByIdPerson(id: String, onSuccess: (Person?) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val person = firestoreManager.getByIdPerson(id)
                if (person != null) {
                    Log.d(TAG, "✅ Person found: $id")
                } else {
                    Log.d(TAG, "⚠️ Person not found: $id")
                }
                onSuccess(person)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting person: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByFullNamePerson(fullName: String, onSuccess: (Person?) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val person = firestoreManager.getByFullNamePerson(fullName)
                onSuccess(person)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting person by name: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    fun getByIdDocumentPerson(idDocument: String, onSuccess: (Person?) -> Unit, onFailure: (String) -> Unit = {}) {
        lifecycleOwner?.lifecycleScope?.launch {
            try {
                val person = firestoreManager.getByIdDocumentPerson(idDocument)
                onSuccess(person)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting person by document: ${e.message}")
                onFailure(e.message ?: "Error desconocido")
            }
        }
    }

    // ============================================================
    // MÉTODOS SÍNCRONOS (para compatibilidad con código existente)
    // ============================================================

    fun getByIdPersonSync(id: String): Person? {
        // Este método es para compatibilidad temporal
        // En producción, deberías usar la versión asíncrona
        Log.w(TAG, "⚠️ Using sync method - consider migrating to async version")
        return null // Firebase requiere llamadas asíncronas
    }
}