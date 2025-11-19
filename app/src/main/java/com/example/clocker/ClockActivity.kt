package com.example.clocker

import Controller.ClockController
import Controller.PersonController
import Entity.Clock
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class ClockActivity : AppCompatActivity() {

    private lateinit var TextID: EditText
    private lateinit var TextName: EditText
    private lateinit var imgPhoto: ImageView
    private lateinit var clockController: ClockController
    private lateinit var personController: PersonController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clock)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        clockController = ClockController(this)
        personController = PersonController(this)

        TextID = findViewById(R.id.TextID)
        TextName = findViewById(R.id.TextName)
        imgPhoto = findViewById(R.id.imgPhoto)

        val btnSelectPhoto = findViewById<ImageButton>(R.id.btnSelectPicture)
        btnSelectPhoto.setOnClickListener { takePhoto() }
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
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private val cameraPreviewLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                imgPhoto.setImageBitmap(bitmap)
            }
        }

    private fun takePhoto() {
        cameraPreviewLauncher.launch(null)
    }

    private fun saveClock() {
        try {
            if (!isValidate()) {
                Toast.makeText(this, R.string.ErrorMsgAdd, Toast.LENGTH_LONG).show()
                return
            }

            val idPerson = TextID.text.toString().trim()
            val person = personController.getByIdPerson(idPerson)

            if (person == null) {
                Toast.makeText(this, R.string.MsgDataNoFound, Toast.LENGTH_LONG).show()
                TextName.setText("")
                return
            } else {
                TextName.setText("${person.Name} ${person.FLastName} ${person.SLastName}")
            }

            val idClock = System.currentTimeMillis().toString()

            val bitmap = (imgPhoto.drawable as BitmapDrawable).bitmap
            val dateClock = LocalDate.now()
            val type = ""
            val address = ""
            val latitude = 0
            val longitude = 0

            val clock = Clock(
                idClock = idClock,
                idPerson = idPerson,
                dateClock = dateClock,
                type = type,
                address = address,
                latitude = latitude,
                longitude = longitude,
                photo = bitmap
            )

            clockController.addClock(clock)
            Toast.makeText(this, getString(R.string.MsgSave), Toast.LENGTH_LONG).show()
            clear()
        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteClock() {
        try {
            val idPerson = TextID.text.toString().trim()
            if (idPerson.isBlank()) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                return
            }

            val clock = clockController.getByIdPersonClock(idPerson)
            if (clock == null) {
                Toast.makeText(this, R.string.ErrorMsgRemove, Toast.LENGTH_LONG).show()
            } else {
                clockController.removeClock(clock)
                Toast.makeText(this, R.string.MsgDelete, Toast.LENGTH_LONG).show()
                clear()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "", Toast.LENGTH_LONG).show()
        }
    }
}
