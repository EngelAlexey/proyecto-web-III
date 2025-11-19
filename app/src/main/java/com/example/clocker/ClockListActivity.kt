package com.example.clocker

import Controller.ClockController
import Entity.Clock
import Interface.OnClockItemClickListener
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ClockListActivity : AppCompatActivity(), OnClockItemClickListener {

    private lateinit var customAdapter: ClockListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clock_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recycler = findViewById<RecyclerView>(R.id.rvclock)
        val clockController = ClockController(this)
        val list: List<Clock> = clockController.getAllClock()
        customAdapter = ClockListAdapter(list, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        recycler.layoutManager = layoutManager
        recycler.adapter = customAdapter
        customAdapter.notifyDataSetChanged()

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
    }

    override fun onClockItemClicked(clock: Clock) {
        Toast.makeText(
            this,
            "Persona ${clock.IDPerson} Fecha ${clock.DateClock}",
            Toast.LENGTH_LONG
        ).show()
    }
}
