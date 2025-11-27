package com.example.clocker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MenuAdministradorActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_administrador)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Verificar que sea administrador
        if (!sessionManager.esAdministrador()) {
            Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar bienvenida
        val tvBienvenida: TextView = findViewById(R.id.tvBienvenida)
        val usuario = sessionManager.obtenerUsuarioActual()
        tvBienvenida.text = "Bienvenido, ${usuario?.nombreUsuario ?: "Administrador"}"

        // Configurar botones
        configurarBotones()
    }

    private fun configurarBotones() {
        // Botón Cerrar Sesión
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        // Botón Gestión de Usuarios (TU MÓDULO)
        findViewById<Button>(R.id.btnGestionUsuarios).setOnClickListener {
            // TODO: Crear GestionUsuariosActivity
            Toast.makeText(this, "Gestión de Usuarios (en desarrollo)", Toast.LENGTH_SHORT).show()
        }

        // Botón Gestión de Zonas (módulo de otros)
        findViewById<Button>(R.id.btnGestionZonas).setOnClickListener {
            // Aquí iría a ZoneActivity cuando esté lista
            Toast.makeText(this, "Módulo de Zonas (en desarrollo)", Toast.LENGTH_SHORT).show()
        }
// Botón Gestión de Usuarios (TU MÓDULO)
        findViewById<Button>(R.id.btnGestionUsuarios).setOnClickListener {
            val intent = Intent(this, GestionUsuariosActivity::class.java)
            startActivity(intent)
        }

        // Botón Ver Asistencias (módulo de otros)
        findViewById<Button>(R.id.btnVerAsistencias).setOnClickListener {
            Toast.makeText(this, "Módulo de Asistencias (en desarrollo)", Toast.LENGTH_SHORT).show()
        }

        // Botón Reportes (módulo de otros)
        findViewById<Button>(R.id.btnReportes).setOnClickListener {
            Toast.makeText(this, "Módulo de Reportes (en desarrollo)", Toast.LENGTH_SHORT).show()
        }

        // Botón Pantalla de Marca (para pruebas)
        findViewById<Button>(R.id.btnPantallaMarca).setOnClickListener {
            // Aquí iría a ClockActivity
            Toast.makeText(this, "Pantalla de Marca (en desarrollo)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        sessionManager.cerrarSesion()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}