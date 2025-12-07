package com.example.clocker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GestionUsuariosActivity : BaseAdminActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuariosAdapter
    private lateinit var tvNoUsuarios: TextView
    // 'db' y 'auth' ya vienen heredados de BaseAdminActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        tvNoUsuarios = findViewById(R.id.tvNoUsuarios)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar Adapter con las acciones
        adapter = UsuariosAdapter(
            usuarios = emptyList(),
            onEditClick = { usuario -> mostrarDialogoEditar(usuario) },
            onPasswordClick = { usuario -> mostrarDialogoResetPassword(usuario) },
            onToggleEstadoClick = { usuario -> toggleEstadoUsuario(usuario) }
        )
        recyclerView.adapter = adapter

        // Botones de la interfaz
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val btnNuevo = findViewById<Button>(R.id.btnNuevoUsuario)

        btnVolver?.setOnClickListener { finish() }
        btnNuevo?.setOnClickListener { mostrarDialogoNuevoUsuario() }

        cargarUsuariosDesdeFirebase()
    }

    private fun cargarUsuariosDesdeFirebase() {
        db.collection("users")
            .orderBy("nombreUsuario", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val listaUsuarios = result.toObjects(Usuario::class.java)

                if (listaUsuarios.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    tvNoUsuarios.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    tvNoUsuarios.visibility = View.GONE
                    adapter.actualizarLista(listaUsuarios)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    // --- 1. CREAR USUARIO (TRUCO INSTANCIA SECUNDARIA) ---
    private fun mostrarDialogoNuevoUsuario() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario, null)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombreUsuario)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etPassword) // Reusamos el campo para Email o Password
        etEmail.hint = "Correo Electrónico (Obligatorio)" // Cambiamos hint visualmente

        val etPass = TextInputEditText(this) // Campo extra para pass si el layout no lo tiene separado
        // Nota: Asumiré que tu layout dialog_usuario tiene campo para contraseña.
        // Si usas correo y pass, asegúrate de pedir ambos.
        // Para simplificar, usaremos el campo etPassword para la contraseña y generaremos el correo automático.
        etEmail.hint = "Contraseña"

        val radioAdmin = dialogView.findViewById<RadioButton>(R.id.radioAdministrador)
        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloDialog)
        tvTitulo.text = "Nuevo Usuario"

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        dialogView.findViewById<Button>(R.id.btnCancelar).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val password = etEmail.text.toString().trim() // Usando el campo existente
            val rol = if (radioAdmin.isChecked) "Administrador" else "Reloj"

            if (nombre.isNotEmpty() && password.length >= 6) {
                crearUsuarioSinCerrarSesionAdmin(nombre, password, rol, dialog)
            } else {
                Toast.makeText(this, "Nombre requerido y Pass mín. 6 chars", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun crearUsuarioSinCerrarSesionAdmin(nombre: String, pass: String, rol: String, dialog: AlertDialog) {
        val emailGenerado = "$nombre@clocker.com".lowercase().replace(" ", "")

        // 1. Crear configuración para una app secundaria
        val options = FirebaseOptions.Builder()
            .setApiKey(FirebaseApp.getInstance().options.apiKey)
            .setApplicationId(FirebaseApp.getInstance().options.applicationId)
            .setProjectId(FirebaseApp.getInstance().options.projectId)
            .build()

        // 2. Inicializar App secundaria (si no existe ya)
        val secondaryAppName = "SecondaryApp"
        val secondaryApp = try {
            FirebaseApp.getInstance(secondaryAppName)
        } catch (e: IllegalStateException) {
            FirebaseApp.initializeApp(this, options, secondaryAppName)
        }

        // 3. Obtener Auth de la app secundaria
        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

        // 4. Crear usuario en la app secundaria (NO afecta al Admin logueado en la app principal)
        secondaryAuth.createUserWithEmailAndPassword(emailGenerado, pass)
            .addOnSuccessListener { result ->
                val nuevoUid = result.user!!.uid

                // 5. Guardar datos en Firestore (usando la instancia PRINCIPAL que tiene permisos de admin)
                val nuevoUsuario = Usuario(
                    id = nuevoUid,
                    nombreUsuario = nombre,
                    email = emailGenerado,
                    rol = rol,
                    activo = true
                )

                db.collection("users").document(nuevoUid).set(nuevoUsuario)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Usuario creado: $nombre", Toast.LENGTH_SHORT).show()
                        secondaryAuth.signOut() // Limpiar sesión secundaria
                        cargarUsuariosDesdeFirebase() // Recargar lista
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error guardando en BD", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error Auth: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // --- 2. EDITAR USUARIO ---
    private fun mostrarDialogoEditar(usuario: Usuario) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario, null)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombreUsuario)
        val layoutPass = dialogView.findViewById<TextInputLayout>(R.id.layoutPassword)
        val radioAdmin = dialogView.findViewById<RadioButton>(R.id.radioAdministrador)
        val radioReloj = dialogView.findViewById<RadioButton>(R.id.radioReloj)
        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloDialog)

        tvTitulo.text = "Editar Usuario"
        layoutPass.visibility = View.GONE // Ocultamos contraseña en edición

        etNombre.setText(usuario.nombreUsuario)
        if (usuario.esAdministrador) radioAdmin.isChecked = true else radioReloj.isChecked = true

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        dialogView.findViewById<Button>(R.id.btnCancelar).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            val nuevoRol = if (radioAdmin.isChecked) "Administrador" else "Reloj"

            if (nuevoNombre.isEmpty()) {
                etNombre.error = "Requerido"
                return@setOnClickListener
            }

            val updates = mapOf(
                "nombreUsuario" to nuevoNombre,
                "rol" to nuevoRol
            )

            db.collection("users").document(usuario.id).update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    cargarUsuariosDesdeFirebase()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
        }
        dialog.show()
    }

    // --- 3. RESTABLECER CONTRASEÑA (EMAIL) ---
    private fun mostrarDialogoResetPassword(usuario: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Restablecer Contraseña")
            .setMessage("Se enviará un correo a '${usuario.email}' para que el usuario restablezca su contraseña.\n\n¿Confirmar envío?")
            .setPositiveButton("Enviar Correo") { _, _ ->
                auth.sendPasswordResetEmail(usuario.email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Correo enviado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // --- 4. ACTIVAR / DESACTIVAR ---
    private fun toggleEstadoUsuario(usuario: Usuario) {
        val nuevoEstado = !usuario.activo
        val accion = if (nuevoEstado) "Activar" else "Desactivar"

        AlertDialog.Builder(this)
            .setTitle("$accion Usuario")
            .setMessage("¿Estás seguro de que deseas $accion a ${usuario.nombreUsuario}?")
            .setPositiveButton("Sí") { _, _ ->
                db.collection("users").document(usuario.id).update("activo", nuevoEstado)
                    .addOnSuccessListener {
                        cargarUsuariosDesdeFirebase() // Refrescar vista
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al cambiar estado", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }
}