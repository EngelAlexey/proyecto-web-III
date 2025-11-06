package com.example.clocker

import Controller.PersonController
import Entity.Person
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class PersonForm : AppCompatActivity() {

    private lateinit var TextId: EditText
    private lateinit var TextName: EditText
    private lateinit var TextFLastName: EditText
    private lateinit var TextSLastName: EditText
    private lateinit var TextNationality: EditText
    private lateinit var SwitchStatus: Switch
    private lateinit var personController: PersonController
    private var isEditMode: Boolean = false
    private lateinit var btnSearchPerson: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.form_person)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        personController = PersonController(this)
        TextId = findViewById(R.id.TextID)
        TextName = findViewById(R.id.TextName)
        TextFLastName = findViewById(R.id.TextFLastName)
        TextSLastName = findViewById(R.id.TextSLastName)
        TextNationality = findViewById(R.id.TextNationality)
        SwitchStatus = findViewById(R.id.swStatus)


      /*
        val btnSave: Button = findViewById(R.id.btnAddPerson)
        btnSave.setOnClickListener { savePerson() }

        val btnDelete: Button = findViewById(R.id.btnDeletePerson)
       */


       /* val btnBack: Button = findViewById(R.id.btnBackNewPerson)
        btnBack.setOnClickListener { finish() }*/

        val btnSearchPerson: ImageButton = findViewById(R.id.btnSearchId_person)
        btnSearchPerson.setOnClickListener { searchPerson() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSavePerson -> {
                savePerson()
                true
            }
            R.id.btnDeletePerson -> {
                deletePerson()
                true
            }
            R.id.btnCancel -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun clear() {
        TextId.text.clear()
        TextName.text.clear()
        TextFLastName.text.clear()
        TextSLastName.text.clear()
        TextNationality.text.clear()
        SwitchStatus.isChecked = false
        isEditMode = false
    }
    fun isValidate(): Boolean =
         TextId.text.isNotBlank() &&
         TextName.text.isNotBlank() &&
         TextFLastName.text.isNotBlank() &&
         TextSLastName.text.isNotBlank() &&
         TextNationality.text.isNotBlank() &&
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
                SwitchStatus.isChecked = person.Status
                isEditMode = true
            }

        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "Error al buscar persona", Toast.LENGTH_LONG).show()
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
