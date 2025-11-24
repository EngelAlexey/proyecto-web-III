package com.example.clocker

import Controller.ZoneController
import Entity.Zone
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.format.DateTimeFormatter

class ZoneListActivity : AppCompatActivity() {

    private lateinit var zoneController: ZoneController
    private lateinit var listViewZones: ListView
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_zone_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        zoneController = ZoneController(this)
        listViewZones = findViewById(R.id.listViewZones)

        loadZones()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadZones() {
        try {
            val zones = zoneController.getAllZone()
            val zoneDisplayList = zones.map { zone ->
                "${zone.Code} - ${zone.Name} (${zone.StartTime.format(timeFormatter)} - ${zone.EndTime.format(timeFormatter)}) - ${if (zone.Status) "Activa" else "Inactiva"}"
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, zoneDisplayList)
            listViewZones.adapter = adapter

        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: getString(R.string.ErrorMsgGetAll), Toast.LENGTH_LONG).show()
        }
    }
}