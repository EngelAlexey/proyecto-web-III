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

        // Botón Gestión de Usuarios
        findViewById<Button>(R.id.btnGestionUsuarios).setOnClickListener {
            val intent = Intent(this, GestionUsuariosActivity::class.java)
            startActivity(intent)
        }

        // Botón Gestión de Zonas
        findViewById<Button>(R.id.btnGestionZonas).setOnClickListener {
            mostrarDialogoSeleccion("Gestión de Zonas", ZoneListActivity::class.java, ZoneActivity::class.java)
        }

        // Botón Gestión de Personal
        findViewById<Button>(R.id.btnGestionPersonal).setOnClickListener {
            val intent = Intent(this, PersonForm::class.java)
            startActivity(intent)
        }

        // Botón Ver Asistencias
        findViewById<Button>(R.id.btnVerAsistencias).setOnClickListener {
            val intent = Intent(this, AttendanceListActivity::class.java)
            startActivity(intent)
        }

        // Botón Consulta Avanzada de Asistencias
        findViewById<Button>(R.id.btnConsultaAvanzada).setOnClickListener {
            val intent = Intent(this, AttendanceQueryActivity::class.java)
            startActivity(intent)
        }

        // Botón Reportes - CORREGIDO
        findViewById<Button>(R.id.btnReportes).setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        // Botón Pantalla de Marca
        findViewById<Button>(R.id.btnPantallaMarca).setOnClickListener {
            mostrarDialogoSeleccion("Control de Marcas", ClockListActivity::class.java, ClockActivity::class.java)
        }
    }

    private fun mostrarDialogoSeleccion(titulo: String, claseLista: Class<*>, claseFormulario: Class<*>) {
        val opciones = arrayOf("Ver Lista", "Agregar Nuevo")

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> { // Ver Lista
                        val intent = Intent(this, claseLista)
                        startActivity(intent)
                    }
                    1 -> { // Agregar Nuevo
                        val intent = Intent(this, claseFormulario)
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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