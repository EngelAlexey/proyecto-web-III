package com.example.clocker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()
    private val gson = Gson()

    companion object {
        private const val KEY_USER_JSON = "user_json"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun guardarSesion(usuario: Usuario) {
        val usuarioJson = gson.toJson(usuario)
        editor.putString(KEY_USER_JSON, usuarioJson)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun obtenerUsuarioActual(): Usuario? {
        val usuarioJson = prefs.getString(KEY_USER_JSON, null)
        return if (usuarioJson != null) {
            try {
                gson.fromJson(usuarioJson, Usuario::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Verifica rol "Administrador" (como está en tu BD) o "admin" por compatibilidad
    fun esAdministrador(): Boolean {
        val usuario = obtenerUsuarioActual()
        return usuario?.rol == "Administrador" || usuario?.rol == "admin"
    }

    fun esReloj(): Boolean {
        val usuario = obtenerUsuarioActual()
        return usuario?.rol == "Reloj" || usuario?.rol == "reloj"
    }

    fun cerrarSesion() {
        editor.clear()
        editor.apply()
    }
}