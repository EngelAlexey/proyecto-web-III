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

        val btnStaff: Button = findViewById(R.id.btnPersonForm)
        btnStaff.setOnClickListener {
            val intent = Intent(this, PersonForm::class.java)
            startActivity(intent)
        }

        val btnClocker: Button = findViewById(R.id.btnClockForm)
        btnClocker.setOnClickListener {
            val intent = Intent(this, ClockActivity::class.java)
            startActivity(intent)

    }
}
}