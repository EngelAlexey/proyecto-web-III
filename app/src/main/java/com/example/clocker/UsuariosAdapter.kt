package com.example.clocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsuariosAdapter(
    private var usuarios: List<Usuario>,
    private val onEditClick: (Usuario) -> Unit,
    private val onPasswordClick: (Usuario) -> Unit,
    private val onToggleEstadoClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreUsuario: TextView = view.findViewById(R.id.tvNombreUsuario)
        val tvRol: TextView = view.findViewById(R.id.tvRol)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnCambiarPassword: Button = view.findViewById(R.id.btnCambiarPassword)
        val btnToggleEstado: Button = view.findViewById(R.id.btnToggleEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]

        // Nombre de usuario
        holder.tvNombreUsuario.text = usuario.nombreUsuario

        // Rol con color
        holder.tvRol.text = usuario.rol
        holder.tvRol.setBackgroundColor(
            when (usuario.rol) {
                "Administrador" -> android.graphics.Color.parseColor("#673AB7")
                "Reloj" -> android.graphics.Color.parseColor("#2196F3")
                else -> android.graphics.Color.GRAY
            }
        )

        // Estado
        if (usuario.activo) {
            holder.tvEstado.text = "● Activo"
            holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            holder.btnToggleEstado.text = "Desactivar"
            holder.btnToggleEstado.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
        } else {
            holder.tvEstado.text = "● Inactivo"
            holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#999999"))
            holder.btnToggleEstado.text = "Activar"
            holder.btnToggleEstado.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
        }

        // Botones
        holder.btnEditar.setOnClickListener {
            onEditClick(usuario)
        }

        holder.btnCambiarPassword.setOnClickListener {
            onPasswordClick(usuario)
        }

        holder.btnToggleEstado.setOnClickListener {
            onToggleEstadoClick(usuario)
        }
    }

    override fun getItemCount(): Int = usuarios.size

    fun actualizarLista(nuevaLista: List<Usuario>) {
        usuarios = nuevaLista
        notifyDataSetChanged()
    }
}