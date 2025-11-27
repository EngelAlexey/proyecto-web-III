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
            // La actividad GestionUsuarios ya tiene lista y botón de crear integrado,
            // así que vamos directo a ella.
            val intent = Intent(this, GestionUsuariosActivity::class.java)
            startActivity(intent)
        }

        // Botón Gestión de Zonas
        findViewById<Button>(R.id.btnGestionZonas).setOnClickListener {
            mostrarDialogoSeleccion("Gestión de Zonas", ZoneListActivity::class.java, ZoneActivity::class.java)
        }

        // Botón Gestión de Personal
        // Nota: Asumo que PersonForm es el formulario. Si no tienes una lista de personal creada aún,
        // podrías dirigir solo al formulario o crear una PersonListActivity futura.
        // Por ahora, usaré PersonForm para "Agregar" y dejaré "Ver Lista" pendiente o apuntando a lo mismo si deseas.
        findViewById<Button>(R.id.btnGestionPersonal).setOnClickListener {
            // Como no veo una 'PersonListActivity' en tus archivos, asumiré que quieres ir al formulario directo
            // O si deseas el diálogo, necesitarías la clase de la lista.
            // Opción A: Ir directo al formulario (actual)
            val intent = Intent(this, PersonForm::class.java)
            startActivity(intent)

            // Opción B (Si tuvieras lista):
            // mostrarDialogoSeleccion("Personal", PersonListActivity::class.java, PersonForm::class.java)
        }

        // Botón Ver Asistencias
        findViewById<Button>(R.id.btnVerAsistencias).setOnClickListener {
            // Asistencias suele ser solo lectura/lista
            val intent = Intent(this, AttendanceListActivity::class.java)
            startActivity(intent)
        }

        // Botón Reportes
        findViewById<Button>(R.id.btnReportes).setOnClickListener {
            Toast.makeText(this, "Módulo de Reportes (en desarrollo)", Toast.LENGTH_SHORT).show()
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