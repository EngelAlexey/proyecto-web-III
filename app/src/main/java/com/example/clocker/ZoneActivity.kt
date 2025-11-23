package com.example.clocker

import Controller.ZoneController
import Entity.Zone
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ZoneActivity : AppCompatActivity() {

    private lateinit var TextId: EditText
    private lateinit var TextCode: EditText
    private lateinit var TextName: EditText
    private lateinit var TextDescription: EditText
    private lateinit var TextStartTime: EditText
    private lateinit var TextEndTime: EditText
    private lateinit var SwitchStatus: Switch
    private lateinit var zoneController: ZoneController
    private var isEditMode: Boolean = false
    private lateinit var btnSearchZone: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_zone)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        zoneController = ZoneController(this)
        TextId = findViewById(R.id.TextID)
        TextCode = findViewById(R.id.TextCode)
        TextName = findViewById(R.id.TextName)
        TextDescription = findViewById(R.id.TextDescription)
        TextStartTime = findViewById(R.id.TextStartTime)
        TextEndTime = findViewById(R.id.TextEndTime)
        SwitchStatus = findViewById(R.id.swStatus)

        val btnSearchZone: ImageButton = findViewById(R.id.btnSearchId_zone)
        btnSearchZone.setOnClickListener { searchZone() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnSave -> {
                saveZone()
                true
            }
            R.id.btnDelete -> {
                deleteZone()
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
        TextCode.text.clear()
        TextName.text.clear()
        TextDescription.text.clear()
        TextStartTime.text.clear()
        TextEndTime.text.clear()
        SwitchStatus.isChecked = false
        isEditMode = false
    }

    fun isValidate(): Boolean =
        TextId.text.isNotBlank() &&
                TextCode.text.isNotBlank() &&
                TextName.text.isNotBlank() &&
                TextDescription.text.isNotBlank() &&
                TextStartTime.text.isNotBlank() &&
                TextEndTime.text.isNotBlank() &&
                SwitchStatus.isChecked

    fun searchZone() {
        try {
            val id = TextId.text.toString().trim()
            if (id.isBlank()) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                return
            }

            val zone = zoneController.getByIdZone(id)

            if (zone == null) {
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
                clear()
            } else {
                TextCode.setText(zone.Code)
                TextName.setText(zone.Name)
                TextDescription.setText(zone.Description)
                TextStartTime.setText(zone.StartTime)
                TextEndTime.setText(zone.EndTime)
                SwitchStatus.isChecked = zone.Status
                isEditMode = true
            }

        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: getString(R.string.ErrorMsgGetById), Toast.LENGTH_LONG).show()
        }
    }

    fun saveZone() {
        try {
            if (isValidate()) {
                val id = TextId.text.toString().trim()
                val existingZone = zoneController.getByIdZone(id)

                if (existingZone != null && !isEditMode) {
                    Toast.makeText(this, getString(R.string.MsgDuplicateDate), Toast.LENGTH_LONG).show()
                } else {
                    val zone = Zone().apply {
                        ID = id
                        Code = TextCode.text.toString()
                        Name = TextName.text.toString()
                        Description = TextDescription.text.toString()
                        StartTime = TextStartTime.text.toString()
                        EndTime = TextEndTime.text.toString()
                        Status = SwitchStatus.isChecked
                    }

                    if (isEditMode) {
                        zoneController.updateZone(zone)
                        Toast.makeText(this, getString(R.string.MsgUpdate), Toast.LENGTH_LONG).show()
                    } else {
                        zoneController.addZone(zone)
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

    fun deleteZone(){
        try {
            val zone = zoneController.getByIdZone(TextId.text.toString().trim())
            val id = TextId.text.toString().trim()
            if (id.isBlank()){
                Toast.makeText(this, R.string.ErrorMsgGetById, Toast.LENGTH_LONG).show()
            } else if (zone == null){
                Toast.makeText(this, R.string.ErrorMsgRemove, Toast.LENGTH_LONG).show()
            } else {
                zoneController.removeZone(id)
            }
            Toast.makeText(this, R.string.MsgDelete, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
        clear()
    }
}