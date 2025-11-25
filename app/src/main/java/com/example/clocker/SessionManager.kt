package com.example.clocker

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("SessionPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROL = "rol"
        private const val KEY_IS_LOGGED = "is_logged"
    }

    fun guardarSesion(usuario: Usuario) {
        prefs.edit().apply {
            putString(KEY_USER_ID, usuario.id)
            putString(KEY_USERNAME, usuario.nombreUsuario)
            putString(KEY_ROL, usuario.rol)
            putBoolean(KEY_IS_LOGGED, true)
            apply()
        }
    }

    fun obtenerUsuarioActual(): Usuario? {
        if (!isLoggedIn()) return null

        return Usuario(
            id = prefs.getString(KEY_USER_ID, "") ?: "",
            nombreUsuario = prefs.getString(KEY_USERNAME, "") ?: "",
            contrasena = "", // No guardamos la contraseña en sesión
            rol = prefs.getString(KEY_ROL, "") ?: ""
        )
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED, false)
    }

    fun cerrarSesion() {
        // Limpiar todas las preferencias
        prefs.edit().clear().commit() // Usar commit() en lugar de apply() para asegurar limpieza inmediata

        // Log para debugging (opcional)
        android.util.Log.d("SessionManager", "Sesión cerrada y datos limpiados")
    }

    fun esAdministrador(): Boolean {
        return prefs.getString(KEY_ROL, "") == "Administrador"
    }

    fun esReloj(): Boolean {
        return prefs.getString(KEY_ROL, "") == "Reloj"
    }
}