package com.example.clocker.Firebase

import android.content.Context
import android.util.Log
import com.example.clocker.PasswordHelper
import com.example.clocker.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "FirebaseAuthManager"

    suspend fun registrarUsuario(
        email: String,
        password: String,
        nombreUsuario: String,
        rol: String
    ): Result<Usuario> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Error creando usuario")

            val passwordHash = PasswordHelper.hashPassword(password)

            val usuario = Usuario(
                id = firebaseUser.uid,
                nombreUsuario = nombreUsuario,
                contrasena = passwordHash,
                rol = rol,
                activo = true,
                email = email
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(usuario)
                .await()

            Log.d(TAG, "✅ Usuario registrado: $nombreUsuario")
            Result.success(usuario)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error registrando usuario", e)
            Result.failure(e)
        }
    }

    suspend fun iniciarSesion(email: String, password: String): Result<Usuario> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Usuario no encontrado")

            val documento = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val usuario = documento.toObject(Usuario::class.java)
                ?: throw Exception("Datos de usuario no encontrados")

            if (!usuario.activo) {
                auth.signOut()
                throw Exception("Usuario desactivado")
            }

            Log.d(TAG, "✅ Sesión iniciada: ${usuario.nombreUsuario}")
            Result.success(usuario)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error iniciando sesión", e)
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        auth.signOut()
        Log.d(TAG, "✅ Sesión cerrada")
    }

    fun obtenerUsuarioActual() = auth.currentUser

    fun haySesionActiva() = auth.currentUser != null

    suspend fun cambiarContrasena(nuevaContrasena: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("No hay sesión activa")
            user.updatePassword(nuevaContrasena).await()

            val passwordHash = PasswordHelper.hashPassword(nuevaContrasena)
            firestore.collection("users")
                .document(user.uid)
                .update("contrasena", passwordHash)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restablecerContrasena(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}