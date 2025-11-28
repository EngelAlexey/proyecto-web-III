package com.example.clocker

import Controller.PersonController
import Controller.ZoneController
import Entity.Person
import Entity.Zone
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PersonForm : AppCompatActivity() {

    private lateinit var TextId: EditText
    private lateinit var TextName: EditText
    private lateinit var TextFLastName: EditText
    private lateinit var TextSLastName: EditText
    private lateinit var TextNationality: EditText
    private lateinit var TextZoneCode: EditText
    private lateinit var SwitchStatus: Switch
    private lateinit var personController: PersonController
    private lateinit var zoneController: ZoneController
    private var isEditMode: Boolean = false
    private lateinit var btnSearchPerson: ImageButton

    private var zoneList: List<Zone> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.form_person)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        personController = PersonController(this)
        zoneController = ZoneController(this)

        TextId = findViewById(R.id.TextID)
        TextName = findViewById(R.id.TextName)
        TextFLastName = findViewById(R.id.TextType)
        TextSLastName = findViewById(R.id.TextEmail)
        TextNationality = findViewById(R.id.TextNationality)
        TextZoneCode = findViewById(R.id.TextZoneCode)
        SwitchStatus = findViewById(R.id.swStatus)

        setupZonePicker()

        val btnSearchPerson: ImageButton = findViewById(R.id.btnSearchId_user)
        btnSearchPerson.setOnClickListener { searchPerson() }
    }

    private fun setupZonePicker() {
        TextZoneCode.keyListener = null
        TextZoneCode.isFocusable = false
        TextZoneCode.isClickable = true

        TextZoneCode.setOnClickListener {
            showZoneSelectionDialog()
        }
    }

    private fun showZoneSelectionDialog() {
        try {
            zoneList = zoneController.getActiveZones()

            if (zoneList.isEmpty()) {
                Toast.makeText(this, "No hay zonas activas disponibles", Toast.LENGTH_SHORT).show()
                return
            }

            val zoneDisplayArray = zoneList.map { "${it.Code} - ${it.Name}" }.toTypedArray()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Seleccione una Zona")
            builder.setItems(zoneDisplayArray) { _, which ->
                val selectedZone = zoneList[which]
                TextZoneCode.setText(selectedZone.Code)
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar zonas: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSave -> {
                savePerson()
                true
            }
            R.id.btnDelete -> {
                deletePerson()
                true
            }
            R.id.btnCancel -> {
                finish()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun clear() {
        TextId.text.clear()
        TextName.text.clear()
        TextFLastName.text.clear()
        TextSLastName.text.clear()
        TextNationality.text.clear()
        TextZoneCode.text.clear()
        SwitchStatus.isChecked = false
        isEditMode = false
    }

    fun isValidate(): Boolean =
        TextId.text.isNotBlank() &&
                TextName.text.isNotBlank() &&
                TextFLastName.text.isNotBlank() &&
                TextSLastName.text.isNotBlank() &&
                TextNationality.text.isNotBlank() &&
                TextZoneCode.text.isNotBlank() &&
                SwitchStatus.isChecked


    fun searchPerson() {
        try {
            val id = TextId.text.toString().trim()
            if (id.isBlank()) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                return
            }

            val person = personController.getByIdPerson(id)

            if (person == null) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                clear()
            } else {
                TextName.setText(person.Name)
                TextFLastName.setText(person.FLastName)
                TextSLastName.setText(person.SLastName)
                TextNationality.setText(person.Nationality)
                TextZoneCode.setText(person.ZoneCode)
                SwitchStatus.isChecked = person.Status
                isEditMode = true
            }

        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: getString(R.string.ErrorMsgGetById), Toast.LENGTH_LONG).show()
        }
    }


    fun savePerson() {
        try {
            if (isValidate()) {
                val id = TextId.text.toString().trim()
                val existingPerson = personController.getByIdPerson(id)

                if (existingPerson != null && !isEditMode) {
                    Toast.makeText(this, getString(R.string.MsgDuplicateDate), Toast.LENGTH_LONG).show()
                } else {
                    val person = Person().apply {
                        ID = id
                        Name = TextName.text.toString()
                        FLastName = TextFLastName.text.toString()
                        SLastName = TextSLastName.text.toString()
                        Nationality = TextNationality.text.toString()
                        ZoneCode = TextZoneCode.text.toString()
                        Status = SwitchStatus.isChecked
                    }

                    if (isEditMode) {
                        personController.updatePerson(person)
                        Toast.makeText(this, getString(R.string.MsgUpdate), Toast.LENGTH_LONG).show()
                    } else {
                        personController.addPerson(person)
                        Toast.makeText(this, getString(R.string.MsgSave), Toast.LENGTH_LONG).show()
                    }

                    clear()
                }
            } else {
                Toast.makeText(this, R.string.ErrorMsgAdd, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }


    fun deletePerson(){
        try {
            val person = personController.getByIdPerson(TextId.text.toString().trim())
            val id = TextId.text.toString().trim()
            if (id.isBlank()){
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
            } else if (person == null){
                Toast.makeText(this, R.string.ErrorMsgRemove, Toast.LENGTH_LONG).show()
            } else {
                personController.removePerson(person)
            }
            Toast.makeText(this, R.string.MsgDelete, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
        clear()
    }
}