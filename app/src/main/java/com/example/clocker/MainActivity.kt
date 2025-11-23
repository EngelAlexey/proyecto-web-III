package com.example.clocker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnPerson: Button = findViewById(R.id.btnPersonForm)
        btnPerson.setOnClickListener {
            val intent = Intent(this, PersonForm::class.java)
            startActivity(intent)
        }

        val btnUser: Button = findViewById(R.id.btnUser)
        btnUser.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        val btnZone: Button = findViewById(R.id.btnZone)
        btnZone.setOnClickListener {
            val intent = Intent(this, ZoneActivity::class.java)
            startActivity(intent)
        }

        val btnZoneList: Button = findViewById(R.id.btnZoneList)
        btnZoneList.setOnClickListener {
            val intent = Intent(this, ZoneListActivity::class.java)
            startActivity(intent)
        }
    }
}