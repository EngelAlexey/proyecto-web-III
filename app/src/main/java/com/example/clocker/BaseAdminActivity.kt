package com.example.clocker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseAdminActivity : AppCompatActivity() {

    protected lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // Verificar que haya sesión activa
        if (!sessionManager.isLoggedIn()) {
            redirigirAlLogin("Debe iniciar sesión")
            return
        }

        // Verificar que sea administrador
        if (!sessionManager.esAdministrador()) {
            redirigirAlLogin("Acceso denegado. Solo administradores.")
            return
        }
    }

    private fun redirigirAlLogin(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        sessionManager.cerrarSesion()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    protected fun cerrarSesionSegura() {
        sessionManager.cerrarSesion()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}