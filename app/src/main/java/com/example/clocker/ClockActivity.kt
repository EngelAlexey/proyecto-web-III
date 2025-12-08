package com.example.clocker

import Controller.ClockController
import Controller.PersonController
import Controller.ZoneController
import Entity.Clock
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ClockActivity : AppCompatActivity() {

    private lateinit var TextID: EditText
    private lateinit var TextName: EditText
    private lateinit var imgPhoto: ImageView
    private lateinit var clockController: ClockController
    private lateinit var personController: PersonController
    private lateinit var zoneController: ZoneController
    private lateinit var sessionManager: SessionManager

    private val cameraPreviewLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                imgPhoto.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clock)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        clockController = ClockController(this)
        personController = PersonController(this)
        zoneController = ZoneController(this)

        TextID = findViewById(R.id.TextID)
        TextName = findViewById(R.id.TextName)
        imgPhoto = findViewById(R.id.imgPhoto)

        val btnSelectPhoto = findViewById<ImageButton>(R.id.btnSelectPicture)
        btnSelectPhoto.setOnClickListener { takePhoto() }

        TextID.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                loadPersonName()
            }
        }

        TextID.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadPersonName()
                true
            } else {
                false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSave -> {
                saveClock()
                true
            }
            R.id.btnDelete -> {
                deleteClock()
                true
            }
            R.id.btnCancel -> {
                manejarSalida()
                true
            }
            android.R.id.home -> {
                manejarSalida()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        manejarSalida()
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        manejarSalida()
    }

    private fun manejarSalida() {
        if (sessionManager.esReloj()) {
            mostrarDialogoCerrarSesion()
        } else {
            finish()
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Desea cerrar sesión y volver al inicio?")
            .setPositiveButton("Sí") { _, _ ->
                sessionManager.cerrarSesion()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun clear() {
        TextID.text.clear()
        TextName.setText("")
        imgPhoto.setImageBitmap(null)
    }

    private fun isValidate(): Boolean {
        val drawable = imgPhoto.drawable as? BitmapDrawable
        return TextID.text.isNotBlank() && drawable?.bitmap != null
    }

    private fun takePhoto() {
        cameraPreviewLauncher.launch(null)
    }

    private fun loadPersonName() {
        val idPerson = TextID.text.toString().trim()
        if (idPerson.isBlank()) {
            TextName.setText("")
            return
        }

        personController.getByIdPerson(
            id = idPerson,
            onSuccess = { person ->
                if (person == null) {
                    Toast.makeText(this, R.string.MsgDataNoFound, Toast.LENGTH_LONG).show()
                    TextName.setText("")
                } else {
                    TextName.setText("${person.Name} ${person.FLastName} ${person.SLastName}")
                }
            },
            onFailure = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                TextName.setText("")
            }
        )
    }

    private fun saveClock() {
        val idPerson = TextID.text.toString().trim()
        if (idPerson.isBlank()) {
            Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
            return
        }

        personController.getByIdPerson(
            id = idPerson,
            onSuccess = { person ->
                if (person == null) {
                    Toast.makeText(this, R.string.MsgDataNoFound, Toast.LENGTH_LONG).show()
                    TextName.setText("")
                    return@getByIdPerson
                }

                TextName.setText("${person.Name} ${person.FLastName} ${person.SLastName}")

                if (!isValidate()) {
                    Toast.makeText(this, "Debe ingresar ID y tomar una foto", Toast.LENGTH_LONG).show()
                    return@getByIdPerson
                }

                // ✅ CORREGIDO: Usar runBlocking o coroutine para obtener zona
                lifecycleScope.launch {
                    try {
                        val assignedZone = withContext(Dispatchers.IO) {
                            zoneController.getByCodeZone(person.ZoneCode)
                        }

                        if (assignedZone == null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@ClockActivity, "La persona no tiene una zona válida asignada.", Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }

                        val currentDateTime = LocalDateTime.now()
                        val isScheduleValid = zoneController.isClockValidInZone(assignedZone, currentDateTime)

                        if (!isScheduleValid) {
                            withContext(Dispatchers.Main) {
                                val dayName = currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                                if (!assignedZone.Days.contains(currentDateTime.dayOfWeek)) {
                                    Toast.makeText(this@ClockActivity, "Marca Inválida: El día $dayName no está permitido en esta zona.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@ClockActivity, "Marca Inválida: Fuera del horario permitido (${assignedZone.StartTime.format(timeFormatter)} - ${assignedZone.EndTime.format(timeFormatter)})", Toast.LENGTH_LONG).show()
                                }
                            }
                            return@launch
                        }

                        // Crear Clock
                        val idClock = System.currentTimeMillis().toString()
                        val bitmap = (imgPhoto.drawable as BitmapDrawable).bitmap

                        val clock = Clock(
                            idClock = idClock,
                            idPerson = idPerson,
                            dateClock = currentDateTime,
                            type = "Entrada/Salida",
                            address = "Dispositivo Móvil",
                            latitude = 0,
                            longitude = 0,
                            photo = bitmap
                        )

                        // Guardar con progreso
                        withContext(Dispatchers.Main) {
                            clockController.addClock(
                                clock = clock,
                                onSuccess = {
                                    Toast.makeText(this@ClockActivity, getString(R.string.MsgSave), Toast.LENGTH_LONG).show()
                                    clear()
                                },
                                onFailure = { error ->
                                    Toast.makeText(this@ClockActivity, "Error: $error", Toast.LENGTH_LONG).show()
                                },
                                onProgress = { message ->
                                    Toast.makeText(this@ClockActivity, message, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ClockActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            onFailure = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun deleteClock() {
        val idPerson = TextID.text.toString().trim()
        if (idPerson.isBlank()) {
            Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
            return
        }

        clockController.getByIdPersonClock(
            idPerson = idPerson,
            onSuccess = { clocks ->
                if (clocks.isEmpty()) {
                    Toast.makeText(this, "No hay marcas para esta persona", Toast.LENGTH_LONG).show()
                } else {
                    val lastClock = clocks.first()
                    clockController.removeClock(
                        id = lastClock.IDClock,
                        onSuccess = {
                            Toast.makeText(this, R.string.MsgDelete, Toast.LENGTH_LONG).show()
                            clear()
                        },
                        onFailure = { error ->
                            Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            onFailure = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
        )
    }
}