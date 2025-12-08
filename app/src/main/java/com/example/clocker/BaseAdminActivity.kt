package com.example.clocker

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// "open" permite que otras clases hereden de esta
open class BaseAdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var sessionManager: SessionManager

    // Declaramos auth y db como protected para que GestionUsuariosActivity pueda usarlas
    protected lateinit var auth: FirebaseAuth
    protected lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No llamamos a setContentView aquí, lo hace la clase hija

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun setContentView(layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.activity_base_admin, null)
        val activityContainer = fullView.findViewById<FrameLayout>(R.id.activity_content)
        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(fullView)

        configurarToolbarYDrawer()
    }

    private fun configurarToolbarYDrawer() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        actualizarHeaderMenu(navView)
    }

    private fun actualizarHeaderMenu(navView: NavigationView) {
        val headerView = navView.getHeaderView(0)
        val tvUsuario = headerView.findViewById<TextView>(R.id.tvMenuUsuario)
        val tvCorreo = headerView.findViewById<TextView>(R.id.tvMenuCorreo)

        val user = auth.currentUser
        if (user != null) {
            tvCorreo.text = user.email
            val localUser = sessionManager.obtenerUsuarioActual()
            tvUsuario.text = localUser?.nombreUsuario ?: "Admin"
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_usuarios -> {
                if (this !is GestionUsuariosActivity) {
                    startActivity(Intent(this, GestionUsuariosActivity::class.java))
                    finish()
                }
            }
            R.id.nav_reportes -> {
                startActivity(Intent(this, ReportActivity::class.java))
            }
            R.id.nav_logout -> {
                cerrarSesionSegura()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    protected fun cerrarSesionSegura() {
        auth.signOut()
        sessionManager.cerrarSesion()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}