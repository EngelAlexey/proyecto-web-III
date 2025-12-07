package com.example.clocker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MenuAdministradorActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_administrador)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance() // Inicializar Firebase Auth

        if (!sessionManager.esAdministrador()) {
            Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tvBienvenida: TextView = findViewById(R.id.tvBienvenida)
        val usuario = sessionManager.obtenerUsuarioActual()
        tvBienvenida.text = "Bienvenido, ${usuario?.nombreUsuario ?: "Administrador"}"

        configurarBotones()
    }

    private fun configurarBotones() {
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        findViewById<Button>(R.id.btnGestionUsuarios).setOnClickListener {
            val intent = Intent(this, GestionUsuariosActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnGestionZonas).setOnClickListener {
            mostrarDialogoSeleccion("Gestión de Zonas", ZoneListActivity::class.java, ZoneActivity::class.java)
        }

        findViewById<Button>(R.id.btnGestionPersonal).setOnClickListener {
            val intent = Intent(this, PersonForm::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnVerAsistencias).setOnClickListener {
            val intent = Intent(this, AttendanceListActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnConsultaAvanzada).setOnClickListener {
            val intent = Intent(this, AttendanceQueryActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnReportes).setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnPantallaMarca).setOnClickListener {
            mostrarDialogoSeleccion("Control de Marcas", ClockListActivity::class.java, ClockActivity::class.java)
        }

        findViewById<Button>(R.id.btnDocumentacion).setOnClickListener {
            val intent = Intent(this, DocumentacionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarDialogoSeleccion(titulo: String, claseLista: Class<*>, claseFormulario: Class<*>) {
        val opciones = arrayOf("Ver Lista", "Agregar Nuevo")

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(this, claseLista)
                        startActivity(intent)
                    }
                    1 -> {
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
        auth.signOut()
        sessionManager.cerrarSesion()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}