package com.example.clocker

object UsuarioManager {

    private val usuarios = mutableListOf<Usuario>()

    init {
        // Usuarios por defecto para pruebas
        crearUsuario("admin", "admin123", "Administrador")
        crearUsuario("reloj1", "reloj123", "Reloj")
    }

    fun crearUsuario(nombreUsuario: String, contrasena: String, rol: String): Boolean {
        if (usuarios.any { it.nombreUsuario == nombreUsuario }) {
            return false
        }

        val id = "USER_${System.currentTimeMillis()}"
        val contrasenaHasheada = PasswordHelper.hashPassword(contrasena)

        val nuevoUsuario = Usuario(
            id = id,
            nombreUsuario = nombreUsuario,
            contrasena = contrasenaHasheada,
            rol = rol,
            activo = true,
            email = "$nombreUsuario@clocker.com"
        )

        usuarios.add(nuevoUsuario)
        return true
    }

    fun validarCredenciales(nombreUsuario: String, contrasena: String): Usuario? {
        val usuario = usuarios.find { it.nombreUsuario == nombreUsuario }

        if (usuario == null) return null
        if (!usuario.activo) return null

        // CORRECCIÓN: Obtenemos la contraseña de forma segura
        val passwordAlmacenada = usuario.contrasena

        // Si la contraseña en BD es nula, no podemos validar => retornamos null
        if (passwordAlmacenada == null) return null

        // Ahora passwordAlmacenada es String (no nulo) y el error desaparece
        val passwordValido = PasswordHelper.verificarPassword(contrasena, passwordAlmacenada)

        return if (passwordValido) usuario else null
    }

    fun obtenerTodosLosUsuarios(): List<Usuario> {
        return usuarios.toList()
    }

    fun obtenerUsuariosActivos(): List<Usuario> {
        return usuarios.filter { it.activo }
    }

    fun actualizarUsuario(id: String, nuevoNombre: String?, nuevoRol: String?): Boolean {
        val usuario = usuarios.find { it.id == id } ?: return false

        nuevoNombre?.let { usuario.nombreUsuario = it }
        nuevoRol?.let { usuario.rol = it }

        return true
    }

    fun cambiarContrasena(id: String, nuevaContrasena: String): Boolean {
        val usuario = usuarios.find { it.id == id } ?: return false
        usuario.contrasena = PasswordHelper.hashPassword(nuevaContrasena)
        return true
    }

    fun desactivarUsuario(id: String): Boolean {
        val usuario = usuarios.find { it.id == id } ?: return false
        usuario.activo = false
        return true
    }

    fun activarUsuario(id: String): Boolean {
        val usuario = usuarios.find { it.id == id } ?: return false
        usuario.activo = true
        return true
    }

    fun eliminarUsuario(id: String): Boolean {
        return usuarios.removeIf { it.id == id }
    }
}