package com.example.clocker

import Controller.PersonController
import Entity.Person
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.form_person)

        personController = PersonController(this)
        TextId = findViewById(R.id.TextID)
        TextName = findViewById(R.id.TextName)
        TextFLastName = findViewById(R.id.TextFLastName)
        TextSLastName = findViewById(R.id.TextSLastName)
        TextNationality = findViewById(R.id.TextNationality)
        SwitchStatus = findViewById(R.id.swStatus)

        val btnSave: Button = findViewById(R.id.btnAddPerson)
        btnSave.setOnClickListener { savePerson() }

        val btnBack: Button = findViewById(R.id.btnBackNewPerson)
        btnBack.setOnClickListener { finish() }
    }

    fun isValidate(): Boolean =
         TextId.text.isNotBlank() &&
         TextName.text.isNotBlank() &&
         TextFLastName.text.isNotBlank() &&
         TextSLastName.text.isNotBlank() &&
         TextNationality.text.isNotBlank() &&
         SwitchStatus.isChecked

    fun savePerson(){
        fun savePerson() {
            try {
                if (isValidate()) {
                    if (personController.getByIdPerson(TextId.text.toString().trim()) != null /*&& !isEditMode*/) {
                        Toast.makeText(this, getString(R.string.MsgDuplicateDate), Toast.LENGTH_LONG).show()
                    } else {
                        val person = Person()
                        person.ID = TextId.text.toString()
                        person.Name = TextName.text.toString()
                        person.FLastName = TextFLastName.text.toString()
                        person.SLastName = TextSLastName.text.toString()
                        person.Nationality = TextNationality.text.toString()
                        person.Status = SwitchStatus.isChecked
                    }

                    Toast.makeText(this, R.string.MsgSave, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, R.string.ErrorMsgAdd, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }
}
