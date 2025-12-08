package com.example.clocker

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


object FirebaseManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun logout() {
        auth.signOut()
    }

    fun saveData(collectionName: String, docId: String?, data: Any, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onFailure("Usuario no autenticado")
            return
        }

        val userDocRef = db.collection("users").document(userId).collection(collectionName)

        val task = if (docId != null) {
            userDocRef.document(docId).set(data, SetOptions.merge())
        } else {
            userDocRef.add(data)
        }

        task.addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Error al guardar") }
    }

    fun getData(collectionName: String, onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onFailure("Usuario no autenticado")
            return
        }

        db.collection("users").document(userId).collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.data }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Error al leer") }
    }
}