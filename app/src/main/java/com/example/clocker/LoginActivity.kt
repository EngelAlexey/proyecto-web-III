package com.example.clocker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var tvError: TextView
    private lateinit var btnLogin: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Verificar si ya hay sesión activa
        if (sessionManager.isLoggedIn()) {
            redirigirSegunRol()
            return
        }

        // Inicializar vistas
        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        tvError = findViewById(R.id.tvError)
        btnLogin = findViewById(R.id.btnLogin)

        // Configurar botón de login
        btnLogin.setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {
        val usuario = etUsuario.text.toString().trim()
        val contrasena = etContrasena.text.toString()

        // Validaciones básicas
        if (usuario.isEmpty()) {
            mostrarError("Por favor ingrese el usuario")
            return
        }

        if (contrasena.isEmpty()) {
            mostrarError("Por favor ingrese la contraseña")
            return
        }

        // Validar credenciales
        val usuarioValido = UsuarioManager.validarCredenciales(usuario, contrasena)

        if (usuarioValido != null) {
            // Login exitoso
            ocultarError()
            sessionManager.guardarSesion(usuarioValido)
            redirigirSegunRol()
        } else {
            // Login fallido
            mostrarError("Usuario o contraseña incorrectos")
        }
    }

    private fun redirigirSegunRol() {
        val usuario = sessionManager.obtenerUsuarioActual()

        if (usuario == null) {
            mostrarError("Error al obtener información del usuario")
            return
        }

        when {
            usuario.esAdministrador() -> {
                // Ir a tu MenuAdministradorActivity
                val intent = Intent(this, MenuAdministradorActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            usuario.esReloj() -> {
                // Ir directo a ClockActivity (la pantalla de marca que ya existe)
                val intent = Intent(this, ClockActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        finish()
    }
    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE
    }

    private fun ocultarError() {
        tvError.visibility = View.GONE
    }
}