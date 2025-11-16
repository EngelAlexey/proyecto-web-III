package com.example.clocker

import Controller.PersonController
import Controller.UserController
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
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

    }

}