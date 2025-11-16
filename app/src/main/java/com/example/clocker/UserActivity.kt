package com.example.clocker

import Controller.PersonController
import Controller.UserController
import Entity.Person
import Entity.User
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserActivity : AppCompatActivity() {

    private lateinit var TextId: EditText
    private lateinit var TextName: EditText
    private lateinit var CheckTypeClock: CheckBox
    private lateinit var CheckTypeAdmin: CheckBox
    private lateinit var TextEmail: EditText
    private lateinit var TextPassword: EditText
    private lateinit var SwitchStatus: Switch
    private lateinit var userController: UserController
    private var isEditMode: Boolean = false
    private lateinit var btnSearchId_user: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userController = UserController(this)
        TextId = findViewById(R.id.TextID)
        TextName = findViewById(R.id.TextName)
        CheckTypeClock = findViewById(R.id.CheckClock)
        CheckTypeAdmin = findViewById(R.id.CheckAdmin)
        TextEmail = findViewById(R.id.TextEmail)
        TextPassword = findViewById(R.id.TextPassword)
        SwitchStatus = findViewById(R.id.swStatus)

        val btnSearchUser: ImageButton = findViewById(R.id.btnSearchId_user)
        btnSearchUser.setOnClickListener { searchUser() }

        CheckTypeClock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) CheckTypeAdmin.isChecked = false
        }

        CheckTypeAdmin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) CheckTypeClock.isChecked = false
        }
    }
    //Utils
    fun clear() {
        TextId.text.clear()
        TextName.text.clear()
        CheckTypeClock.isChecked = false
        CheckTypeAdmin.isChecked = false
        TextEmail.text.clear()
        TextPassword.text.clear()
        SwitchStatus.isChecked = false
        isEditMode = false
    }

    fun isValidate(): Boolean =
        TextId.text.isNotBlank() &&
                TextName.text.isNotBlank() &&
                (CheckTypeClock.isChecked xor CheckTypeAdmin.isChecked) &&
                TextEmail.text.isNotBlank() &&
                TextPassword.text.isNotBlank() &&
                SwitchStatus.isChecked

    fun searchUser() {
        try {
            val id = TextId.text.toString().trim()
            if (id.isBlank()) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                return
            }

            val user = userController.getByIdUser(id)

            if (user == null) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                clear()
            } else {
                TextName.setText(user.Name)
                CheckTypeClock.isChecked = user.Type == "Clock"
                CheckTypeAdmin.isChecked = user.Type == "Admin"
                TextEmail.setText(user.Email)
                TextPassword.setText(user.Password)
                SwitchStatus.isChecked = user.Status
                isEditMode = true
            }

        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: getString(R.string.ErrorMsgGetById), Toast.LENGTH_LONG).show()
        }
    }

    fun saveUser() {
        try {
            if (isValidate()) {
                val id = TextId.text.toString().trim()
                val existingUser = userController.getByIdUser(id)

                if (existingUser != null && !isEditMode) {
                    Toast.makeText(this, getString(R.string.MsgDuplicateDate), Toast.LENGTH_LONG).show()
                } else {
                    val user = User().apply {
                        ID = id
                        Name = TextName.text.toString()
                        Type = if (CheckTypeClock.isChecked) "Clock" else "Admin"
                        Email = TextEmail.text.toString()
                        Password = TextPassword.text.toString()
                        Status = SwitchStatus.isChecked
                    }

                    if (isEditMode) {
                        userController.updateUser(user)
                        Toast.makeText(this, getString(R.string.MsgUpdate), Toast.LENGTH_LONG).show()
                    } else {
                        userController.addUser(user)
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
            val user = userController.getByIdUser(TextId.text.toString().trim())
            val id = TextId.text.toString().trim()
            if (id.isBlank()){
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
            } else if (user == null){
                Toast.makeText(this, R.string.ErrorMsgRemove, Toast.LENGTH_LONG).show()
            } else {
                userController.removeUser(user)
            }
            Toast.makeText(this, R.string.MsgDelete, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
        clear()
    }

}