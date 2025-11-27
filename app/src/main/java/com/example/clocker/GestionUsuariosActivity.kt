package com.example.clocker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class GestionUsuariosActivity : BaseAdminActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuariosAdapter
    private lateinit var tvNoUsuarios: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GestionUsuarios", "onCreate iniciado")
        setContentView(R.layout.activity_gestion_usuarios)

        sessionManager = SessionManager(this)




        Log.d("GestionUsuarios", "Usuario es administrador")

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        tvNoUsuarios = findViewById(R.id.tvNoUsuarios)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UsuariosAdapter(
            usuarios = emptyList(),
            onEditClick = { usuario -> mostrarDialogoEditar(usuario) },
            onPasswordClick = { usuario -> mostrarDialogoCambiarPassword(usuario) },
            onToggleEstadoClick = { usuario -> toggleEstadoUsuario(usuario) }
        )
        recyclerView.adapter = adapter

        // Configurar botones
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val btnNuevo = findViewById<Button>(R.id.btnNuevoUsuario)

        Log.d("GestionUsuarios", "btnVolver encontrado: ${btnVolver != null}")
        Log.d("GestionUsuarios", "btnNuevo encontrado: ${btnNuevo != null}")

        btnVolver?.setOnClickListener {
            Log.d("GestionUsuarios", "Click en btnVolver")
            finish()
        }

        btnNuevo?.setOnClickListener {
            Log.d("GestionUsuarios", "Click en btnNuevo")
            mostrarDialogoNuevoUsuario()
        }

        // Cargar usuarios
        cargarUsuarios()
        Log.d("GestionUsuarios", "onCreate completado")
    }

    private fun cargarUsuarios() {
        val usuarios = UsuarioManager.obtenerTodosLosUsuarios()
        Log.d("GestionUsuarios", "Usuarios cargados: ${usuarios.size}")

        if (usuarios.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvNoUsuarios.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvNoUsuarios.visibility = View.GONE
            adapter.actualizarLista(usuarios)
        }
    }

    private fun mostrarDialogoNuevoUsuario() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario, null)

        val etNombreUsuario = dialogView.findViewById<TextInputEditText>(R.id.etNombreUsuario)
        val etPassword = dialogView.findViewById<TextInputEditText>(R.id.etPassword)
        val radioAdministrador = dialogView.findViewById<RadioButton>(R.id.radioAdministrador)
        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloDialog)

        tvTitulo.text = "Nuevo Usuario"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val nombreUsuario = etNombreUsuario.text.toString().trim()
            val password = etPassword.text.toString()
            val rol = if (radioAdministrador.isChecked) "Administrador" else "Reloj"

            // Validaciones
            if (nombreUsuario.isEmpty()) {
                Toast.makeText(this, "Ingrese el nombre de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear usuario
            val exito = UsuarioManager.crearUsuario(nombreUsuario, password, rol)

            if (exito) {
                Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                cargarUsuarios()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun mostrarDialogoEditar(usuario: Usuario) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario, null)

        val etNombreUsuario = dialogView.findViewById<TextInputEditText>(R.id.etNombreUsuario)
        val layoutPassword = dialogView.findViewById<TextInputLayout>(R.id.layoutPassword)
        val radioAdministrador = dialogView.findViewById<RadioButton>(R.id.radioAdministrador)
        val radioReloj = dialogView.findViewById<RadioButton>(R.id.radioReloj)
        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloDialog)

        tvTitulo.text = "Editar Usuario"

        // Prellenar datos
        etNombreUsuario.setText(usuario.nombreUsuario)
        layoutPassword.visibility = View.GONE

        if (usuario.rol == "Administrador") {
            radioAdministrador.isChecked = true
        } else {
            radioReloj.isChecked = true
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val nuevoNombre = etNombreUsuario.text.toString().trim()
            val nuevoRol = if (radioAdministrador.isChecked) "Administrador" else "Reloj"

            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "Ingrese el nombre de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val exito = UsuarioManager.actualizarUsuario(usuario.id, nuevoNombre, nuevoRol)

            if (exito) {
                Toast.makeText(this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show()
                cargarUsuarios()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun mostrarDialogoCambiarPassword(usuario: Usuario) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cambiar_password, null)

        val tvUsuarioPassword = dialogView.findViewById<TextView>(R.id.tvUsuarioPassword)
        val etNuevaPassword = dialogView.findViewById<TextInputEditText>(R.id.etNuevaPassword)
        val etConfirmarPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmarPassword)

        tvUsuarioPassword.text = "Usuario: ${usuario.nombreUsuario}"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCancelarPassword).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnGuardarPassword).setOnClickListener {
            val nuevaPassword = etNuevaPassword.text.toString()
            val confirmarPassword = etConfirmarPassword.text.toString()

            if (nuevaPassword.isEmpty()) {
                Toast.makeText(this, "Ingrese la nueva contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nuevaPassword.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nuevaPassword != confirmarPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val exito = UsuarioManager.cambiarContrasena(usuario.id, nuevaPassword)

            if (exito) {
                Toast.makeText(this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Error al cambiar contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun toggleEstadoUsuario(usuario: Usuario) {
        val accion = if (usuario.activo) "desactivar" else "activar"

        AlertDialog.Builder(this)
            .setTitle("Confirmar")
            .setMessage("¿Está seguro que desea $accion al usuario ${usuario.nombreUsuario}?")
            .setPositiveButton("Sí") { _, _ ->
                val exito = if (usuario.activo) {
                    UsuarioManager.desactivarUsuario(usuario.id)
                } else {
                    UsuarioManager.activarUsuario(usuario.id)
                }

                if (exito) {
                    Toast.makeText(this, "Usuario ${if (usuario.activo) "desactivado" else "activado"} exitosamente", Toast.LENGTH_SHORT).show()
                    cargarUsuarios()
                } else {
                    Toast.makeText(this, "Error al cambiar estado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}